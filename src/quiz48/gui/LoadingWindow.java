/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import quiz48.Pointer;
import quiz48.WindowLocation;

/**
 *
 * @author vasya
 */
public class LoadingWindow {
    private static final Color DEFAULT_FOREGROUND_COLOR = Color.BLUE;
    
    public interface Callback {
        void setInformation(String information, Color c);
        void setInformation(String information);
        void exit();
    }
    
    private static void createView(Container cp, String title, Pointer<JLabel> infoLabel) {
        JPanel broot = new JPanel();
        broot.setLayout(new BorderLayout());
        broot.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        JPanel view = new JPanel();
        broot.add(view, BorderLayout.CENTER);
        view.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        view.setLayout(new BoxLayout(view, BoxLayout.X_AXIS));
        view.add(new JLabel(AppIcons.instance().get("loading.gif")));
        view.add(Box.createHorizontalStrut(10));
        JLabel info = new JLabel();
        infoLabel.put(info);
        java.awt.Font fnt = info.getFont();
        info.setFont(new java.awt.Font(fnt.getName(), java.awt.Font.BOLD, (int)(fnt.getSize() * 1.3)));
        info.setForeground(DEFAULT_FOREGROUND_COLOR);
        info.setText(title);
        view.add(info);
        cp.setLayout(new BorderLayout());
        cp.add(broot, BorderLayout.CENTER);
    }
    
    public static Callback showLoadingWindow(Window parent, String title) {
        Pointer<JLabel> label = new Pointer<>();
        if(parent != null) {
            Pointer<JDialog> dlg = new Pointer<>();
            EventQueue.invokeLater(() -> {
                dlg.put(new JDialog(parent));
                dlg.get().setUndecorated(true);
                dlg.get().setModal(true);
                dlg.get().setResizable(false);
                createView(dlg.get().getContentPane(), title, label);
                dlg.get().pack();
                WindowLocation.DialogSetCenterParentWindowLocation(parent, dlg.get());
                dlg.get().setVisible(true);
            });
            return new Callback() {
                @Override
                public void setInformation(String information) {
                    setInformation(information, DEFAULT_FOREGROUND_COLOR);
                }
                @Override
                public void exit() {
                    EventQueue.invokeLater(() -> {
                        dlg.get().dispose();
                    });
                }
                @Override
                public void setInformation(String information, Color c) {
                    EventQueue.invokeLater(() -> {
                        label.get().setText(information);
                        label.get().setForeground(c);
                        dlg.get().pack();
                        WindowLocation.DialogSetCenterParentWindowLocation(parent, dlg.get());
                    });
                }
            };
        }
        
        Pointer<JFrame> frm = new Pointer<>();
        EventQueue.invokeLater(() -> {
            frm.put(new JFrame());
            frm.get().setUndecorated(true);
            frm.get().setResizable(false);
            createView(frm.get().getContentPane(), title, label);
            frm.get().pack();
            WindowLocation.WindowSetCenterScreenLocation(frm.get());
            frm.get().setVisible(true);
        });
        return new Callback() {
            @Override
            public void setInformation(String information) {
                setInformation(information, DEFAULT_FOREGROUND_COLOR);
            }
            @Override
            public void exit() {
                EventQueue.invokeLater(() -> {
                    frm.get().dispose();
                });
            }
            @Override
            public void setInformation(String information, Color c) {
                EventQueue.invokeLater(() -> {
                    label.get().setText(information);
                    label.get().setForeground(c);
                    frm.get().pack();
                    WindowLocation.WindowSetCenterScreenLocation(frm.get());
                });
            }
        };
    }
    
    public static void sleep(int timeout) {
        try { Thread.sleep(timeout * 1000);
        } catch (InterruptedException ex) { }
    }
}
