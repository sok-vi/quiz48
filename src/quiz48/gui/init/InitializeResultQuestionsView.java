/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.gui.init;

import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import quiz48.db.ConnectDB;
import quiz48.db.orm.QueryResult;
import quiz48.db.orm.TestResult;
import quiz48.gui.AppIcons;
import quiz48.gui.BottomPanel;

/**
 *
 * @author vasya
 */
public class InitializeResultQuestionsView {
    
    public interface  SetCurrentTestResult {
        void run(TestResult r, List<QueryResult> qrs);
    }
    
    public static void initialize(
            JFrame wnd, 
            JPanel main, 
            BottomPanel bottom, 
            Runnable initStartWindow, 
            Runnable initStatWindow, 
            ConnectDB conn, 
            TestResult current,
            List<QueryResult> qresults) {
        
        main.removeAll();
        main.setLayout(new BorderLayout());
        
        bottom.clearButtons();

        bottom.addButton(new JButton() { {
            setText("<К списку результатов тестов");
            setHorizontalTextPosition(JButton.LEFT);
            setIcon(AppIcons.instance().get("result_view32.png"));
            addActionListener((e) -> { initStatWindow.run(); });
        } });
        
        bottom.addButton(new JButton() { {
            setText("<<К списку задач");
            setHorizontalTextPosition(JButton.LEFT);
            setIcon(AppIcons.instance().get("qlist32.png"));
            addActionListener((e) -> { initStartWindow.run(); });
        } });
        
        wnd.revalidate();
        wnd.repaint();
    }
}
