/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.gui.init;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import quiz48.Pointer;
import quiz48.TaskQueue;
import quiz48.db.ConnectDB;
import quiz48.db.orm.TestResultWithRating;
import quiz48.gui.AppIcons;
import quiz48.gui.BottomPanel;
import quiz48.gui.DuplicateCellRenderer;
import quiz48.gui.LoadingWindow;
import quiz48.gui.PercentCellValue;
import quiz48.gui.User;

/**
 *
 * @author vasya
 */
public class InitializeResultView {
    private static final class MaxPageCountSetter {
        private final JLabel label;
        public MaxPageCountSetter(JLabel label) { this.label = label; }
        public final void setMaxPasgeCount(int pageCount) { label.setText(String.format("из %1$s", Integer.toString(pageCount))); }
    }
    
    private interface LoadDBPage {
        void setCurrPage(int pageNum);
    }
    
    private static void implInitialize(
            JFrame wnd, 
            JPanel main, 
            BottomPanel bottom, 
            Runnable initStartWindow, 
            User u, 
            ConnectDB conn,
            LinkedList<TestResultWithRating> qrl,
            int page_count) {
        main.removeAll();
        main.setLayout(new BorderLayout());
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        
        Pointer<Integer> currPage = new Pointer<>(0),
                pageCount = new Pointer<>(page_count);
        Pointer<JButton> prevButton = new Pointer<>(),
                nextButton = new Pointer<>();
        Pointer<JTextField> currPageLabel = new Pointer<>();
        Pointer<MaxPageCountSetter> maxPageCountSetter = new Pointer<>();
        Pointer<JTable> tableView = new Pointer<>();
        
        LoadDBPage load = (page) -> {
            Pointer<Integer> newPage = new Pointer<>(page);
            if(newPage.get() >= pageCount.get()) { newPage.put(pageCount.get() - 1); }
            if(newPage.get() < 0) { newPage.put(0); }
            if((int)newPage.get() == (int)currPage.get()) { return; }
            
            TaskQueue.instance().addNewTask(() -> {
                LoadingWindow.Callback cb = LoadingWindow.showLoadingWindow(wnd, "Загрузка новой страницы результатов...");
                try {
                    qrl.clear();
                    pageCount.put(TestResultWithRating.loadResults(conn, (entity) -> {
                        qrl.add(entity);
                    }, newPage.get(), u.getUserEntity()));
                } catch (SQLException ex) {
                }
                EventQueue.invokeLater(() -> {
                    currPage.put(newPage.get());
                    prevButton.get().setEnabled(currPage.get() > 0);
                    nextButton.get().setEnabled(currPage.get() < (pageCount.get() - 1));
                    currPageLabel.get().setText(Integer.toString(currPage.get() + 1));
                    currPageLabel.get().setBackground(UIManager.getColor("TextField.background"));
                    maxPageCountSetter.get().setMaxPasgeCount(pageCount.get());
                    tableView.get().revalidate();
                    tableView.get().repaint();
                });
                cb.exit();
            });
        };
        
        //верхняя панель
        main.add(new JPanel() { {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(0, 7, 0, 7));
            add(new JLabel(String.format(
                            "<html><table><tr><td><img src=\"%4$s\"/></td><td>%1$s  {<strong>%2$s</strong>}%3$s</td></tr?</table></html>", 
                            u.getUserEntity().getName(), 
                            u.getUserEntity().getLogin(), 
                            u.getUserEntity().isAdmin ? 
                                    String.format(
                                            " <img src=\"%1$s\"/>", 
                                            AppIcons.instance().get("stat_admin16.png").toString()) :
                                    "",
                            AppIcons.instance().get("user48.png").toString())), BorderLayout.WEST);
            add(new JPanel(), BorderLayout.CENTER);
            add(new JLabel(AppIcons.instance().get("test_result64.png")), BorderLayout.EAST);
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
                        setDefaultRenderer(Boolean.class, new DuplicateCellRenderer());
                        setDefaultRenderer(PercentCellValue.class, new PercentCellValue.PercentCellValueRenderer());
                        setRowMargin(2);
                        getTableHeader().setReorderingAllowed(false);
                        getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        tableView.put(this);
                        
                        setModel(new AbstractTableModel() {
                            @Override
                            public int getRowCount() { return qrl.size(); }

                            @Override
                            public int getColumnCount() {
                                return 6;
                            }
                        
                            @Override
                            public Object getValueAt(int rowIndex, int columnIndex) {
                                switch(columnIndex) {
                                    case 0:
                                        return qrl.get(rowIndex).test.name;
                                    case 1:
                                        return df.format(qrl.get(rowIndex).date);
                                    case 2:
                                        return String.format("%1$s [%2$s]", qrl.get(rowIndex).user.getName(), qrl.get(rowIndex).user.getLogin());
                                    case 3:
                                        return quiz48.QuizTimer.durationFormat(qrl.get(rowIndex).time() * 1000, true);
                                    case 4:
                                        return new PercentCellValue(qrl.get(rowIndex).rating);
                                    case 5:
                                        return qrl.get(rowIndex).duplicate;
                                }
                                return 0;
                            }
                            
                            @Override
                            public String getColumnName(int column) {
                                switch(column) {
                                    case 0:
                                        return "Название";
                                    case 1:
                                        return "Дата/время";
                                    case 2:
                                        return "Тестируемый";
                                    case 3:
                                        return "Затрачено, с.";
                                    case 4:
                                        return "Оценка, %";
                                    case 5:
                                        return "Повторно";
                                }
                                
                                return super.getColumnName(column);
                            }

                            @Override
                            public Class<?> getColumnClass(int columnIndex) {
                                switch(columnIndex) {
                                    case 4:
                                        return PercentCellValue.class;
                                    case 5:
                                        return Boolean.class;
                                }
                                
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
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(0, 7, 0, 7));
            add(new JPanel(), BorderLayout.WEST);
            add(new JPanel(), BorderLayout.CENTER);
            add(new JPanel() { {
                setLayout(new FlowLayout());
                add(new JButton() { {
                    setText("<");
                    setEnabled(false);
                    prevButton.put(this);
                    setToolTipText("Предыдущая страница");
                    addActionListener((e) -> {
                        load.setCurrPage(currPage.get() - 1);
                    });
                } });
                add(new JTextField() { {
                    setText("1");
                    setColumns(4);
                    setHorizontalAlignment(JTextField.CENTER);
                    currPageLabel.put(this);
                    addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyPressed(KeyEvent e) {
                            if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                                try {
                                    load.setCurrPage(Integer.parseInt(currPageLabel.get().getText()) - 1);
                                }
                                catch(NumberFormatException ex) {
                                    currPageLabel.get().setBackground(Color.red);
                                }
                            }
                            super.keyPressed(e);
                        }
                       
                    });
                } });
                add(new JLabel() { {
                    maxPageCountSetter.put(new MaxPageCountSetter(this));
                    maxPageCountSetter.get().setMaxPasgeCount(pageCount.get());
                } });
                add(new JButton() { {
                    setText(">");
                    nextButton.put(this);
                    if(pageCount.get() == 1) {
                        setEnabled(false);
                    }
                    addActionListener((e) -> {
                        load.setCurrPage(currPage.get() + 1);
                    });
                    setToolTipText("Следующая страница");
                } });
            } }, BorderLayout.EAST);
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
    public static void initialize(
            JFrame wnd, 
            JPanel main, 
            BottomPanel bottom, 
            Runnable initStartWindow, 
            User u, 
            ConnectDB conn) {
        
        LinkedList<TestResultWithRating> list = new LinkedList<>();
        TaskQueue.instance().addNewTask(() -> {
            LoadingWindow.Callback cb = LoadingWindow.showLoadingWindow(wnd, "Загрузка результатов...");
            try {
                //LoadingWindow.sleep(2);
                int page_count = TestResultWithRating.loadResults(conn, (entity) -> {
                    list.add(entity);
                }, 0, u.getUserEntity());
                cb.setInformation("Загрузка результатов...успешно");
                //LoadingWindow.sleep(1);
                EventQueue.invokeLater(() -> {
                    implInitialize(wnd, main, bottom, initStartWindow, u, conn, list, page_count);
                });
            } catch (SQLException ex) {
                cb.setInformation("Загрузка результатов...ошибка", Color.red);
                LoadingWindow.sleep(3);
            }
            cb.exit();
        });
        
    }
}
