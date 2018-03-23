/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.db.orm;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import quiz48.Pointer;
import quiz48.db.ConnectDB;

/**
 *
 * @author vasya
 *///SELECT * FROM QUERY OFFSET 1 ROWS FETCH NEXT 3 ROWS ONLY
public class TestResultWithRating extends TestResult{
    private final static int PAGE_SIZE = 10;
    public final double rating;
    
    public TestResultWithRating(int ID, int time, User user, Test test, Timestamp date, boolean duplicate, status status, double rating) {
        super(ID, time, user, test, date, duplicate, status);
        this.rating = rating;
    }
    
    public static void loadResults(ConnectDB conn, EntityAccess<TestResultWithRating> ea, int dbPage, User u) throws SQLException {
        /**
         * узнае количество страниц данных в бд
         */
        Pointer<Integer> count = new Pointer<>(0);
        conn.executeQuery((s) -> {
            ResultSet rs = s.executeQuery();
            if(rs.next()) { count.put(rs.getInt("CNT")); }
            else { throw new SQLException("fail"); }
        }, "SELECT COUNT(*) AS CNT FROM quiz_result");
        
        int pageCount = count.get() / PAGE_SIZE;
        if((pageCount * PAGE_SIZE) < count.get()) { ++pageCount; }
        
        /**
         * указанная страница должна попасть в дипозон
         * если нет то выберем ближайшую страницу
         */
        
        Pointer<Integer> page = new Pointer<>(dbPage);
        if(page.get() > pageCount) { page.put(pageCount); }
        if(page.get() < 0) { page.put(0); }
        
        conn.executeQuery((s) -> {
            s.setInt(1, page.get() * PAGE_SIZE);
            s.setInt(2, PAGE_SIZE);
            ResultSet rs = s.executeQuery();
            while(rs.next()) {
                int _w = rs.getInt("SUM_W"),
                        _wa = rs.getInt("SUM_W_ACT");
                if(_w == 0) { _w = 0; }
                ea.getEntity(
                        new TestResultWithRating(
                                rs.getInt("id"), 
                                rs.getInt("time"), 
                                u, 
                                null, 
                                rs.getTimestamp("date"), 
                                rs.getInt("duplicate") != 0, 
                                status.int2status(rs.getInt("status")),
                                ((double)_wa) / _w
                        ));
            }
        }, "SELECT qr.*, "
                + "(SELECT SUM(q1.weight) FROM query_result qr1 INNER JOIN query q1 ON q1.id=qr1.query_id WHERE qr1.quiz_result_id=qr.id) AS SUM_W, "
                + "(SELECT SUM(q2.weight) FROM query_result qr2 INNER JOIN query q2 ON q2.id=qr2.query_id WHERE qr2.quiz_result_id=qr.id AND qr1.fail=0) AS SUM_W_ACT "
                + "FROM quiz_result qr OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
    }
}
