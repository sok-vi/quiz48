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
import quiz48.md5.md5;

/**
 *
 * @author vasya
 */
public class User {
    public final int ID;
    private String m_Login, m_Name;
    public final boolean isAdmin;
    
    public final String getLogin() { return m_Login; }
    public final void putLogin(String newLogin, ConnectDB conn) throws SQLException {
        
        conn.executeQuery((s) -> {
            s.setString(1, newLogin);
            s.setInt(2, ID);
            s.executeUpdate();
        }, "UPDATE users SET login=? WHERE id=?");
        
        m_Login = newLogin;
    }
    public final String getName() { return m_Name; }
    public final void putName(String newName, ConnectDB conn) throws SQLException {
        
        conn.executeQuery((s) -> {
            s.setString(1, newName);
            s.setInt(2, ID);
            s.executeUpdate();
        }, "UPDATE users SET name=? WHERE id=?");
        
        m_Name = newName;
    }
    
    public final void putPwd(String newPwdHash, ConnectDB conn) throws SQLException {
        
        conn.executeQuery((s) -> {
            s.setString(1, newPwdHash);
            s.setInt(2, ID);
            s.executeUpdate();
        }, "UPDATE users SET pwd=? WHERE id=?");
    }
    
    public User(int id, String login, String name, boolean isAdmin) {
        ID = id;
        m_Login = login;
        m_Name = name;
        this.isAdmin = isAdmin;
    }
    
    public static User loadUser(ConnectDB conn, String login, String pwd) throws SQLException {
        Pointer<User> userRow = new Pointer<>();
        
        conn.executeQuery((s) -> {
            s.setString(1, login);
            ResultSet rs = s.executeQuery();
            if(rs.next()) {
                if(md5.checkPWD(pwd, rs.getString("pwd"))) {
                    userRow.put(
                            new User(
                                    rs.getInt("id"), 
                                    rs.getString("login"), 
                                    rs.getString("name"), 
                                    rs.getInt("is_admin") == 1)
                    );
                }
            }
        }, "SELECT * FROM users WHERE login=?");
        
        return userRow.get();
    }
    
    public static User createUser(ConnectDB conn, String login, String name, String pwd) throws SQLException {
        conn.executeQuery((s) -> {
            s.setString(1, login);
            s.setString(2, name);
            s.setString(3, md5.calculate(pwd));
            s.executeUpdate();
        }, "INSERT INTO users (login, name, pwd) VALUES (?, ?, ?)");
        
        return User.loadUser(conn, login, pwd);
    }
}
