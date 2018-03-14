/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.db.orm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import quiz48.Pointer;
import quiz48.db.ConnectDB;

/**
 *
 * @author vasya
 */
public class TestResult {
    public final int ID, time;
    public final User user;
    public final Test test;
    public final Timestamp date;
    public final boolean duplicate, passed;
    
    public TestResult(int ID, int time, User user, Test test, Timestamp date, boolean duplicate, boolean passed) {
        this.ID = ID;
        this.time = time;
        this.user = user;
        this.test = test;
        this.date = date;
        this.duplicate = duplicate;
        this.passed = passed;
    }
    
    public static TestResult createTestResult(ConnectDB conn, User u, Test t) throws SQLException {
        Pointer<Integer> ID = new Pointer<>(),
                dup = new Pointer<>();
        
        //узнаем не проходили ли тест уже
        conn.executeQuery((s) -> {
            s.setInt(1, t.ID);
            s.setInt(2, u.ID);
            ResultSet rs = s.executeQuery();
            if(rs.next()) {
                dup.put(rs.getInt("CNT"));
            }
            else {
                throw new SQLException("fail");
            }
        }, "SELECT COUNT(*) AS CNT FROM quiz_result WHERE quiz_id=? AND user_id=?");
        
        //вставим наконец запись
        conn.executeQuery((s) -> {
            s.setInt(1, t.ID);
            s.setInt(2, u.ID);
            s.setInt(3, dup.get());
            s.executeUpdate();
            ResultSet gkeys = s.getGeneratedKeys();
            if(gkeys.next()) {
                ID.put(gkeys.getInt(1));
            }
            else {
                throw new SQLException("fail");
            }
        }, "INSERT INTO quiz_result (quiz_id, user_id, duplicate) VALUES(?, ?, ?)");
        
        //получим все поля - в том числе и сгенерированные
        Pointer<TestResult> tr = new Pointer<>();
        conn.executeQuery((s) -> {
            s.setInt(1, ID.get());
            ResultSet rs = s.executeQuery();
            if(rs.next()) {
                tr.put(
                        new TestResult(
                                rs.getInt("id"), 
                                rs.getInt("time"), 
                                u, 
                                t, 
                                rs.getTimestamp("date"), 
                                rs.getInt("duplicate") != 0, 
                                rs.getInt("status") == 1));
            }
            else {
                throw new SQLException("fail");
            }
        }, "SELECT * FROM quiz_result WHERE id=?");
        
        return tr.get();
    }
}
