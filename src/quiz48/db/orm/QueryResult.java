/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.db.orm;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import quiz48.Pointer;
import quiz48.db.ConnectDB;

/**
 *
 * @author vasya
 */
public class QueryResult {
    public enum fail {
        ok(0),
        fail(1),
        timeout_ok(2),
        timeout_fail(3),
        timeout_test_ok(4),
        timeout_test_fail(5),
        qu_timeout_test_ok(6),
        qu_timeout_test_fail(7);
        
        protected int value;
        private fail(int iv) { value = iv; }
        public int fail2int() { return value; }
        
        public final String getResultString() {
            switch(value) {
                case 1:
                    return "ошибка";
                case 2:
                    return "правильно - превышено время вопроса";
                case 3:
                    return "ошибка - превышено время вопроса";
                case 4:
                    return "правильно - превышено общее время теста";
                case 5:
                    return "ошибка - превышено общее время теста";
                case 6:
                    return "правильно - превышено общее время теста и вопроса";
                case 7:
                    return "ошибка - превышено общее время теста и вопроса";
            }
            
            return "правильно";
        }
        
        public final Color getResultColor() {
            switch(value) {
                case 1:
                    return Color.red;
                case 2:
                case 4:
                case 6:
                    return Color.yellow;
                case 3:
                case 5:
                case 7:
                    return Color.PINK;
            }
            
            return Color.green;
        }
    }
    
    public final TestResult testResult;
    public final Query query;
    public final boolean duplicate;
    private int time;
    private fail result;
    private String answer;
    
    public QueryResult(TestResult tr, Query q, int time, String answer, fail fv, boolean duplicate) {
        testResult = tr;
        query = q;
        this.time = time;
        this.answer = answer;
        result = fv;
        this.duplicate = duplicate;
    }
    
    public final int time() { return time; }
    public final void time(int time/*в секундах*/, ConnectDB conn) throws SQLException {
        conn.executeQuery((s) -> {
            s.setInt(1, time);
            s.setInt(2, testResult.ID);
            s.setInt(3, query.ID);
            s.executeUpdate();
        }, "UPDATE query_result SET time=? WHERE quiz_result_id=? AND query_id=?");
        this.time = time;
    }
    
    public final String answer() { return answer; }
    public final void answer(String answer, ConnectDB conn) throws SQLException {
        conn.executeQuery((s) -> {
            s.setString(1, answer);
            s.setInt(2, testResult.ID);
            s.setInt(3, query.ID);
            s.executeUpdate();
        }, "UPDATE query_result SET answer=? WHERE quiz_result_id=? AND query_id=?");
        this.answer = answer;
    }
    
    public final fail fail() { return result; }
    public final void fail(fail fv, ConnectDB conn) throws SQLException {
        conn.executeQuery((s) -> {
            s.setInt(1, fv.fail2int());
            s.setInt(2, testResult.ID);
            s.setInt(3, query.ID);
            s.executeUpdate();
        }, "UPDATE query_result SET fail=? WHERE quiz_result_id=? AND query_id=?");
        this.result = fv;
    }
    
    public static QueryResult saveQueryResult(ConnectDB conn, TestResult tr, Query q, int time, String answer, fail fv) throws SQLException {
        Pointer<Boolean> qResExist = new Pointer<>(false);
        conn.executeQuery((s) -> {
            s.setInt(1, q.ID);
            ResultSet rs = s.executeQuery();
            if(rs.next()) {
                qResExist.put(rs.getInt("CNT") > 0);
            }
        }, "SELECT COUNT(*) AS CNT FROM query_result WHERE query_id=?");
        
        conn.executeQuery((s) -> {
            s.setInt(1, tr.ID);
            s.setInt(2, q.ID);
            s.setString(3, answer);
            s.setInt(4, time);
            s.setInt(5, fv.fail2int());
            s.setInt(6, qResExist.get() ? 1 : 0);
            s.executeUpdate();
        }, "INSERT INTO query_result (quiz_result_id, query_id, answer, time, fail, duplicate) VALUES(?, ?, ?, ?, ?, ?)");
        
        return new QueryResult(tr, q, time, answer, fv, false);
    }
}
