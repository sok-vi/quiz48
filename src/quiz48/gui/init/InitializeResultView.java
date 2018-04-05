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
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import quiz48.Pointer;
import quiz48.TaskQueue;
import quiz48.db.ConnectDB;
import quiz48.db.orm.Query;
import quiz48.db.orm.QueryResult;
import quiz48.db.orm.TestResultWithRating;
import quiz48.gui.AppIcons;
import quiz48.gui.BottomPanel;
import quiz48.gui.DuplicateCellRenderer;
import quiz48.gui.FilterDlg;
import quiz48.gui.LoadingWindow;
import quiz48.gui.PercentCellValue;
import quiz48.gui.User;

/**
 *
 * @author vasya
 */
public class InitializeResultView {
    
    public final static class ResultViewState {
        public int page = 0;
    }
    
    public interface ShowTestResultView {
        void run(ResultViewState rvs);
    }
    
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
            InitializeResultQuestionsView.SetCurrentTestResult initTestResultView,
            LinkedList<TestResultWithRating> qrl,
            int page_count,
            ResultViewState rvs) {
        main.removeAll();
        main.setLayout(new BorderLayout());
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        
        Pointer<Integer> pageCount = new Pointer<>(page_count);
        Pointer<JButton> prevButton = new Pointer<>(),
                nextButton = new Pointer<>();
        Pointer<JTextField> currPageLabel = new Pointer<>();
        Pointer<MaxPageCountSetter> maxPageCountSetter = new Pointer<>();
        Pointer<JTable> tableView = new Pointer<>();
        
        LoadDBPage load = (page) -> {
            Pointer<Integer> newPage = new Pointer<>(page);
            if(newPage.get() >= pageCount.get()) { newPage.put(pageCount.get() - 1); }
            if(newPage.get() < 0) { newPage.put(0); }
            if((int)newPage.get() == (int)rvs.page) { return; }
            
            TaskQueue.instance().addNewTask(() -> {
                LoadingWindow.Callback cb = LoadingWindow.showLoadingWindow(wnd, "Загрузка новой страницы результатов...");
                try {
                    qrl.clear();
                    TestResultWithRating.LoadPageInfo page_info = 
                            TestResultWithRating.loadResults(
                                    conn, 
                                    (entity) -> { 
                                        qrl.add(entity); 
                                    }, 
                                    newPage.get(), 
                                    u.getUserEntity());
                    pageCount.put(page_info.pageCount);
                } catch (SQLException ex) {
                }
                EventQueue.invokeLater(() -> {
                    rvs.page = newPage.get();
                    prevButton.get().setEnabled(rvs.page > 0);
                    nextButton.get().setEnabled(rvs.page < (pageCount.get() - 1));
                    currPageLabel.get().setText(Integer.toString(rvs.page + 1));
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
            add(new JPanel() { {
                setLayout(new FlowLayout(FlowLayout.RIGHT));
                /*add(new JButton() { {
                    setText("Построить отчёт по результатам...");
                    setIcon(AppIcons.instance().get("report32.png"));
                    addActionListener((e) -> {
                    });
                } });*/
                add(new JLabel(AppIcons.instance().get("test_result64.png")));
            } }, BorderLayout.EAST);
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
                            
                        final Pointer<JTable> table = new Pointer<>(this);
                        addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                if(e.getClickCount() > 1) {
                                    int sel = table.get().getSelectedRow();
                                    if(sel >= 0) {
                                        TaskQueue.instance().addNewTask(() -> {
                                            LoadingWindow.Callback cb = LoadingWindow.showLoadingWindow(wnd, "Загрузка ответов на вопросы...");
                                            TestResultWithRating trwr = qrl.get(sel);
                                            LinkedList<QueryResult> queryResults = new LinkedList<>();
                                            LoadingWindow.sleep(2);
                                            try {
                                                Query.loadQuerys(conn, (entity) -> {
                                                    try {
                                                        QueryResult qr0 = QueryResult.loadQueryResult(conn, trwr, entity);
                                                        if(qr0 != null) { queryResults.add(qr0); }
                                                    } catch (SQLException ex) {
                                                        cb.setInformation("Загрузка ответов на вопросы... ошибку", Color.RED);
                                                        LoadingWindow.sleep(3);
                                                        System.exit(0);
                                                    }
                                                }, trwr.test);
                                                cb.setInformation("Загрузка ответов на вопросы... успешно");
                                            } catch (SQLException ex) {
                                                cb.setInformation("Загрузка ответов на вопросы... ошибку", Color.RED);
                                                LoadingWindow.sleep(3);
                                                System.exit(0);
                                            }
                                            EventQueue.invokeLater(() -> {
                                                initTestResultView.run(trwr, queryResults, rvs);
                                            });
                                            LoadingWindow.sleep(1);
                                            cb.exit();
                                        });
                                    }
                                }
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
            add(new JPanel() { {
                JPopupMenu fmenu = new JPopupMenu() { {
                    add(new JMenuItem() { {
                        setText("По названию теста...");
                        addActionListener((e) -> {
                            FilterDlg dlg = new FilterDlg(wnd, FilterDlg.filterType.test, (f) -> {});
                            dlg.setVisible(true);
                        });
                    } });
                    add(new JMenuItem() { {
                        setText("По дате/времени...");
                        addActionListener((e) -> {
                            FilterDlg dlg = new FilterDlg(wnd, FilterDlg.filterType.date, (f) -> {});
                            dlg.setVisible(true);
                        });
                    } });
                    add(new JMenuItem() { {
                        setText("По логину...");
                        addActionListener((e) -> {
                            FilterDlg dlg = new FilterDlg(wnd, FilterDlg.filterType.login, (f) -> {});
                            dlg.setVisible(true);
                        });
                    } });
                    add(new JMenuItem() { {
                        setText("По имени...");
                        addActionListener((e) -> {
                            FilterDlg dlg = new FilterDlg(wnd, FilterDlg.filterType.name, (f) -> {});
                            dlg.setVisible(true);
                        });
                    } });
                } };
                Pointer<JPanel> thisPanel = new Pointer<>(this);
                setLayout(new FlowLayout(FlowLayout.LEFT));
                add(new JButton() { {
                    setIcon(AppIcons.instance().get("add_filter16.png"));
                    setText("Добавить фильтр...");
                    addActionListener((e) -> {
                        fmenu.show(thisPanel.get(), getX(), getY() + getHeight());
                    });
                } });
            } }, BorderLayout.WEST);
            add(new JPanel(), BorderLayout.CENTER);
            add(new JPanel() { {
                setLayout(new FlowLayout());
                add(new JButton() { {
                    setText("<");
                    setEnabled(rvs.page > 0);
                    prevButton.put(this);
                    setToolTipText("Предыдущая страница");
                    addActionListener((e) -> {
                        load.setCurrPage(rvs.page - 1);
                    });
                } });
                add(new JTextField() { {
                    setText(Integer.toString(rvs.page + 1));
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
                    setEnabled(rvs.page + 1 < pageCount.get());
                    addActionListener((e) -> {
                        load.setCurrPage(rvs.page + 1);
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
            ConnectDB conn,
            InitializeResultQuestionsView.SetCurrentTestResult initTestResultView,
            ResultViewState rvs) {
        
        ResultViewState _rvs = (rvs == null) ? new ResultViewState() : rvs;
        LinkedList<TestResultWithRating> list = new LinkedList<>();
        TaskQueue.instance().addNewTask(() -> {
            LoadingWindow.Callback cb = LoadingWindow.showLoadingWindow(wnd, "Загрузка результатов...");
            try {
                //LoadingWindow.sleep(2);
                TestResultWithRating.LoadPageInfo page_info = TestResultWithRating.loadResults(conn, (entity) -> {
                    list.add(entity);
                }, _rvs.page, u.getUserEntity());
                _rvs.page = page_info.currPage;
                cb.setInformation("Загрузка результатов...успешно");
                //LoadingWindow.sleep(1);
                EventQueue.invokeLater(() -> {
                    implInitialize(wnd, main, bottom, initStartWindow, u, conn, initTestResultView, list, page_info.pageCount, _rvs);
                });
            } catch (SQLException ex) {
                cb.setInformation("Загрузка результатов...ошибка", Color.red);
                LoadingWindow.sleep(3);
            }
            cb.exit();
        });
        
    }
}
