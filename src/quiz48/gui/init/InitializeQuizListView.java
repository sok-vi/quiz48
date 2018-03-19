/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.gui.init;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import quiz48.Pointer;
import quiz48.TaskQueue;
import quiz48.db.ConnectDB;
import quiz48.db.orm.Test;
import quiz48.gui.AppIcons;
import quiz48.gui.BottomPanel;
import quiz48.gui.LoadingWindow;
import quiz48.gui.User;

/**
 *
 * @author vasya
 */
public class InitializeQuizListView {
    
    private interface ReloadQuizList {
        void run(int id);
    }
    
    public static void initialize(
            JFrame wnd, 
            JPanel main, 
            BottomPanel bottom, 
            Runnable initStatWindow, 
            User u, 
            ConnectDB conn, 
            InitializeTestView.SetCurrentTest InitTestWindow) {
        
        bottom.clearButtons();
        main.removeAll();
        main.setLayout(new BorderLayout());
        
        Pointer<JButton> resultButton = new Pointer<>();
        
        JPanel _user_login = new JPanel();
        main.add(new JPanel() { {
            setLayout(new BorderLayout());
            add(_user_login, BorderLayout.WEST);
            add(new JPanel(), BorderLayout.CENTER);
            add(new JPanel() { { 
                add(new JButton() { {
                    resultButton.put(this);
                    setText("Результаты");
                    setIcon(AppIcons.instance().get("result_view32.png"));
                    addActionListener((e) -> {
                        initStatWindow.run();
                    });
                } }); 
            } }, BorderLayout.EAST);
        }}, BorderLayout.NORTH);
        u.initializeUserPanel(wnd, _user_login, conn);
        
        DefaultListModel<Test> qListModel = new DefaultListModel<>();
        JList<Test> qList = new JList<>(qListModel);
        
        Font lf = qList.getFont();
        qList.setFont(new Font(lf.getName(), lf.getStyle(), (int)(lf.getSize() * 1.5)));
        
        main.add(new JPanel() { {
            setLayout(new BorderLayout());
            add(new JPanel() { {
                setLayout(new BorderLayout());
                add(new JPanel() { { 
                    setLayout(new BorderLayout());
                    setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
                    add(new JLabel(AppIcons.instance().get("start_cat.gif")), BorderLayout.CENTER);
                } }, BorderLayout.CENTER);
            } }, BorderLayout.WEST);
            add(new JPanel() { {
                setLayout(new BorderLayout());
                add(new JScrollPane(qList), BorderLayout.CENTER);
            } }, BorderLayout.CENTER);
        } }, BorderLayout.CENTER);
        
        Pointer<JButton> startButton = new Pointer<>();
        Pointer<Boolean> isLogin = new Pointer<>(u.isLogin());
        resultButton.get().setEnabled(isLogin.get());
        
        bottom.addButton(new JButton() { {
            setText("Пройти тест");
            setIcon(AppIcons.instance().get("start32.png"));
            setEnabled(isLogin.get());
            startButton.put(this);
            addActionListener((e) -> { InitTestWindow.run(qList.getSelectedValue()); });
        } });
        
        ReloadQuizList loadQuiz = (id) -> {
            TaskQueue.instance().addNewTask(() -> {
                LoadingWindow.Callback cb = LoadingWindow.showLoadingWindow(wnd, "Построение списка заданий...");
                //LoadingWindow.sleep(2);
                EventQueue.invokeLater(() -> { qListModel.clear(); });
                try {
                    Test.loadTest(conn, (entity) -> {
                        EventQueue.invokeLater(() -> { qListModel.addElement(entity); });
                    }, u);
                    //LoadingWindow.sleep(2);
                } catch (SQLException ex) {
                    cb.setInformation("Построение списка заданий... ошибка", Color.red);
                    ex.printStackTrace();
                    EventQueue.invokeLater(() -> {
                        wnd.dispatchEvent(new WindowEvent(wnd, WindowEvent.WINDOW_CLOSING));
                    });
                    LoadingWindow.sleep(2);
                }
                
                if(id >= 0) {
                    EventQueue.invokeLater(() -> {
                        for(int i = 0; i < qListModel.getSize(); ++i) {
                            Test itTest = qListModel.elementAt(i);
                            if(itTest.ID == id) {
                                qList.setSelectedIndex(i);
                                break;
                            }
                        }
                    });
                }
                
                cb.exit();
            });
        };
        
        bottom.addClearPanelListener(() -> { 
            isLogin.put(false);
            u.addLoginListener(null); 
            startButton.get().setEnabled(false); 
        });
        u.addLoginListener((login) -> { 
            if(isLogin.get() != login) {
                Test sel = qList.getSelectedValue();
                loadQuiz.run(sel != null ? sel.ID : -1);
            }
            isLogin.put(login);
            resultButton.get().setEnabled(login);
            startButton.get().setEnabled(login && (qList.getSelectedIndex() >= 0)); 
        });
        
        qList.addListSelectionListener((e) -> {
            startButton.get().setEnabled(
                            isLogin.get() && 
                            (qList.getSelectedIndex() >= 0)
                    );
        });
        qList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if((e.getClickCount() > 1) && 
                        isLogin.get() && 
                        (qList.getSelectedIndex() >= 0)) {
                    InitTestWindow.run(qList.getSelectedValue());
                }
            }
            
        });
        qList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if((e.getKeyCode() == KeyEvent.VK_ENTER) &&
                        isLogin.get() && 
                        (qList.getSelectedIndex() >= 0)) {
                    InitTestWindow.run(qList.getSelectedValue());
                }
            }
            
        });
        
        loadQuiz.run(-1);
        wnd.revalidate();
        wnd.repaint();
    }
}
