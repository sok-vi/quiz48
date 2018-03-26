/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48;

import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import quiz48.db.ConnectDB;
import quiz48.gui.BottomPanel;
import quiz48.gui.User;
import quiz48.gui.init.InitializeMainWindow;
import quiz48.gui.init.InitializeQuizListView;
import quiz48.gui.init.InitializeResultQuestionsView;
import quiz48.gui.init.InitializeResultView;
import quiz48.gui.init.InitializeTestView;

/**
 *
 * @author vasya
 */
public final class Application {
    private static Application gApp = null;
    private final static Object sSynch = new Object();
    public static Application instance() {
        synchronized(sSynch) {
            if(gApp == null) { gApp = new Application(); }
            return gApp;
        }
    }
    
    private Application() {
        
    }
    
    /**
     * бд
     */
    private final ConnectDB m_Conn = new ConnectDB();
    public final boolean isConnectDB() { return m_Conn.isConnected(); }
    public final void connectDB() throws SQLException { if(!isConnectDB()) { m_Conn.connect(); } }
    public final void closeDB() { m_Conn.close(); }
    
    /**
     * юзер
     */
    private final User m_User = new User();
    
    /**
     * главноное окно
     */
    private JFrame m_MainWnd;
    private JPanel m_MainPanel;
    private BottomPanel m_BottomPanel;
    public final void createMainWindow() {
        if(m_MainWnd == null) {
            m_MainWnd = new JFrame();
            InitializeMainWindow.initialize(m_MainWnd, (main, bottom) -> {
                m_MainPanel = main;
                m_BottomPanel = bottom;
            });
            
            Pointer<Runnable> initQuizListView = new Pointer<>();
            Pointer<InitializeResultView.ShowTestResultView> initResultView = new Pointer<>();
            Pointer<InitializeTestView.SetCurrentTest> initTestView = new Pointer<>();
            Pointer<InitializeResultQuestionsView.SetCurrentTestResult> initResultQuestions = new Pointer<>();
            
            initQuizListView.put(() -> {
                InitializeQuizListView.initialize(
                        m_MainWnd, 
                        m_MainPanel, 
                        m_BottomPanel, 
                        initResultView.get(), 
                        m_User,
                        m_Conn,
                        initTestView.get());
            });
            
            initResultView.put((rvs) -> {
                InitializeResultView.initialize(m_MainWnd, m_MainPanel, m_BottomPanel, initQuizListView.get(), m_User, m_Conn, initResultQuestions.get(), rvs);
            });
            
            initTestView.put((t) -> {
                InitializeTestView.initialize(m_MainWnd, m_MainPanel, m_BottomPanel, initQuizListView.get(), m_User, m_Conn, t, initResultQuestions.get());
            });
            
            initResultQuestions.put((tr, qrs, rvs) -> {
                InitializeResultQuestionsView.initialize(m_MainWnd, m_MainPanel, m_BottomPanel, initQuizListView.get(), initResultView.get(), m_Conn, tr, qrs, rvs);
            });
            
            initQuizListView.get().run();
            m_MainWnd.setVisible(true);
        }
    }
}
