/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.gui.init;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
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
        
        //верхняя панель
        main.add(new JPanel() { {
        } }, BorderLayout.NORTH);
        //таблица
        main.add(new JPanel() { {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(0, 7, 0, 7));
            add(new JScrollPane(
                    new JTable() { {
                        /**
                         * колонки
                         * 1 - название
                         * 2 - дата прохождения
                         * 3 - юзер
                         * 4 - общее время
                         * 5 - процент выполнения
                         */
                        setRowMargin(2);
                        setDragEnabled(false);
                        getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        
                        setModel(new AbstractTableModel() {
                            @Override
                            public int getRowCount() { return 0; }

                            @Override
                            public int getColumnCount() {
                                return 5;
                            }
                        
                            @Override
                            public Object getValueAt(int rowIndex, int columnIndex) {
                                return null;
                            }
                            
                            public String getColumnName(int column) {
                                switch(column) {
                                    case 0:
                                        return "Название";
                                    case 1:
                                        return "Дата";
                                    case 2:
                                        return "Пользователь";
                                    case 3:
                                        return "Заптачено, с.";
                                    case 4:
                                        return "Оценка, %";
                                }
                                
                                return super.getColumnName(column);
                            }

                            @Override
                            public Class<?> getColumnClass(int columnIndex) {
                                return super.getColumnClass(columnIndex); 
                            }
                        });
                            
                        addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                super.mouseClicked(e);
                            }
                        });
                    } }
            ), BorderLayout.CENTER);
        } }, BorderLayout.CENTER);
        //нижняя панель
        main.add(new JPanel() { {
        } }, BorderLayout.SOUTH);
        
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
