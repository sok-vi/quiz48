/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.db.orm;

import java.sql.SQLException;
import quiz48.db.ConnectDB;

/**
 *
 * @author vasya
 */
public class QueryResult {
    public enum fail {
        ok(0) { },
        fail(1) { },
        timeout(2) { };
        
        protected int value;
        private fail(int iv) { value = iv; }
        public int fail2int() { return value; }
    }
    
    public final TestResult testResult;
    public final Query query;
    private int time;
    private fail result;
    private String answer;
    
    public QueryResult(TestResult tr, Query q, int time, String answer, fail fv) {
        testResult = tr;
        query = q;
        this.time = time;
        this.answer = answer;
        result = fv;
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
        conn.executeQuery((s) -> {
            s.setInt(1, tr.ID);
            s.setInt(2, q.ID);
            s.setString(3, answer);
            s.setInt(4, time);
            s.setInt(5, fv.fail2int());
            s.executeUpdate();
        }, "INSERT INTO query_result (quiz_result_id, query_id, answer, time, fail) VALUES(?, ?, ?, ?, ?)");
        
        return new QueryResult(tr, q, time, answer, fv);
    }
}
