/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.gui.init;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import quiz48.db.ConnectDB;
import quiz48.gui.AppIcons;
import quiz48.gui.BottomPanel;
import quiz48.gui.User;

/**
 *
 * @author vasya
 */
public class InitializeResultView {
    public static void initialize(
            JFrame wnd, 
            JPanel main, 
            BottomPanel bottom, 
            Runnable initStartWindow, 
            User u, 
            ConnectDB conn) {
        
        main.removeAll();
        main.setLayout(new BorderLayout());
        
        bottom.clearButtons();

        bottom.addButton(new JButton() { {
            setText("<К списку задач");
            setHorizontalTextPosition(JButton.LEFT);
            setIcon(AppIcons.instance().get("qlist32.png"));
            addActionListener((e) -> { initStartWindow.run(); });
        } });
        
        wnd.revalidate();
        wnd.repaint();
    }
}
