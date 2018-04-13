/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.db.orm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import quiz48.Pointer;
import quiz48.db.ConnectDB;
import quiz48.gui.FilterDlg;

/**
 *
 * @author vasya
 *///SELECT * FROM QUERY OFFSET 1 ROWS FETCH NEXT 3 ROWS ONLY
public class TestResultWithRating extends TestResult {
    public interface SQLWhereParams {
        int SetParams(PreparedStatement s, int startParamIndex) throws SQLException;
    }
    
    public interface SQLWhereCreator {
        void where(String SQLWhere, SQLWhereParams params);
    }
    
    public final static class LoadPageInfo {
        public final int currPage, pageCount;
        public LoadPageInfo(int curr, int count) { currPage = curr; pageCount = count; }
    }
    
    private final static int PAGE_SIZE = 50;
    public final double rating;
    
    public TestResultWithRating(int ID, int time, User user, Test test, Timestamp date, boolean duplicate, status status, double rating) {
        super(ID, time, user, test, date, duplicate, status);
        this.rating = rating;
    }
    
    public static LoadPageInfo loadResults(
            ConnectDB conn, 
            EntityAccess<TestResultWithRating> ea, 
            int dbPage, 
            User u, 
            LinkedList<FilterDlg.Filter> filters) throws SQLException {

        Pointer<String> sql_where = new Pointer<>("");
        LinkedList<SQLWhereParams> prs = new LinkedList<>();
        
        if(!u.isAdmin) { sql_where.put("qr.user_id=?"); }
        
        for(FilterDlg.Filter f : filters) {
            f.SetSQLWhere((sql, ps) -> {
                prs.add(ps);
                sql_where.put(
                        sql_where.get() + 
                                (sql_where.get().length() > 0 ? " AND " : "") + 
                                sql);
            });
        }
        
        if(sql_where.get().length() > 0) {
            sql_where.put(" WHERE " + sql_where.get() + " ");
        }
        
        Pointer<Integer> count = new Pointer<>(0);
        conn.executeQuery((s) -> {
            int cnt_p = 1;
            if(!u.isAdmin) {
                s.setInt(1, u.ID);
                ++cnt_p;
            }
            
            for(SQLWhereParams sp : prs) {
                cnt_p += sp.SetParams(s, cnt_p);
            }
            
            ResultSet rs = s.executeQuery();
            if(rs.next()) { count.put(rs.getInt("CNT")); }
            else { throw new SQLException("fail"); }
        }, "SELECT COUNT(*) AS CNT FROM quiz_result qr" + sql_where.get());

        
        //int count = getPageCount(conn);
        int pageCount = count.get() / PAGE_SIZE;
        if((pageCount * PAGE_SIZE) < count.get()) { ++pageCount; }
        
        /**
         * указанная страница должна попасть в дипозон
         * если нет то выберем ближайшую страницу
         */
        
        Pointer<Integer> page = new Pointer<>(dbPage);
        if(page.get() >= pageCount) { page.put(pageCount - 1); }
        if(page.get() < 0) { page.put(0); }
        
        HashMap<Integer, User> users = new HashMap<>();
        HashMap<Integer, Test> tests = new HashMap<>();
        users.put(u.ID, u);
        
        conn.executeQuery((s) -> {
            int cnt_p = 1;
            if(!u.isAdmin) {
                s.setInt(1, u.ID);
                ++cnt_p;
            }
            
            for(SQLWhereParams sp : prs) {
                cnt_p += sp.SetParams(s, cnt_p);
            }
            
            s.setInt(cnt_p++, page.get() * PAGE_SIZE);
            s.setInt(cnt_p, PAGE_SIZE);
            
            ResultSet rs = s.executeQuery();
            while(rs.next()) {
                int _w = rs.getInt("SUM_W"),
                        _wa = rs.getInt("SUM_W_ACT"),
                        _uid = rs.getInt("user_id"),
                        _tid = rs.getInt("quiz_id");
                if(_w == 0) { _w = 1; }
                
                if(!users.containsKey(_uid)) {
                    users.put(_uid, User.loadUser(conn, _uid));
                }
                
                User _u = users.get(_uid);
                
                if(!tests.containsKey(_tid)) {
                    tests.put(_tid, Test.loadTest(conn, _tid));
                }
                
                Test _t = tests.get(_tid);
                
                ea.getEntity(
                        new TestResultWithRating(
                                rs.getInt("id"), 
                                rs.getInt("time"), 
                                _u, 
                                _t, 
                                rs.getTimestamp("date"), 
                                rs.getInt("duplicate") != 0, 
                                status.int2status(rs.getInt("status")),
                                ((double)_wa) * 100 / _w
                        ));
            }
        }, String.format("SELECT qr.*, "
                + "(SELECT SUM(q1.weight) FROM query q1 WHERE q1.quiz_id=qr.quiz_id) AS SUM_W, "
                + "(SELECT SUM(q2.weight) FROM query_result qr2 INNER JOIN query q2 ON q2.id=qr2.query_id WHERE qr2.quiz_result_id=qr.id AND qr2.fail=0) AS SUM_W_ACT "
                + "FROM quiz_result qr%1$s OFFSET ? ROWS FETCH NEXT ? ROWS ONLY", sql_where.get()));
        
        return new LoadPageInfo(page.get(), pageCount);
    }
}
