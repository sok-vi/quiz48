/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.backup;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import quiz48.AppProperties;
import quiz48.Pointer;
import quiz48.db.ConnectDB;
import quiz48.gui.LoadingWindow;

/**
 * формат бэкапа <br />
 * 1--------------<br />
 * byte - флаг, указывает собержимое<br />
 * 2--------------<br />
 * заголовок блока<br />
 * byte - тип блока<br />
 * int - длина блока<br />
 * int - количество элементов блока<br />
 *
 * @author vasya
 */
public final class Backup {
    private static final int DB_PAGE_SIZE = 2;
    
    private static final byte 
            STORE_QUIZ = 1,
            STORE_USERS = 2,
            STORE_RESULTS = 4,
            STORE_CONTENT = 8;
    
    private static final byte
            TYPE_QUIZ = 1,
            TYPE_QUERY = 2,
            TYPE_ANSWER = 3,
            TYPE_USER = 4,
            TYPE_QUIZ_RESULT = 5,
            TYPE_QUERY_RESULT = 6,
            TYPE_CONTENT = 7;
    
    private static final byte
            FS_ENTITY_TYPE_DIR = 1,
            FS_ENTITY_TYPE_FILE = 2;
    
    private static class BlockTitle {
        public byte type;
        public int length, count;
    }
    
    private static class StoreBlockInfo extends BlockTitle {
        public File tmpFile;
    }
    
    private static int getRecordCount(ConnectDB conn, String table) throws SQLException {
        Pointer<Integer> count = new Pointer<>(0);
        
        conn.executeQuery((s) -> {
            ResultSet rs = s.executeQuery();
            if(rs.next()) {
                count.put(rs.getInt("CNT"));
            }
            else {
                throw new SQLException("fail");
            }
        }, String.format("SELECT COUNT(*) AS CNT FROM %1$s", table));
        
        return count.get();
    }
    
    private static int getPageCount(int recordCount) {
        int page_count = 0;
        if(recordCount > 0) {
            page_count = recordCount / DB_PAGE_SIZE;
            if((page_count * DB_PAGE_SIZE) < recordCount) { ++page_count; }
        }
        
        return page_count;
    }
    
