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
        fail(int iv) { value = iv; }
        public int fail2int() { return value; }
    }
    
    
    
    public final TestResult testResult;
    public final Query query;
    private int time;
    private fail fail;
    private String answer;
    
    public QueryResult(TestResult tr, Query q, int time, String answer, fail fv) {
        testResult = tr;
        query = q;
        this.time = time;
        this.answer = answer;
        fail = fv;
    }
    
    public static QueryResult saveQueryResult(ConnectDB conn, TestResult tr, Query q, int time, String answer, fail fv) throws SQLException {
        conn.executeQuery((s) -> {
            s.setInt(1, tr.ID);
            s.setInt(2, q.ID);
            s.setString(3, answer);
            s.setInt(4, time);
            s.setInt(5, fv.fail2int());
        }, "INSERT INTO query_result (quiz_result_id, query_id, answer, time, fail) VALUES(?, ?, ?, ?, ?)");
        
        return new QueryResult(tr, q, time, answer, fv);
    }
}
