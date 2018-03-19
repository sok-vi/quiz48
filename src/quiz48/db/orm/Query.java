/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.db.orm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import quiz48.PackageLocation;
import quiz48.db.ConnectDB;

/**
 *
 * @author vasya
 */
public class Query {
    public final int ID, time;
    public final String Query, Answer;
    public final boolean isFix;
    public final LinkedList<String> answers = new LinkedList<>();
    
    private static String parseIMG() {
        return "";
    }
    
    public Query(int id, int time, String query, String answer, boolean external, boolean isFix, ConnectDB conn) throws SQLException {
        ID = id;
        this.time = time;
        Answer = answer;
        this.isFix = isFix;
        if(!this.isFix) {
            quiz48.db.orm.Answer.loadAnswers(conn, (a) -> {
                answers.add(a);
            }, id);
        }
        
        if(external) {
            String content = "<html><div stryle=\"color: red; font-size: 28pt;\"><strong>File not found.</strong></div></html>";
            String file = PackageLocation.thisPackagePath + 
                    ("content/" + query).replace(
                            File.separatorChar == '\\' ? "/" : "\\", 
                            File.separator);
            File f_content = new File(file);
            if(f_content.exists()) {
                try {
                    try(FileInputStream fis = new FileInputStream(f_content)) {
                        byte[] b_content = new byte[(int)f_content.length()];
                        if(fis.read(b_content) == b_content.length) {
                            content = new String(b_content);
                        }
                    }
                } catch (IOException ex) { }
            }
            
            Query = content;
        }
        else { Query = query; }
    }
    
    public static void loadQuery(ConnectDB conn, EntityAccess<Query> ea, Test quiz) throws SQLException {
        conn.executeQuery((s) -> {
            s.setInt(1, quiz.ID);
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                ea.getEntity(
                        new Query(
                                rs.getInt("id"), 
                                rs.getInt("time"), 
                                rs.getString("query"), 
                                rs.getString("answer"), 
                                rs.getInt("ext") == 1, 
                                rs.getInt("is_fix") == 0,
                                conn));
            }
        }, "SELECT * FROM query WHERE quiz_id=? ORDER BY sort");
    }
}
