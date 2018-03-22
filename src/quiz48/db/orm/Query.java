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
import java.util.Random;
import quiz48.PackageLocation;
import quiz48.db.ConnectDB;

/**
 *
 * @author vasya
 */
public class Query {
    public final static int MAX_COUNT_SELECT_RANDOM_IDS = 2000;
    
    public final int ID, time, weight;
    public final String Query, Answer;
    public final boolean isFix, isVisibleAnswerInResult;
    public final LinkedList<String> answers = new LinkedList<>();
    
    private static String contentPath(String contentPath) {
        return PackageLocation.thisPackagePath + 
                ("content/" + contentPath).replace(
                        File.separatorChar == '\\' ? "/" : "\\", 
                        File.separator);
    }
    /**
     * @content-path@ заменяется на путь до ./content/
     */
    private static String parseIMG(String content) {
        return content.replace("@content-path@", "file:" + contentPath(""));
    }
    
    public Query(int id, int time, String query, String answer, 
            boolean external, boolean isFix, boolean isVisibleAnswerInResult, 
            int weight, ConnectDB conn) throws SQLException {
        ID = id;
        this.time = time;
        Answer = answer;
        this.isFix = isFix;
        this.isVisibleAnswerInResult = isVisibleAnswerInResult;
        this.weight = weight;
        if(!this.isFix) {
            quiz48.db.orm.Answer.loadAnswers(conn, (a) -> {
                answers.add(a);
            }, id);
        }
        
        if(external) {
            String content = "<html><div stryle=\"color: red; font-size: 28pt;\"><strong>File not found.</strong></div></html>";
            String file = contentPath(query);
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
            
            Query = parseIMG(content);
        }
        else { Query = parseIMG(query); }
    }
    
    public static void loadQuerys(ConnectDB conn, EntityAccess<Query> ea, Test quiz) throws SQLException {
        if(quiz.fix) {
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
                                    rs.getInt("is_visible_answer_in_result") != 0,
                                    rs.getInt("weight"), 
                                    conn));
                }
            }, "SELECT * FROM query WHERE quiz_id=? ORDER BY sort");
        }
        else {
            LinkedList<Integer> ids = new LinkedList<>();
            conn.executeQuery((s) -> {
                s.setInt(1, quiz.ID);
                s.setInt(2, MAX_COUNT_SELECT_RANDOM_IDS);
                ResultSet rs = s.executeQuery();
                while(rs.next()) {
                    ids.add(rs.getInt("id"));
                }
            }, "SELECT ID FROM query q "
                    + "WHERE q.quiz_id=? AND "
                    + "(((SELECT COUNT(*) FROM query_result qr.quiz_result_id=q.quiz_id AND qr.query_id=q.id)=0 AND q.repeat=0) OR (q.repeat>0)) "
                    + "ORDER BY q.id FETCH FIRST ? ROWS ONLY");
            
            LinkedList<Integer> u_ids = new LinkedList<>();
            StringBuilder sb = new StringBuilder(quiz.count);
            if(ids.size() >= quiz.count) {
                Random rnd = new Random(System.currentTimeMillis());
                for(int i = 0; i < quiz.count; ++i) {
                    int nPos = rnd.nextInt(quiz.count);
                    u_ids.add(ids.get(nPos));
                    ids.remove(nPos);
                    sb.append(i == 0 ? "id=?" : " OR id=?");
                }
                ids.clear();
                
                conn.executeQuery((s) -> {
                    for(int i = 0; i < u_ids.size(); ++i) {
                        s.setInt(i + 1, u_ids.get(i));
                    }
                    ResultSet rs = s.executeQuery();
                    while(rs.next()) {
                        ea.getEntity(
                                new Query(
                                        rs.getInt("id"), 
                                        rs.getInt("time"), 
                                        rs.getString("query"), 
                                        rs.getString("answer"), 
                                        rs.getInt("ext") == 1, 
                                        rs.getInt("is_fix") == 0,
                                        rs.getInt("is_visible_answer_in_result") != 0,
                                        rs.getInt("weight"), 
                                        conn));
                    }
                }, String.format("SELECT * FROM query WHERE %1$s", sb.toString()));
            }
            else {
                //не вышло фигня какая-то
            }
        }
    }
}
