/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.db.orm;

import java.sql.ResultSet;
import java.sql.SQLException;
import quiz48.db.ConnectDB;

/**
 *
 * @author vasya
 */
public class Answer {
    public static void loadAnswers(ConnectDB conn, EntityAccess<String> ea, int qID) throws SQLException {
        conn.executeQuery((s) -> {
            s.setInt(1, qID);
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                ea.getEntity(rs.getString("answer"));
            }
        }, "SELECT * FROM answer WHERE query_id=?");
    }
}
