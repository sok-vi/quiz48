/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.backup;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import quiz48.PackageLocation;
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
            TYPE_QUERY_ANSWER = 6,
            TYPE_CONTENT = 7;
    
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
    
    private static void storeQuiz(ConnectDB conn, StoreBlockInfo sbi, DataOutputStream dto, boolean result) throws SQLException {
        
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
            String backupPath,
            LoadingWindow.Callback cb) throws FileNotFoundException, IOException, SQLException {
        
        boolean exContent = (new File(PackageLocation.thisPackagePath + "content\\")).exists();
        
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
                                (exContent ? STORE_CONTENT : 0));
                
                StoreBlockInfo info = new StoreBlockInfo();
                info.tmpFile = new File(String.format("%1$s.%2$s.tmp", backupPath, Integer.toString((int)System.currentTimeMillis())));
                
                if(info.tmpFile.exists()) {
                    throw new IOException(String.format("Временый файл {%1$s} уже существует", info.tmpFile.getAbsolutePath()));
                }
                
                if(user) {
                    
                    if(!info.tmpFile.createNewFile()) {
                        throw new IOException(String.format("Не удалось создать создать временный файл %1$s", info.tmpFile.getAbsolutePath()));
                    }
                
                    //сохраняем юзеров во временный файл
                    try(FileOutputStream tmpfs = new FileOutputStream(info.tmpFile)) {
                        try(DataOutputStream tmpdto = new DataOutputStream(tmpfs)) {
                            storeUsers(conn, info, tmpdto);
                        }
                    }

                    info.length = (int)info.tmpFile.length();
                    storeBlockInfo(info, dto);//запись информации о блоке
                    copyTmpFile(fs, info.tmpFile);//копируем узеров

                    if(!info.tmpFile.delete()) {
                        throw new IOException(String.format("Не удалось удалить временный создать файл %1$s", info.tmpFile.getAbsolutePath()));
                    }
                }
            }
        }
    }
}
