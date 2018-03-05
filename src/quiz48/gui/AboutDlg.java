/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import quiz48.Pointer;
import quiz48.WindowLocation;

/**
 *
 * @author vasya
 */
public class AboutDlg extends JDialog {
    public AboutDlg(Window parent) {
        super(parent);
        setLayout(new BorderLayout());
        setTitle("О программе");
        setModal(true);
        setResizable(false);
        
        Pointer<AboutDlg> thisDlg = new Pointer<>(this);
        
        add(new JPanel() { {
            setLayout(new BorderLayout());
            add(new JPanel() { {
                setLayout(new BorderLayout());
                add(new JPanel(), BorderLayout.CENTER);
                add(new JPanel() { {
                    setLayout(new BorderLayout());
                    setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
                    add(new JPanel() { {
                        setLayout(new BorderLayout());
                        setBorder(BorderFactory.createEtchedBorder());
                        add(new JLabel() { {
                            setIcon(AppIcons.instance().get("super_cat.gif"));
                            if(Desktop.isDesktopSupported()) {
                                setCursor(new Cursor(Cursor.HAND_CURSOR));
                                addMouseListener(new MouseAdapter() {
                                    @Override
                                    public void mouseClicked(MouseEvent e) {
                                        try {
                                            Desktop.getDesktop().browse(new URI("https://www.youtube.com/watch?v=IT7dYd4llkI"));
                                        } catch (IOException|URISyntaxException ex) { }
                                        super.mouseClicked(e);
                                    }
                                    
                                });
                            }
                        } }, BorderLayout.CENTER);
                    } }, BorderLayout.CENTER);
                } }, BorderLayout.NORTH);
            } }, BorderLayout.WEST);
            add(new JLabel(String.format("<html>"
                    + "<div style=\"font-size: 26pt;"
                    + "border-bottom: red dotted 1px;"
                    + "font-weight: bold;" 
                    + "color: teal;" 
                    + "text-align: center;" 
                    + "padding-top: 10pt;" 
                    + "padding-bottom: 10pt;\">Quiz48</div>"
                    + "<div style=\"border-bottom: red dotted 1px;"
                    + "font-size: 14pt;"
                    + "padding: 5pt;\">Программа для тестированя знаний учеников <br>различных классов</div>"
                    + "<table style=\"font-size: 17pt;\">"
                    + "<tr>"
                    + "<td><strong>Автор:&nbsp;</strong></td>"
                    + "<td color=\"blue\"><strong><em>Вася Соколов</strong></em></td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td><strong>Класс:&nbsp;</strong></td>"
                    + "<td color=\"blue\"><strong><em>4<sup>&nbsp;<u>A</u></sup></strong></em></td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td><strong>Школа:&nbsp;</strong></td>"
                    + "<td color=\"blue\"><strong><em>№48</strong></em></td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td><strong>Город:&nbsp;</strong></td>"
                    + "<td color=\"blue\"><strong><em>Липецк</strong></em></td>"
                    + "</tr>"
                    + "</table>"
                    + "<table style=\"font-size: 20pt;\">"
                    + "<tr>"
                    + "<td><img src=\"%1$s\" /></td>"
                    + "<td><span color=\"blue\"><strong><em>Российская<br />Федерация</strong></em></span></td>"
                    + "</tr>"
                    + "</table>"
                    + "</html>", AppIcons.instance().get("flag.gif").toString())), BorderLayout.CENTER);
        } }, BorderLayout.CENTER);
        
        add(new JPanel() { {
            setLayout(new FlowLayout(FlowLayout.RIGHT));
            add(new JButton() { {
                setText("Закрыть");
                addActionListener((e) -> {
                    thisDlg.get().dispose();
                });
            } });
        } }, BorderLayout.SOUTH);
        
        pack();
        
        WindowLocation.DialogSetCenterParentWindowLocation(parent, this);
    }
}
