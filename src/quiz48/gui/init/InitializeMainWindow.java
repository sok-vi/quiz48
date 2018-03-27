/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.gui.init;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import quiz48.TaskQueue;
import quiz48.WindowLocation;
import quiz48.db.ConnectDB;
import quiz48.gui.AppIcons;
import quiz48.gui.BottomPanel;
import quiz48.gui.LoadingWindow;

/**
 *
 * @author vasya
 */
public class InitializeMainWindow {
    public interface SetterPannels {
        void setPannels(JPanel main, BottomPanel bottom);
    }
    
    public static void initialize(JFrame wnd, SetterPannels s) {
        wnd.setTitle("Учебные тесты Quiz48");
        wnd.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        wnd.setIconImage(AppIcons.instance().get("icon.png").getImage());
        WindowLocation.WindowSetCenterScreenLocation23(wnd);
        wnd.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                TaskQueue.instance().addNewTask(() -> {
                    LoadingWindow.Callback cb = LoadingWindow.showLoadingWindow(e.getWindow(), "Закрытие соединения с базой данных...");
                    //LoadingWindow.sleep(3);
                    if(ConnectDB.instance().isConnected()) { ConnectDB.instance().close(); }
                    cb.setInformation("Закрытие соединения с базой данных... успешно");
                    //LoadingWindow.sleep(2);
                    try {
                        EventQueue.invokeAndWait(() -> { e.getWindow().setVisible(false); });
                    } catch (InterruptedException|InvocationTargetException ex) { }
                    TaskQueue.instance().close(() -> { System.exit(0); });
                });
            }
            
        });
        
        JPanel main = new JPanel(), bottom = new JPanel();
        wnd.setLayout(new BorderLayout());
        wnd.add(bottom, BorderLayout.SOUTH);
        wnd.add(main, BorderLayout.CENTER);
        BottomPanel bPanel = new BottomPanel(bottom, wnd);
        s.setPannels(main, bPanel);
    }
}
