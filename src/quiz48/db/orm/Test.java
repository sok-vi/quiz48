/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.db.orm;

import java.sql.ResultSet;
import java.sql.SQLException;
import quiz48.Pointer;
import quiz48.db.ConnectDB;

/**
 *
 * @author vasya
 */
public class Test  {
    public final int ID, count, time, repeat, sort, level;
    public final String name;
    public final boolean fix;
    
    public Test(int id, String name, boolean fix, int count, int time, int repeat, int sort, int level) {
        this.ID = id;
        this.name = name;
        this.fix = fix;
        this.count = count;
        this.time = time;
        this.repeat = repeat;
        this.sort = sort;
        this.level = level;
    }
    
    public static void loadTests(ConnectDB conn, EntityAccess<Test> ea, quiz48.gui.User u) throws SQLException {
        if(!u.isLogin()) {
            conn.executeQuery((s) -> {
                ResultSet rs = s.executeQuery();
                while (rs.next()) {
                    ea.getEntity(
                            new Test(
                                    rs.getInt("id"), 
                                    rs.getString("name"), 
                                    rs.getInt("is_fix") == 0, 
                                    rs.getInt("count"), 
                                    rs.getInt("time"), 
                                    rs.getInt("repeat"),
                                    rs.getInt("sort"),
                                    rs.getInt("level")
                            ));
                }
            }, "SELECT * FROM quiz ORDER BY sort, level ASC");
        }
        else {
            conn.executeQuery((s) -> {
                s.setInt(1, u.getUserEntity().ID);
                ResultSet rs = s.executeQuery();
                while (rs.next()) {
                    ea.getEntity(
                            new Test(
                                    rs.getInt("id"), 
                                    rs.getString("name"), 
                                    rs.getInt("is_fix") == 0, 
                                    rs.getInt("count"), 
                                    rs.getInt("time"), 
                                    rs.getInt("repeat"),
                                    rs.getInt("sort"),
                                    rs.getInt("level")
                            ));
                }
            }, "SELECT * FROM quiz q WHERE (((q.is_fix=0) AND (q.repeat=1)) OR "
                    + "((q.repeat=0) AND ((SELECT count(*) FROM quiz_result qr1 WHERE qr1.quiz_id=q.id AND qr1.user_id=?)=0))) "
                    + "ORDER BY q.sort, q.level ASC");
        }
    }
    
    public static Test loadTest(ConnectDB conn, int tid) throws SQLException {
        Pointer<Test> tp = new Pointer<>();
        
        conn.executeQuery((s) -> {
            s.setInt(1, tid);
            ResultSet rs = s.executeQuery();
            if(rs.next()) {
                tp.put(
                        new Test(
                                tid, 
                                rs.getString("name"), 
                                rs.getInt("is_fix") == 0, 
                                rs.getInt("count"), 
                                rs.getInt("time"), 
                                rs.getInt("repeat"), 
                                rs.getInt("sort"), 
                                rs.getInt("level")
                        )
                );
            }
            else { throw new SQLException("fail"); }
        }, "SELECT * FROM quiz WHERE id=?");
        
        return tp.get();
    }

    @Override
    public String toString() {
/*        return String.format(
                "<html><strong>%1$s</strong>%2$s%3$s</html>", 
                name,
                level > 0 ? String.format("&nbsp;<em>(сложность: %1$s)</em>", Integer.toString(level)) : "",
                sort > 0 ? String.format("&nbsp;<u color=blue>класс: %1$s</u>", Integer.toString(sort)) : ""
                );*/
        return String.format("<html><strong>%1$s</strong></html>", name);
    }

    
}
