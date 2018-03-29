/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import quiz48.AppProperties;

/**
 * создаём соединение при загрузки приложения
 * содержит экземрляр драйвера derby 
 * создаёт Statement для выполнения запросов
 * @author vasja
 */
public final class ConnectDB implements AutoCloseable {
    private static ConnectDB sInstance = null;
    private final static Object sSynch = new Object();
    
    public static ConnectDB instance() {
        synchronized(sSynch) {
            if(sInstance == null) { sInstance = new ConnectDB(); }
        }
        
        return sInstance;
    }
    
    private Connection m_Derby = null;
    private final Object m_Synch = new Object();
    
    public static final ConnectDB connect(String dbPath, String dbLogin, String dbPassword) throws SQLException {
        ConnectDB conn = new ConnectDB();
        conn.m_Derby = DriverManager.getConnection("jdbc:derby:" + dbPath, dbLogin, dbPassword);
        return conn;
    }
    
    public final boolean connect() throws SQLException {
        if(m_Derby != null) { return true; }
        
        m_Derby = DriverManager.getConnection(
                "jdbc:derby:" + AppProperties.DBPath, 
                AppProperties.DBLogin, AppProperties.DBPassword);
        
        if(m_Derby != null) {
            try(Statement s = m_Derby.createStatement()) {
                s.execute(String.format("SET CURRENT SCHEMA %1$s", AppProperties.DBLogin));
            }
        }
        
        return (m_Derby != null);
    }
    
    @Override
    public final void close() {
        try {
            if((m_Derby != null) &&
                    !m_Derby.isClosed()) {
                m_Derby.close();
            }
        } catch (SQLException ex) { }
    }
    
    public final boolean isConnected() {
        try {
            return (m_Derby != null) && 
                    !m_Derby.isClosed();
        } catch (SQLException ex) { }
        
        return false;
    }
    
    public interface ExQuery {
        void query(PreparedStatement ps) throws SQLException, NullPointerException;
    }
    
    public final void executeQuery(ExQuery q, String sql) throws SQLException, NullPointerException {
        synchronized(m_Synch) {
            if((q == null) || !isConnected()) { throw new NullPointerException(); }
            try(PreparedStatement s = 
                    ((sql.toUpperCase().indexOf("INSERT") == 0) ? 
                            m_Derby.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS) : 
                            m_Derby.prepareStatement(sql))) {
                q.query(s);
            }
        }
    }
}
