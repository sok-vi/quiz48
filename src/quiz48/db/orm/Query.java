/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.db.orm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
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
    
    public Query(int id, int time, String query, String answer, boolean external, boolean isFix, ConnectDB conn) throws SQLException {
        ID = id;
        this.time = time;
        Query = query;
        Answer = answer;
        this.isFix = isFix;
        if(!this.isFix) {
            quiz48.db.orm.Answer.loadAnswers(conn, (a) -> {
                answers.add(a);
            }, id);
        }
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
