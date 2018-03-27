/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.backup;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import quiz48.PackageLocation;
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
        public String tmpPath;
    }
    
    private static void storeUsers(ConnectDB conn) {
        
    }
    
    public static void storeBackup(
            ConnectDB conn, 
            /*вопросы обязательно*/
            boolean user, 
            boolean result,
            String backupPath,
            LoadingWindow.Callback cb) throws FileNotFoundException, IOException {
        
        boolean exContent = (new File(PackageLocation.thisPackagePath + "content\\")).exists();
        
        File bf = new File(backupPath);
        if(!bf.exists()) { 
            if(!bf.createNewFile()) {
                throw new IOException(String.format("Не удалось создать создать файл %1$s", backupPath));
            }
        }
        
        try(FileOutputStream fs = new FileOutputStream(bf)) {
            try(DataOutputStream dto = new DataOutputStream(fs)) {
                dto.writeByte(
                        STORE_QUIZ | 
                                (user ? STORE_USERS : 0) |
                                (result ? STORE_RESULTS : 0) |
                                (exContent ? STORE_CONTENT : 0));
                
            }
        }
    }
}
