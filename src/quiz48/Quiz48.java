/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48;

import java.awt.Color;
import quiz48.md5.MD5Frame;
import java.awt.EventQueue;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import quiz48.backup.BackupFrame;
import quiz48.backup.RestoreFrame;
import quiz48.gui.LoadingWindow;
/**
 *
 * @author vasya
 */
public class Quiz48 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Runnable runnable = null;
        
        try {
            UIManager.setLookAndFeel(AppProperties.LookAndFeel);
        }
        catch(ClassNotFoundException|InstantiationException|IllegalAccessException|UnsupportedLookAndFeelException e) { }
        
        if(args.length == 1) {
            switch(args[0]) {
                case "LookAndFeel":
                    /**
                     * перечислить список тем графического интерфейса
                        javax.swing.plaf.metal.MetalLookAndFeel
                        javax.swing.plaf.nimbus.NimbusLookAndFeel
                        com.sun.java.swing.plaf.motif.MotifLookAndFeel
                        com.sun.java.swing.plaf.gtk.GTKLookAndFeel
                     */
                    runnable = () -> {
                        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                            System.out.println(info.getClassName());
                        }
                    };
                    break;
                case "md5":
                    //утилитка для расчёта md5
                    runnable = () -> {
                        EventQueue.invokeLater(() -> {
                            MD5Frame f = new MD5Frame();
                            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            f.setVisible(true);
                        });
                    };
                    break;
                case "backup":
                    //сделать бэкап
                    runnable = () -> {
                        EventQueue.invokeLater(() -> {
                            BackupFrame f = new BackupFrame();
                            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            f.setVisible(true);
                        });
                    };
                    break;
                case "restore":
                    //сделать бэкап
                    runnable = () -> {
                        EventQueue.invokeLater(() -> {
                            RestoreFrame f = new RestoreFrame();
                            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            f.setVisible(true);
                        });
                    };
                    break;
            }
        }
        else {
            //дефолтная - запускаем основную программу
            runnable = () -> {
                TaskQueue.instance().addNewTask(() -> {
                    LoadingWindow.Callback cb = LoadingWindow.showLoadingWindow(null, "Запуск...");//покажем окно загрузки
                    //LoadingWindow.sleep(1);
                    cb.setInformation("Подключение к базе данных...");
                    try {
                        //LoadingWindow.sleep(2);
                        Application.instance().connectDB();
                    } catch (SQLException ex) {
                        cb.setInformation("Ошибка подключения к базе данных", Color.RED);
                        LoadingWindow.sleep(3);
                        cb.exit();
                        System.exit(0);
                    }
                    
                    //LoadingWindow.sleep(2);
                    cb.setInformation("Подключение к базе данных... успешно");
                    //LoadingWindow.sleep(1);
                    
                    EventQueue.invokeLater(() -> {
                        Application.instance().createMainWindow();
                    });
                    
                    cb.exit();
                });
            };
        }
        
        if(runnable != null) {
            runnable.run();
        }
    }
    
}