    private static void storeUsers(ConnectDB conn, StoreBlockInfo sbi, DataOutputStream dto) throws SQLException {
        Pointer<Integer> count = new Pointer<>(getRecordCount(conn, "users"));
        int page_count = getPageCount(count.get());
        count.put(0);
        Pointer<Integer> page = new Pointer<>();
        
        for(int i = 0; i < page_count; ++i) {
            page.put(i);
            conn.executeQuery((s) -> {
                s.setInt(1, page.get() * DB_PAGE_SIZE);
                s.setInt(2, DB_PAGE_SIZE);
                ResultSet rs = s.executeQuery();
                while(rs.next()) {
                    try {
                        dto.writeUTF(rs.getString("login"));
                        dto.writeUTF(rs.getString("name"));
                        dto.writeUTF(rs.getString("pwd"));
                        dto.writeInt(rs.getInt("is_admin"));
                    } catch (IOException ex) {
                        throw new SQLException(ex);
                    }
                    count.put(count.get() + 1);
                }
            }, "SELECT * FROM users OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        }
        
        sbi.count = count.get();
    }
    
    private static void storeAnswers(ConnectDB conn, StoreBlockInfo sbi, DataOutputStream dto, int query_id) throws SQLException {
        Pointer<Integer> count = new Pointer<>(
                getRecordCount(
                        conn, 
                        String.format("answer WHERE query_id=%1$d", query_id)
                )
        );
        int page_count = getPageCount(count.get());
        count.put(0);
        Pointer<Integer> page = new Pointer<>();
        
        for(int i = 0; i < page_count; ++i) {
            page.put(i);
            conn.executeQuery((s) -> {
                s.setInt(1, query_id);
                s.setInt(2, page.get() * DB_PAGE_SIZE);
                s.setInt(3, DB_PAGE_SIZE);
                ResultSet rs = s.executeQuery();
                while(rs.next()) {
                    try {
                        dto.writeUTF(rs.getString("answer"));
                    } catch (IOException ex) {
                        throw new SQLException(ex);
                    }
                    count.put(count.get() + 1);
                }
            }, "SELECT * FROM answer WHERE query_id=? OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        }
        
        sbi.count = count.get();
    }
    
    private static void storeQuerys(ConnectDB conn, StoreBlockInfo sbi, FileOutputStream fs, DataOutputStream dto, int quiz_id) throws SQLException {
        /**
         * сделаем допущение
         * будем считать, что к одной викторине привязано не слишком много вопросов
         * и при восстановление нам хватит памяти сделать мап увязвающий реальные id вопросов с сохранёнными в бэкапе
         */
        Pointer<Integer> count = new Pointer<>(
                getRecordCount(
                        conn, 
                        String.format("query WHERE quiz_id=%1$d", quiz_id)
                )
        );
        int page_count = getPageCount(count.get());
        count.put(0);
        Pointer<Integer> page = new Pointer<>();
        
        for(int i = 0; i < page_count; ++i) {
            page.put(i);
            conn.executeQuery((s) -> {
                s.setInt(1, quiz_id);
                s.setInt(2, page.get() * DB_PAGE_SIZE);
                s.setInt(3, DB_PAGE_SIZE);
                ResultSet rs = s.executeQuery();
                while(rs.next()) {
                    try {
                        dto.writeInt(rs.getInt("id"));
                        dto.writeUTF(rs.getString("query"));
                        dto.writeUTF(rs.getString("answer"));
                        dto.writeInt(rs.getInt("is_visible_answer_in_result"));
                        dto.writeInt(rs.getInt("sort"));
                        dto.writeInt(rs.getInt("time"));
                        dto.writeInt(rs.getInt("is_fix"));
                        dto.writeInt(rs.getInt("repeat"));
                        dto.writeInt(rs.getInt("ext"));
                        dto.writeInt(rs.getInt("weight"));
                        
                        if(rs.getInt("is_fix") != 0) {
                            //сохранение ответов на вопросы
                            StoreBlockInfo info = new StoreBlockInfo();
                            info.tmpFile = new File(String.format("%1$s.%2$d.tmp", sbi.tmpFile.getAbsolutePath(), (int)System.currentTimeMillis()));
                            if(info.tmpFile.exists()) {
                                throw new IOException(String.format("Временый файл {%1$s} уже существует", info.tmpFile.getAbsolutePath()));
                            }

                            if(!info.tmpFile.createNewFile()) {
                                throw new IOException(String.format("Не удалось создать временный файл %1$s", info.tmpFile.getAbsolutePath()));
                            }
                            //сохранияем вопросы
                            try(FileOutputStream tmpfs = new FileOutputStream(info.tmpFile)) {
                                try(DataOutputStream tmpdto = new DataOutputStream(tmpfs)) {
                                    storeAnswers(conn, info, tmpdto, rs.getInt("id"));
                                }
                            }


                            info.length = (int)info.tmpFile.length();
                            info.type = TYPE_ANSWER;
                            storeBlockInfo(info, dto);//запись информации о блоке
                            copyTmpFile(fs, info.tmpFile);//копируем вопросы

                            if(!info.tmpFile.delete()) {
                                throw new IOException(String.format("Не удалось удалить временный файл %1$s", info.tmpFile.getAbsolutePath()));
                            }
                        }
                        
                    } catch (IOException ex) {
                        throw new SQLException(ex);
                    }
                    count.put(count.get() + 1);
                }
            }, "SELECT * FROM query WHERE quiz_id=? OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        }
        
        sbi.count = count.get();
    }
    
    private static void storeQueryResults(ConnectDB conn, StoreBlockInfo sbi, DataOutputStream dto, int quiz_result_id) throws SQLException {
        Pointer<Integer> count = new Pointer<>(
                getRecordCount(
                        conn, 
                        String.format("query_result WHERE quiz_result_id=%1$d", quiz_result_id)
                )
        );
        int page_count = getPageCount(count.get());
        count.put(0);
        Pointer<Integer> page = new Pointer<>();
    
        for(int i = 0; i < page_count; ++i) {
            page.put(i);
            conn.executeQuery((s) -> {
                s.setInt(1, quiz_result_id);
                s.setInt(2, page.get() * DB_PAGE_SIZE);
                s.setInt(3, DB_PAGE_SIZE);
                ResultSet rs = s.executeQuery();
                while(rs.next()) {
                    try {
                        dto.writeInt(rs.getInt("query_id"));
                        dto.writeUTF(rs.getString("answer"));
                        dto.writeInt(rs.getInt("time"));
                        dto.writeInt(rs.getInt("fail"));
                        dto.writeInt(rs.getInt("duplicate"));
                    } catch (IOException ex) {
                        throw new SQLException(ex);
                    }
                    count.put(count.get() + 1);
                }
            }, "SELECT * FROM query_result WHERE quiz_result_id=? OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        }
        
        sbi.count = count.get();
    }
    
    private static void storeQuizResults(ConnectDB conn, StoreBlockInfo sbi, FileOutputStream fs, DataOutputStream dto, int quiz_id) throws SQLException {
        Pointer<Integer> count = new Pointer<>(
                getRecordCount(
                        conn, 
                        String.format("quiz_result WHERE quiz_id=%1$d", quiz_id)
                )
        );
        int page_count = getPageCount(count.get());
        count.put(0);
        Pointer<Integer> page = new Pointer<>();
        
        for(int i = 0; i < page_count; ++i) {
            page.put(i);
            conn.executeQuery((s) -> {
                s.setInt(1, quiz_id);
                s.setInt(2, page.get() * DB_PAGE_SIZE);
                s.setInt(3, DB_PAGE_SIZE);
                ResultSet rs = s.executeQuery();
                while(rs.next()) {
                    try {
                        dto.writeUTF(rs.getString("login"));
                        dto.writeInt(rs.getInt("status"));
                        dto.writeLong(rs.getTimestamp("date").getTime());
                        dto.writeInt(rs.getInt("time"));
                        dto.writeInt(rs.getInt("duplicate"));
                        
                        //сохранение ответов на вопросы
                        StoreBlockInfo info = new StoreBlockInfo();
                        info.tmpFile = new File(String.format("%1$s.%2$d.tmp", sbi.tmpFile.getAbsolutePath(), (int)System.currentTimeMillis()));
                        if(info.tmpFile.exists()) {
                            throw new IOException(String.format("Временый файл {%1$s} уже существует", info.tmpFile.getAbsolutePath()));
                        }
                        
                        if(!info.tmpFile.createNewFile()) {
                            throw new IOException(String.format("Не удалось создать временный файл %1$s", info.tmpFile.getAbsolutePath()));
                        }
                        //сохранияем вопросы
                        try(FileOutputStream tmpfs = new FileOutputStream(info.tmpFile)) {
                            try(DataOutputStream tmpdto = new DataOutputStream(tmpfs)) {
                                storeQueryResults(conn, info, tmpdto, rs.getInt("id"));
                            }
                        }
                        

                        info.length = (int)info.tmpFile.length();
                        info.type = TYPE_QUERY_RESULT;
                        storeBlockInfo(info, dto);//запись информации о блоке
                        copyTmpFile(fs, info.tmpFile);//копируем вопросы
                        
                        if(!info.tmpFile.delete()) {
                            throw new IOException(String.format("Не удалось удалить временный файл %1$s", info.tmpFile.getAbsolutePath()));
                        }
                        
                    } catch (IOException ex) {
                        throw new SQLException(ex);
                    }
                    count.put(count.get() + 1);
                }
            }, "SELECT qr.id AS id, qr.status AS status, qr.date AS date, qr.time AS time, qr.duplicate AS duplicate, u.login AS login"
                    + " FROM quiz_result qr INNER JOIN users u ON u.id=qr.user_id WHERE qr.quiz_id=? OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        }
        
        sbi.count = count.get();
    }
    
    private static void storeQuizs(ConnectDB conn, StoreBlockInfo sbi, FileOutputStream fs, DataOutputStream dto, boolean result) throws SQLException {
        Pointer<Integer> count = new Pointer<>(getRecordCount(conn, "quiz"));
        int page_count = getPageCount(count.get());
        count.put(0);
        Pointer<Integer> page = new Pointer<>();
        
        for(int i = 0; i < page_count; ++i) {
            page.put(i);
            conn.executeQuery((s) -> {
                s.setInt(1, page.get() * DB_PAGE_SIZE);
                s.setInt(2, DB_PAGE_SIZE);
                ResultSet rs = s.executeQuery();
                while(rs.next()) {
                    try {
                        dto.writeUTF(rs.getString("name"));
                        dto.writeInt(rs.getInt("is_fix"));
                        dto.writeInt(rs.getInt("count"));
                        dto.writeInt(rs.getInt("time"));
                        dto.writeInt(rs.getInt("sort"));
                        dto.writeInt(rs.getInt("level"));
                        dto.writeInt(rs.getInt("repeat"));
                        
                        //сохранение вопросов
                        StoreBlockInfo info = new StoreBlockInfo();
                        info.tmpFile = new File(String.format("%1$s.%2$d.tmp", sbi.tmpFile.getAbsolutePath(), (int)System.currentTimeMillis()));
                        if(info.tmpFile.exists()) {
                            throw new IOException(String.format("Временый файл {%1$s} уже существует", info.tmpFile.getAbsolutePath()));
                        }
                        
                        if(!info.tmpFile.createNewFile()) {
                            throw new IOException(String.format("Не удалось создать временный файл %1$s", info.tmpFile.getAbsolutePath()));
                        }
                        //сохранияем вопросы
                        try(FileOutputStream tmpfs = new FileOutputStream(info.tmpFile)) {
                            try(DataOutputStream tmpdto = new DataOutputStream(tmpfs)) {
                                storeQuerys(conn, info, tmpfs, tmpdto, rs.getInt("id"));
                            }
                        }
                        

                        info.length = (int)info.tmpFile.length();
                        info.type = TYPE_QUERY;
                        storeBlockInfo(info, dto);//запись информации о блоке
                        copyTmpFile(fs, info.tmpFile);//копируем вопросы
                        
                        if(!info.tmpFile.delete()) {
                            throw new IOException(String.format("Не удалось удалить временный файл %1$s", info.tmpFile.getAbsolutePath()));
                        }
                        
                        //сохранение результатов
                        if(result) {
                            if(!info.tmpFile.createNewFile()) {
                                throw new IOException(String.format("Не удалось создать временный файл %1$s", info.tmpFile.getAbsolutePath()));
                            }
                            
                            //сохраним результаты тестов
                            try(FileOutputStream tmpfs = new FileOutputStream(info.tmpFile)) {
                                try(DataOutputStream tmpdto = new DataOutputStream(tmpfs)) {
                                    storeQuizResults(conn, info, tmpfs, tmpdto, rs.getInt("id"));
                                }
                            }
                            
                            info.length = (int)info.tmpFile.length();
                            info.type = TYPE_QUIZ_RESULT;
                            storeBlockInfo(info, dto);//запись информации о блоке
                            copyTmpFile(fs, info.tmpFile);//копируем тестов
                            
                            if(!info.tmpFile.delete()) {
                                throw new IOException(String.format("Не удалось удалить временный файл %1$s", info.tmpFile.getAbsolutePath()));
                            }
                        }
                    } catch (IOException ex) {
                        throw new SQLException(ex);
                    }
                    count.put(count.get() + 1);
                }
            }, "SELECT * FROM quiz OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        }
        
        sbi.count = count.get();
    }
    
    private static void storeContent(File f, FileOutputStream fs, DataOutputStream dto, int level, StoreBlockInfo sbi) throws IOException {
        if(f.isDirectory()) {
            if(level > 0) {
                ++sbi.count;
                dto.writeByte(FS_ENTITY_TYPE_DIR);
                dto.writeInt(level);
                dto.writeInt(0);
                dto.writeUTF(f.getName());
            }
            File[] list = f.listFiles();
            for(File cf : list) {
                storeContent(cf, fs, dto, level + (cf.isDirectory() ? 1 : 0), sbi);
            }
        }
        else {
            ++sbi.count;
            dto.writeByte(FS_ENTITY_TYPE_FILE);
            dto.writeInt(level);
            dto.writeInt((int)f.length());//будем считать , что нет очень больших файлов в контенте
            dto.writeUTF(f.getName());
            copyTmpFile(fs, f);
        }
    }
    
    private static void storeBlockInfo(BlockTitle bt, DataOutputStream dto) throws IOException {
        dto.writeByte(bt.type);
        dto.writeInt(bt.length);
        dto.writeInt(bt.count);
    }
    
    private static void copyTmpFile(FileOutputStream fs, File tmpFile) throws FileNotFoundException, IOException {
        byte[] buffer = new byte[1024];
        try(FileInputStream fsi = new FileInputStream(tmpFile)) {
            while(true) {
                int read_bytes = fsi.read(buffer);
                if(read_bytes > 0) { fs.write(buffer, 0, read_bytes); }
                if(read_bytes < buffer.length) { break; }
            }
        }
    }
    
    public static void storeBackup(
            ConnectDB conn, 
            /*вопросы обязательно*/
            boolean user, 
            boolean result,
            boolean content,
            String backupPath,
            String contentPath,
            LoadingWindow.Callback cb) throws FileNotFoundException, IOException, SQLException {
        
        if(!(new File(contentPath)).exists()) {
            throw new IOException(String.format("Отсутствует папка с контентом %1$s", contentPath));
        }
        
        File bf = new File(backupPath);
        if(!bf.exists()) { 
            if(!bf.createNewFile()) {
                throw new IOException(String.format("Не удалось создать создать файл %1$s", backupPath));
            }
        }
        else {
            if(!bf.delete()) {
                throw new IOException(String.format("Не удалось перезаписать файл %1$s", backupPath));
            }
            
            if(!bf.createNewFile()) {
                throw new IOException(String.format("Не удалось перезаписать файл %1$s", backupPath));
            }
        }
        
        try(FileOutputStream fs = new FileOutputStream(bf)) {
            try(DataOutputStream dto = new DataOutputStream(fs)) {
                dto.writeByte(
                        STORE_QUIZ | 
                                (user ? STORE_USERS : 0) |
                                (result ? STORE_RESULTS : 0) |
                                (content ? STORE_CONTENT : 0));
                
                StoreBlockInfo info = new StoreBlockInfo();
                info.tmpFile = new File(String.format("%1$s.%2$d.tmp", backupPath, (int)System.currentTimeMillis()));
                
                if(info.tmpFile.exists()) {
                    throw new IOException(String.format("Временый файл {%1$s} уже существует", info.tmpFile.getAbsolutePath()));
                }
                
                if(user) {
                    
                    if(!info.tmpFile.createNewFile()) {
                        throw new IOException(String.format("Не удалось создать временный файл %1$s", info.tmpFile.getAbsolutePath()));
                    }
                
                    //сохраняем юзеров во временный файл
                    try(FileOutputStream tmpfs = new FileOutputStream(info.tmpFile)) {
                        try(DataOutputStream tmpdto = new DataOutputStream(tmpfs)) {
                            storeUsers(conn, info, tmpdto);
                        }
                    }

                    info.length = (int)info.tmpFile.length();
                    info.type = TYPE_USER;
                    storeBlockInfo(info, dto);//запись информации о блоке
                    copyTmpFile(fs, info.tmpFile);//копируем узеров

                    if(!info.tmpFile.delete()) {
                        throw new IOException(String.format("Не удалось удалить временный создать файл %1$s", info.tmpFile.getAbsolutePath()));
                    }
                }
                    
                
                if(!info.tmpFile.createNewFile()) {
                    throw new IOException(String.format("Не удалось создать временный файл %1$s", info.tmpFile.getAbsolutePath()));
                }
                
                //сохраняем викторины
                try(FileOutputStream tmpfs = new FileOutputStream(info.tmpFile)) {
                    try(DataOutputStream tmpdto = new DataOutputStream(tmpfs)) {
                        storeQuizs(conn, info, tmpfs, tmpdto, result);
                    }
                }

                info.length = (int)info.tmpFile.length();
                info.type = TYPE_QUIZ;
                storeBlockInfo(info, dto);//запись информации о блоке
                copyTmpFile(fs, info.tmpFile);//копируем узеров

                if(!info.tmpFile.delete()) {
                    throw new IOException(String.format("Не удалось удалить временный файл %1$s", info.tmpFile.getAbsolutePath()));
                }
                
                if(content) {
                
                    if(!info.tmpFile.createNewFile()) {
                        throw new IOException(String.format("Не удалось создать временный файл %1$s", info.tmpFile.getAbsolutePath()));
                    }

                    //сохраняем конент
                    try(FileOutputStream tmpfs = new FileOutputStream(info.tmpFile)) {
                        try(DataOutputStream tmpdto = new DataOutputStream(tmpfs)) {
                            info.count = 0;
                            storeContent(new File(contentPath), tmpfs, tmpdto, 0, info);
                        }
                    }

                    info.length = (int)info.tmpFile.length();
                    info.type = TYPE_CONTENT;
                    storeBlockInfo(info, dto);//запись информации о блоке
                    copyTmpFile(fs, info.tmpFile);//копируем узеров

                    if(!info.tmpFile.delete()) {
                        throw new IOException(String.format("Не удалось удалить временный файл %1$s", info.tmpFile.getAbsolutePath()));
                    }
                }
            }
        }
    }
    
    private static BlockTitle loadBlockInfo(DataInputStream dti) throws IOException {
        BlockTitle bt = new BlockTitle();
        
        bt.type = dti.readByte();
        bt.length = dti.readInt();
        bt.count = dti.readInt();
        
        return bt;
    }
    
    
    /*quiz result
    
                            dto.writeUTF(rs.getString("login"));
                        dto.writeInt(rs.getInt("status"));
                        dto.writeLong(rs.getTimestamp("date").getTime());
                        dto.writeInt(rs.getInt("time"));
                        dto.writeInt(rs.getInt("duplicate"));

    
    query result
    
                            dto.writeInt(rs.getInt("query_id"));
                        dto.writeUTF(rs.getString("answer"));
                        dto.writeInt(rs.getInt("time"));
                        dto.writeInt(rs.getInt("fail"));
                        dto.writeInt(rs.getInt("duplicate"));

    
    */
    
    private static void loadQueryResults(            
            ConnectDB conn, 
            DataInputStream dti, 
            HashMap<Integer, Integer> old_key_map,
            BlockTitle bt,
            int quiz_result_id) throws IOException, SQLException {
        
        for(int i = 0; i < bt.count; ++i) {
            int query_id = dti.readInt();
            if(!old_key_map.containsKey(query_id)) { throw new SQLException("fail query id"); }
            int new_query_id = old_key_map.get(query_id);
            String answer = dti.readUTF();
            int time = dti.readInt(),
                    fail = dti.readInt(),
                    duplicate = dti.readInt();
            
            conn.executeQuery((s) -> {
                s.setInt(1, quiz_result_id);
                s.setInt(2, new_query_id);
                s.setString(3, answer);
                s.setInt(4, time);
                s.setInt(5, fail);
                s.setInt(6, duplicate);
                s.executeUpdate();
            }, "INSERT INTO query_result (quiz_result_id, query_id, answer, time, fail, duplicate) VALUES (?, ?, ?, ?, ?, ?)");
        }
    }
    
    private static void loadResults(            
            ConnectDB conn, 
            DataInputStream dti, 
            HashMap<Integer, Integer> old_key_map,
            int quiz_id,
            BlockTitle bt,
            boolean skip_results_not_found_users) throws IOException, SQLException {
        
        for(int i = 0; i < bt.count; ++i) {
            String login = dti.readUTF();
            int status = dti.readInt();
            Timestamp dt = new Timestamp(dti.readLong());
            int //fail = dti.readInt(),
                    time = dti.readInt(),
                    duplicate = dti.readInt();
            Pointer<Integer> user_id = new Pointer<>(),
                    new_quiz_result_id = new Pointer<>();
            Pointer<Boolean> query_user_fail = new Pointer<>(false);
            
            conn.executeQuery((s) -> {
                s.setString(1, login);
                ResultSet rs = s.executeQuery();
                if(rs.next()) {
                    user_id.put(rs.getInt("id"));
                }
                else {
                    if(!skip_results_not_found_users) {
                        throw new SQLException("fail link result to user");
                    }
                    else {
                        query_user_fail.put(true);
                    }
                }
            }, "SELECT id FROM users WHERE login=?");
            
            if(!query_user_fail.get()) {
                conn.executeQuery((s) -> {
                    s.setInt(1, quiz_id);
                    s.setInt(2, user_id.get());
                    s.setInt(3, status);
                    s.setTimestamp(4, dt);
                    s.setInt(5, time);
                    s.setInt(6, duplicate);
                    s.executeUpdate();
                    ResultSet rs = s.getGeneratedKeys();
                    if(rs.next()) {
                        new_quiz_result_id.put(rs.getInt(1));
                    }
                    else {
                        throw new SQLException("fail quiz result add");
                    }
                }, "INSERT INTO quiz_result (quiz_id, user_id, status, date, time, duplicate) VALUES (?, ?, ?, ?, ?, ?)");
            }
            
            BlockTitle qrbt = loadBlockInfo(dti);
            if(qrbt.type != TYPE_QUERY_RESULT) { throw new IOException("fail content type"); }
            if(query_user_fail.get()) {
                dti.skip(qrbt.length);
            }
            else {
                loadQueryResults(conn, dti, old_key_map, qrbt, new_quiz_result_id.get());
            }
        }
    }
    
    /*
    
                            dto.writeInt(rs.getInt("id"));
                        dto.writeUTF(rs.getString("query"));
                        dto.writeUTF(rs.getString("answer"));
                        dto.writeInt(rs.getInt("is_visible_answer_in_result"));
                        dto.writeInt(rs.getInt("sort"));
                        dto.writeInt(rs.getInt("time"));
                        dto.writeInt(rs.getInt("is_fix"));
                        dto.writeInt(rs.getInt("repeat"));
                        dto.writeInt(rs.getInt("ext"));
                        dto.writeInt(rs.getInt("weight"));
                        
                        if(rs.getInt("is_fix") != 0) {

    */

    private static void loadQuerys(
            ConnectDB conn, 
            DataInputStream dti, 
            BlockTitle bt, 
            int quiz_id,
            HashMap<Integer, Integer> old_key_map) throws SQLException, IOException {
        
        for(int i = 0; i < bt.count; ++i) {
            Integer id = dti.readInt();
            String qyery = dti.readUTF(),
                    answer = dti.readUTF();
            Integer is_visible_answer_in_result = dti.readInt(),
                    sort = dti.readInt(),
                    time = dti.readInt(),
                    is_fix = dti.readInt(),
                    repeat = dti.readInt(),
                    ext = dti.readInt(),
                    weight = dti.readInt();
            Pointer<Integer> newID = new Pointer<>();
            
            conn.executeQuery((s) -> {
                s.setString(1, qyery);
                s.setString(2, answer);
                s.setInt(3, is_visible_answer_in_result);
                s.setInt(4, quiz_id);
                s.setInt(5, sort);
                s.setInt(6, time);
                s.setInt(7, is_fix);
                s.setInt(8, repeat);
                s.setInt(9, ext);
                s.setInt(10, weight);
                s.executeLargeUpdate();
                ResultSet rs = s.getGeneratedKeys();
                if(rs.next()) {
                    newID.put(rs.getInt(1));
                    old_key_map.put(id, newID.get());
                }
                else {
                    throw new SQLException("fail query add");
                }
            }, "INSERT INTO query (query, answer, is_visible_answer_in_result, "
                    + "quiz_id, sort, time, is_fix, repeat, ext, weight) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            
            if(is_fix != 0) {
                BlockTitle answers_bt = loadBlockInfo(dti);
                if(answers_bt.type != TYPE_ANSWER) { throw new IOException("fail content type"); }
                for(int j = 0; j < answers_bt.count; ++j) {
                    String answer_var = dti.readUTF();
                    conn.executeQuery((s) -> {
                        s.setInt(1, newID.get());
                        s.setString(2, answer_var);
                        s.executeUpdate();
                    }, "INSERT INTO answer (query_id, answer) VALUES (?, ?)");
                }
            }
        }
    }
    /*
    
                        dto.writeUTF(rs.getString("name"));
                        dto.writeInt(rs.getInt("is_fix"));
                        dto.writeInt(rs.getInt("count"));
                        dto.writeInt(rs.getInt("time"));
                        dto.writeInt(rs.getInt("sort"));
                        dto.writeInt(rs.getInt("level"));
                        dto.writeInt(rs.getInt("repeat"));
    
    
*/
    private static void loadQuizs(
            ConnectDB conn, 
            DataInputStream dti, 
            boolean delete_quiz, 
            boolean results, 
            boolean stored_results,
            boolean delete_results, 
            boolean skip_results_not_found_users, 
            BlockTitle bt) throws SQLException, IOException {
        if(delete_quiz) {
            conn.executeQuery((s) -> { s.executeUpdate(); }, "DELETE FROM quiz");
            conn.executeQuery((s) -> { s.executeUpdate(); }, "DELETE FROM query");
            conn.executeQuery((s) -> { s.executeUpdate(); }, "DELETE FROM answer");
        }
        
        if(delete_quiz || delete_results) {
            conn.executeQuery((s) -> { s.executeUpdate(); }, "DELETE FROM quiz_result");
            conn.executeQuery((s) -> { s.executeUpdate(); }, "DELETE FROM query_result");
        }
        
        for(int i = 0; i < bt.count; ++i) {
            String name = dti.readUTF();
            int is_fix = dti.readInt(),
                    count = dti.readInt(),
                    time = dti.readInt(),
                    sort = dti.readInt(),
                    level = dti.readInt(),
                    repeat = dti.readInt();
            Pointer<Integer> newID = new Pointer<>();
            
            conn.executeQuery((s) -> {
                s.setString(1, name);
                s.setInt(2, is_fix);
                s.setInt(3, count);
                s.setInt(4, time);
                s.setInt(5, sort);
                s.setInt(6, level);
                s.setInt(7, repeat);
                s.executeUpdate();
                ResultSet rs = s.getGeneratedKeys();
                if(rs.next()) {
                    newID.put(rs.getInt(1));
                }
                else {
                    throw new SQLException("fail quiz add");
                }
            }, "INSERT INTO quiz (name, is_fix, count, time, sort, level, repeat) VALUES (?, ?, ?, ?, ?, ?, ?)");
            
            //мап, который увяжет старые и новые id querys
            //      old      new
            HashMap<Integer, Integer> old_key_map = new HashMap<>();
            BlockTitle qbt = loadBlockInfo(dti);
            if(qbt.type != TYPE_QUERY) { throw new IOException("fail content type"); }
            loadQuerys(conn, dti, qbt, newID.get(), old_key_map);
            
            if(stored_results) {
                BlockTitle res_bt = loadBlockInfo(dti);
                if(res_bt.type != TYPE_QUIZ_RESULT) { throw new IOException("fail content type"); }
                if(results) {
                    loadResults(conn, dti, old_key_map, newID.get(), res_bt, skip_results_not_found_users);
                }
                else {
                    dti.skip(res_bt.length);
                }
            }
        }
    }
    
    private static void loadUsers(ConnectDB conn, DataInputStream dti, OptUser user, BlockTitle bt) throws SQLException, IOException {
        if(user == OptUser.delete) {
            conn.executeQuery((s) -> { s.execute(); }, "DELETE FROM users");
        }
        
        Pointer<Boolean> exist = new Pointer<>(false);
        
        for(int i = 0; i < bt.count; ++i) {
            String _login = dti.readUTF(),
                    _name = dti.readUTF(),
                    _pwd = dti.readUTF();
            Integer _is_admin = dti.readInt();
            
            if(user != OptUser.delete) {
                conn.executeQuery((s) -> {
                    s.setString(1, _login);
                    ResultSet rs = s.executeQuery();
                    if(rs.next()) {
                        exist.put(rs.getInt("CNT") > 0);
                    }
                    else {
                        throw  new SQLException("users fail");
                    }
                }, "SELECT COUNT(*) AS CNT FROM users WHERE login=?");
            }
            
            if(exist.get()) {
                if(user == OptUser.no_delete) {
                    throw new SQLException("fail user exists");
                }
                else {
                    continue;
                }
            }
            
            conn.executeQuery((s) -> {
                s.setString(1, _login);
                s.setString(2, _name);
                s.setString(3, _pwd);
                s.setInt(4, _is_admin);
                s.executeUpdate();
            }, "INSERT INTO users (login, name, pwd, is_admin) VALUES (?, ?, ?, ?)");
        }
    }
    
    public enum OptContent { defaultDir, skip, path }
    
    private static void loadContent(FileInputStream fs, DataInputStream dti, OptContent opt, String path, BlockTitle bt) throws IOException {
        String b_path = path;
        if(b_path.charAt(b_path.length() - 1) != File.separatorChar) {
            if((b_path.charAt(b_path.length() - 1) == '/') || 
                    (b_path.charAt(b_path.length() - 1) == '\\')) {
                b_path = String.format("%1$s%2$s", b_path.substring(0, path.length() - 1), File.separator);
            }
            else {
                b_path += File.separator;
            }
        }
        
        File pd = new File(b_path);
        File[] old_files = pd.listFiles();
        if(old_files != null) { for(File df : old_files) { df.delete(); } }
        
        LinkedList<String> p_elenets = new LinkedList<>();
        byte[] buffer = new byte[1024];
        for(int i = 0; i < bt.count; ++i) {
            byte en = dti.readByte();
            int clevel = dti.readInt();
            int f_length = dti.readInt();
            String name = dti.readUTF();
            String _path = b_path;
            while(clevel < p_elenets.size()) { p_elenets.removeLast(); }
            for(String pi : p_elenets) { _path += pi + File.separator; }
            switch(en) {
                case FS_ENTITY_TYPE_DIR:
                    File d = new File(_path + name);
                    if(!d.mkdir()) { throw new IOException("dir create fail"); }
                    p_elenets.add(name);
                    break;
                case FS_ENTITY_TYPE_FILE:
                    File f = new File(_path + name);
                    if(!f.createNewFile()) { throw new IOException("file create fail"); }
                    try(FileOutputStream fsi = new FileOutputStream(f)) {
                        while(f_length > 0) {
                            if(f_length > buffer.length) {
                                f_length -= buffer.length;
                                if(fs.read(buffer, 0, buffer.length) != buffer.length) {
                                    throw new IOException("fail read");
                                }
                                fsi.write(buffer, 0, buffer.length);
                            }
                            else {
                                if(fs.read(buffer, 0, f_length) != f_length) {
                                    throw new IOException("fail read");
                                }
                                fsi.write(buffer, 0, f_length);
                                f_length = 0;
                            }
                        }
                    }
                    break;
            }
        }
    }
    
    private static ConnectDB createNewDB(
            String db_path,
            String db_login,
            String db_pwd
    ) throws FileNotFoundException, IOException, SQLException {
        
        ConnectDB newDB = ConnectDB.connect(db_path + ";create=true", db_login, db_pwd);
        
        try {
            newDB.executeQuery((s) -> {
                s.execute();
            }, "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.database.defaultConnectionMode','noAccess')");
            newDB.executeQuery((s) -> {
                s.setString(1, db_login);
                s.execute();
            }, "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.database.fullAccessUsers',?)");
            newDB.executeQuery((s) -> {
                s.setString(1, "derby.user." + db_login);
                s.setString(2, db_pwd);
                s.execute();
            }, "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY(?,?)");
            newDB.executeQuery((s) -> {
                s.execute();
            }, "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.connection.requireAuthentication','true')");
            newDB.executeQuery((s) -> {
                s.execute();
            }, String.format("CREATE SCHEMA %1$s AUTHORIZATION %1$s", db_login));
            newDB.executeQuery((s) -> {
                s.execute();
            }, String.format("SET CURRENT SCHEMA %1$s", db_login));
            
            try(InputStream fs = Backup.class.getResourceAsStream(String.format("db%1$sdb.sql", File.separator))) {
                try(InputStreamReader isr = new InputStreamReader(fs)) {
                    try(BufferedReader reader = new BufferedReader(isr)) {
                        String line;
                        StringBuilder sb = new StringBuilder();
                        boolean open_block_comment = false;
                        while((line = reader.readLine()) != null) {
                            String buff = "";
                            while(true) {
                                if(open_block_comment) {
                                    //нашли начало блочного комментария
                                    //ищем конец
                                    int pos = line.indexOf("*/");
                                    if(pos != -1) {
                                        line = line.substring(pos + 2);
                                        open_block_comment = false;
                                    }
                                    else {
                                        line = "";
                                        break;
                                    }
                                }
                                else {
                                    //ищем начало блочного комментария
                                    int pos = line.indexOf("/*");
                                    if(pos != -1) {
                                        open_block_comment = true;
                                        if(pos > 0) {
                                            buff += line.substring(0, pos);
                                        }
                                        line = line.substring(pos + 2);
                                    }
                                    else {
                                        break;
                                    }
                                }
                            }

                            line = buff + line;

                            int pos = line.indexOf("--");
                            if(pos != -1) {
                                line = line.substring(0, pos) + "\n";
                            }

                            line = line.replaceAll("\\t+", " ");
                            line = line.replaceAll("\\s+", " ");
                            line = (line.compareTo(" ") == 0 ? "" : line);

                            if(line.length() > 0) { sb.append(line); }
                        }

                        String[] commands = sb.toString().split(";");
                        for(String sql_cmd : commands) {
                            newDB.executeQuery((s) -> { s.execute(); }, sql_cmd);
                        }
                    }
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            newDB.close();
            throw e;
        }
        
        return newDB;
    }
    
    public enum OptDB { defaultDB, setPathDB, newDB }
    public enum OptUser { delete, no_delete, no_delete_skip }
    
    public static void restore(
            OptDB db,
            String db_path,
            String db_login,
            String db_pwd,
            /*путь к бэкапу*/
            String backupPath,
            /*user*/
            boolean user_loading,
            OptUser user,
            /*quiz*/
            boolean quiz,
            boolean delete_quiz,
            /*results*/
            boolean results,
            boolean delete_results,
            boolean skip_results_not_found_users,
            /*content*/
            OptContent cnt,
            String cnt_path
            ) throws FileNotFoundException, IOException, SQLException {
        
        ConnectDB newConn = null;
        switch(db) {
            case defaultDB:
            case setPathDB:
                newConn = ConnectDB.connect(db_path, db_login, db_pwd);
                break;
            case newDB:
                newConn = createNewDB(db_path, db_login, db_pwd);
                break;
        }
        
        try {
            
            File bf = new File(backupPath);
            if(!bf.exists()) {
                throw new IOException(String.format("Не найден файл бэкапа %1$s", bf.getAbsolutePath()));
            }
            
            try(FileInputStream fs = new FileInputStream(bf)) {
                try(DataInputStream dti = new DataInputStream(fs)) {
                    byte gflags = dti.readByte();
                    
                    //читаем узеров если есть и надо
                    if((gflags & STORE_USERS) == STORE_USERS) {
                        BlockTitle bt = loadBlockInfo(dti);
                        if(bt.type != TYPE_USER) { throw new IOException("fail content type"); }
                        if(user_loading) {
                            loadUsers(newConn, dti, user, bt);
                        }
                        else {
                            fs.skip(bt.length);
                        }
                    }
                    
                    //читаем викторины
                    if((gflags & STORE_QUIZ) == STORE_QUIZ) {
                        BlockTitle bt = loadBlockInfo(dti);
                        if(bt.type != TYPE_QUIZ) { throw new IOException("fail content type"); }
                        if(quiz) {
                            loadQuizs(newConn, dti, delete_quiz, results, (gflags & STORE_RESULTS) == STORE_RESULTS, delete_results, skip_results_not_found_users, bt);
                        }
                        else {
                            fs.skip(bt.length);
                        }
                    }
                    
                    if(((gflags & STORE_CONTENT) == STORE_CONTENT) && (cnt != OptContent.skip)) {
                        BlockTitle bt = loadBlockInfo(dti);
                        if(bt.type != TYPE_CONTENT) { throw new IOException("fail content type"); }
                        loadContent(fs, dti, cnt, cnt_path, bt);
                    }
                }
            }
        }
        finally {
            newConn.close();
        }
    }
}
