/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.backup;

import java.io.BufferedReader;
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
    
    private static void storeContent(File f, FileOutputStream fs, DataOutputStream dto) throws IOException {
        if(f.isDirectory()) {
            dto.writeByte(FS_ENTITY_TYPE_DIR);
            dto.writeUTF(f.getName());
            File[] list = f.listFiles();
            for(File cf : list) {
                storeContent(cf, fs, dto);
            }
        }
        else {
            dto.writeByte(FS_ENTITY_TYPE_FILE);
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
                            storeContent(new File(contentPath), tmpfs, tmpdto);
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
    
    public static void restore(
            OptDB db,
            String db_path,
            String db_login,
            String db_pwd
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
    }
}
