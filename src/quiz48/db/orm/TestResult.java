/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.db.orm;

import java.awt.Color;
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
    public enum status {
        fail(0),
        ok(1),
        timeout(2);
        
        protected int value;
        private status(int iv) { value = iv; }
        public int status2int() { return value; }
        
        public static status int2status(int iv) {
            switch(iv) {
                case 1:
                    return ok;
                case 2:
                    return timeout;
            }
            return fail;
        }
        
        public final String getResultString() {
            switch(value) {
                case 1:
                    return "пойден";
                case 2:
                    return "превышен лимит времени";
            }
            
            return "не завершёт";
        }
        
        public final Color getResultColor() {
            return value == 1 ? Color.GREEN : Color.RED;
        }
    }
    
    private static status int2status(int v) {
        switch(v) {
            case 1:
                return status.ok;
        }
        
        return status.fail;
    }
    
    public final int ID;
    private int time;
    private status sv;
    public final User user;
    public final Test test;
    public final Timestamp date;
    public final boolean duplicate;
    
    public TestResult(int ID, int time, User user, Test test, Timestamp date, boolean duplicate, status status) {
        this.ID = ID;
        this.time = time;
        this.user = user;
        this.test = test;
        this.date = date;
        this.duplicate = duplicate;
        sv = status;
    }
    
    public final int time() { return time; }
    public final void time(int time, ConnectDB conn) throws SQLException {
        conn.executeQuery((s) -> {
            s.setInt(1, time);
            s.setInt(2, ID);
            s.execute();
        }, "UPDATE quiz_result SET time=? WHERE id=?");
        this.time = time;
    }
    
    public final status status() { return sv; }
    public final void status(status sv, ConnectDB conn) throws SQLException {
        conn.executeQuery((s) -> {
            s.setInt(1, sv.status2int());
            s.setInt(2, ID);
            s.executeUpdate();
        }, "UPDATE quiz_result SET status=? WHERE id=?");
        this.sv = sv;
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
            s.setInt(3, 1);
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
                                status.int2status(rs.getInt("status"))
                                ));
            }
            else {
                throw new SQLException("fail");
            }
        }, "SELECT * FROM quiz_result WHERE id=?");
        
        return tr.get();
    }
}
