/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.gui.init;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import quiz48.Pointer;
import quiz48.QuizTimer;
import quiz48.TaskQueue;
import quiz48.db.ConnectDB;
import quiz48.db.orm.Query;
import quiz48.db.orm.Test;
import quiz48.db.orm.TestResult;
import quiz48.gui.AppIcons;
import quiz48.gui.BottomPanel;
import quiz48.gui.LoadingWindow;
import quiz48.gui.User;

/**
 *
 * @author vasya
 */
public class InitializeTestView {
    
    public interface SetCurrentTest {
        void run(Test t);
    }
            
    public static void implInitialize(JFrame wnd, JPanel main, BottomPanel bottom, Runnable initStartWindow, User u, ConnectDB conn, Test current, LinkedList<Query> querys, TestResult tresult) {
        Pointer<Integer> queryIndex = new Pointer<>(0);
        
        main.removeAll();
        main.setLayout(new BorderLayout());
        
        main.setLayout(new GridBagLayout());
        GridBagConstraints _cc = new GridBagConstraints();
        Insets _is1 = new Insets(5, 5, 5, 5),
                _is2 = new Insets(0, 0, 3, 3);
        
        //левая панель
        /////////////////////
        //формируем панель с логином
        _cc.gridx = 0;
        _cc.gridy = 0;
        _cc.weightx = 0;
        _cc.weighty = 0;
        _cc.gridwidth = 1;
        _cc.gridheight = 1;
        _cc.insets = _is1;
        _cc.fill = GridBagConstraints.BOTH;
        _cc.anchor = GridBagConstraints.CENTER;
        main.add(new JPanel() { {
            setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Пользователь"));
            setLayout(new GridBagLayout());
            
            GridBagConstraints _cc0 = new GridBagConstraints();
            _cc0.gridx = 0;
            _cc0.gridy = 0;
            _cc0.weightx = 0;
            _cc0.weighty = 0;
            _cc0.gridwidth = 1;
            _cc0.gridheight = 1;
            _cc0.insets = _is2;
            _cc0.fill = GridBagConstraints.NONE;
            _cc0.anchor = GridBagConstraints.EAST;
            add(new JLabel() { {
                setText(":");
                setIcon(AppIcons.instance().get("user24.png"));
            } }, _cc0);
            
            _cc0.gridx = 1;
            _cc0.gridy = 0;
            _cc0.weightx = 0;
            _cc0.weighty = 0;
            _cc0.gridwidth = 1;
            _cc0.gridheight = 1;
            _cc0.insets = _is2;
            _cc0.fill = GridBagConstraints.NONE;
            _cc0.anchor = GridBagConstraints.WEST;
            add(new JLabel() { {
                setText(u.getUserEntity().getName());
                setForeground(Color.blue);
            } }, _cc0);
            
            _cc0.gridx = 0;
            _cc0.gridy = 1;
            _cc0.weightx = 0;
            _cc0.weighty = 0;
            _cc0.gridwidth = 1;
            _cc0.gridheight = 1;
            _cc0.insets = _is2;
            _cc0.fill = GridBagConstraints.NONE;
            _cc0.anchor = GridBagConstraints.EAST;
            add(new JLabel(":"), _cc0);
            
            _cc0.gridx = 1;
            _cc0.gridy = 1;
            _cc0.weightx = 0;
            _cc0.weighty = 0;
            _cc0.gridwidth = 1;
            _cc0.gridheight = 1;
            _cc0.insets = _is2;
            _cc0.fill = GridBagConstraints.NONE;
            _cc0.anchor = GridBagConstraints.WEST;
            add(new JLabel() { {
                setText(u.getUserEntity().getLogin());
                setForeground(Color.blue);
            } }, _cc0);
        } }, _cc);
        
        //панель учёта времени всего теста
        Pointer<JLabel> quizTimer = new Pointer<>(),
                quizTimeout = new Pointer<>();
        Pointer<Long> quizTimeoutValue = new Pointer<>();
        
        _cc.gridx = 0;
        _cc.gridy = 1;
        _cc.weightx = 0;
        _cc.weighty = 0;
        _cc.gridwidth = 1;
        _cc.gridheight = 1;
        _cc.insets = _is1;
        _cc.fill = GridBagConstraints.BOTH;
        _cc.anchor = GridBagConstraints.CENTER;
        main.add(new JPanel() { {
            setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Время теста"));
            setLayout(new GridBagLayout());
            
            GridBagConstraints _cc0 = new GridBagConstraints();
            _cc0.gridx = 0;
            _cc0.gridy = 0;
            _cc0.weightx = 0;
            _cc0.weighty = 0;
            _cc0.gridwidth = 1;
            _cc0.gridheight = 1;
            _cc0.insets = _is2;
            _cc0.fill = GridBagConstraints.NONE;
            _cc0.anchor = GridBagConstraints.EAST;
            add(new JLabel() { {
                setText(":");
                setIcon(AppIcons.instance().get("timer24.gif"));
            } }, _cc0);
            
            _cc0.gridx = 1;
            _cc0.gridy = 0;
            _cc0.weightx = 0;
            _cc0.weighty = 0;
            _cc0.gridwidth = 1;
            _cc0.gridheight = 1;
            _cc0.insets = _is2;
            _cc0.fill = GridBagConstraints.NONE;
            _cc0.anchor = GridBagConstraints.WEST;
            add(new JLabel() { {
                quizTimer.put(this);
                setText(u.getUserEntity().getName());
                setForeground(Color.blue);
            } }, _cc0);
            
            if(current.time > 0) {
                //общее время теста ограничено
                quizTimeoutValue.put((long)(current.time * 1000));
                _cc0.gridx = 0;
                _cc0.gridy = 1;
                _cc0.weightx = 0;
                _cc0.weighty = 0;
                _cc0.gridwidth = 1;
                _cc0.gridheight = 1;
                _cc0.insets = _is2;
                _cc0.fill = GridBagConstraints.NONE;
                _cc0.anchor = GridBagConstraints.EAST;
                add(new JLabel() { {
                    setText(":");
                    setIcon(AppIcons.instance().get("timeout24.gif"));
                } }, _cc0);

                _cc0.gridx = 1;
                _cc0.gridy = 1;
                _cc0.weightx = 0;
                _cc0.weighty = 0;
                _cc0.gridwidth = 1;
                _cc0.gridheight = 1;
                _cc0.insets = _is2;
                _cc0.fill = GridBagConstraints.NONE;
                _cc0.anchor = GridBagConstraints.WEST;
                add(new JLabel() { {
                    quizTimeout.put(this);
                    setText(u.getUserEntity().getLogin());
                    setForeground(Color.blue);
                } }, _cc0);
            }
        } }, _cc);
        
        //панель времени конкретоного вопроса
        Pointer<JLabel> queryTimer = new Pointer<>(),
                queryTimeout = new Pointer<>();
        Pointer<Runnable> createQueryTimeoutLabels = new Pointer<>(),
                deleteQueryTimeoutLabels = new Pointer<>();
        _cc.gridx = 0;
        _cc.gridy = 2;
        _cc.weightx = 0;
        _cc.weighty = 0;
        _cc.gridwidth = 1;
        _cc.gridheight = 1;
        _cc.insets = _is1;
        _cc.fill = GridBagConstraints.BOTH;
        _cc.anchor = GridBagConstraints.CENTER;
        main.add(new JPanel() { {
            setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Вопрос"));
            setLayout(new GridBagLayout());
            
            GridBagConstraints _cc0 = new GridBagConstraints();
            _cc0.gridx = 0;
            _cc0.gridy = 0;
            _cc0.weightx = 0;
            _cc0.weighty = 0;
            _cc0.gridwidth = 1;
            _cc0.gridheight = 1;
            _cc0.insets = _is2;
            _cc0.fill = GridBagConstraints.NONE;
            _cc0.anchor = GridBagConstraints.EAST;
            add(new JLabel() { {
                setText(":");
                setIcon(AppIcons.instance().get("timer24.gif"));
            } }, _cc0);
            
            _cc0.gridx = 1;
            _cc0.gridy = 0;
            _cc0.weightx = 0;
            _cc0.weighty = 0;
            _cc0.gridwidth = 1;
            _cc0.gridheight = 1;
            _cc0.insets = _is2;
            _cc0.fill = GridBagConstraints.NONE;
            _cc0.anchor = GridBagConstraints.WEST;
            add(new JLabel() { {
                queryTimer.put(this);
                setText(u.getUserEntity().getName());
                setForeground(Color.blue);
            } }, _cc0);
            
            createQueryTimeoutLabels.put(() -> {
                if(getComponentCount() > 2) {
                    remove(2);
                    remove(2);
                    queryTimeout.put(null);
                }
            });
            
            createQueryTimeoutLabels.put(() -> {
                _cc0.gridx = 0;
                _cc0.gridy = 1;
                _cc0.weightx = 0;
                _cc0.weighty = 0;
                _cc0.gridwidth = 1;
                _cc0.gridheight = 1;
                _cc0.insets = _is2;
                _cc0.fill = GridBagConstraints.NONE;
                _cc0.anchor = GridBagConstraints.EAST;
                add(new JLabel() { {
                    setText(":");
                    setIcon(AppIcons.instance().get("timeout24.gif"));
                } }, _cc0);

                _cc0.gridx = 1;
                _cc0.gridy = 1;
                _cc0.weightx = 0;
                _cc0.weighty = 0;
                _cc0.gridwidth = 1;
                _cc0.gridheight = 1;
                _cc0.insets = _is2;
                _cc0.fill = GridBagConstraints.NONE;
                _cc0.anchor = GridBagConstraints.WEST;
                add(new JLabel() { {
                    queryTimeout.put(this);
                    setText(u.getUserEntity().getLogin());
                    setForeground(Color.blue);
                } }, _cc0);
            });
        } }, _cc);
        
        //панель с вопросом
        Pointer<JLabel> queryTitleLabel = new Pointer<>(),
                queryBodyLabel = new Pointer<>();
        
        _cc.gridx = 1;
        _cc.gridy = 0;
        _cc.weightx = 100;
        _cc.weighty = 100;
        _cc.gridwidth = 1;
        _cc.gridheight = 4;
        _cc.insets = _is1;
        _cc.fill = GridBagConstraints.BOTH;
        _cc.anchor = GridBagConstraints.CENTER;
       main.add(new JPanel() { {
           setLayout(new BorderLayout());
           add(new JPanel() { {
               setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
               setLayout(new BorderLayout());
               add(new JLabel() { {
                   queryTitleLabel.put(this);
                   //setForeground(Color.blue);
                   setHorizontalAlignment(JLabel.CENTER);
                   setHorizontalTextPosition(JLabel.CENTER);
                   //java.awt.Font fnt = getFont();
                   //setFont(new java.awt.Font(fnt.getName(), java.awt.Font.BOLD, (int)(fnt.getSize() * 1.7)));
               } }, BorderLayout.CENTER);
           } }, BorderLayout.NORTH);
           add(new JPanel() { {
               setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
               setLayout(new BorderLayout());
               add(new JLabel() { {
                   queryBodyLabel.put(this);
               } }, BorderLayout.CENTER);
           } }, BorderLayout.CENTER);
           add(new JPanel() { {
               setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
               setLayout(new BorderLayout());
               add(new JLabel() { {
                   setText("opjkopjpokipoj0iojp");
               } }, BorderLayout.CENTER);
           } }, BorderLayout.SOUTH);
        } }, _cc);
        
        bottom.clearButtons();
        Runnable initNewQuery = () -> {
            Query currQuery = querys.get(queryIndex.get());
            if(currQuery.time > 0) {
                if(queryTimeout.get() == null) {
                    createQueryTimeoutLabels.get().run();
                }
            }
            else {
                if(queryTimeout.get() == null) {
                    deleteQueryTimeoutLabels.get().run();
                }
            }

            //queryTitleLabel.get().setText(current.name);
            queryTitleLabel.get().setText(
                    String.format("<html><div style=\"color: blue; font-size: 24pt;\"><strong>%1$s (<span style=\"color: green;\">вопрос №%2$s</span>)</strong></div></html>", current.name, Integer.toHexString(queryIndex.get() + 1)));
            queryBodyLabel.get().setText(currQuery.Query);
        };
        
        initNewQuery.run();

        bottom.addButton(new JButton() { {
            setText("Следующий вопрос>");
            setIcon(AppIcons.instance().get("next_q32.png"));
            addActionListener((e) -> { });
        } });
        
        bottom.addButton(new JButton() { {
            setText("<К списку задач");
            setHorizontalTextPosition(JButton.LEFT);
            setIcon(AppIcons.instance().get("qlist32.png"));
            addActionListener((e) -> { initStartWindow.run(); });
        } });
        
        wnd.revalidate();
        wnd.repaint();
        
        QuizTimer myTimer = new QuizTimer();
        Pointer<Boolean> usef1 = new Pointer<>(true);
        Pointer<Integer> usefc1 = new Pointer<>(0);

        quizTimer.get().setText(QuizTimer.durationFormat(myTimer.getQuizTimer(), usef1.get()));
        queryTimer.get().setText(QuizTimer.durationFormat(myTimer.getQuestionTimer(), usef1.get()));
        if(quizTimeout.get() != null) {
            quizTimeout.get().setText(QuizTimer.durationFormat(quizTimeoutValue.get(), usef1.get()));
        }
        
        final Timer time = new Timer(500, (e) -> {
            usefc1.put(usefc1.get() + 1);
            if(usefc1.get() == 2) {
                usefc1.put(0);
                usef1.put(!usef1.get());
            }
            myTimer.update();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
            EventQueue.invokeLater(() -> {
                quizTimer.get().setText(QuizTimer.durationFormat(myTimer.getQuizTimer(), usef1.get()));
                if(quizTimeout.get() != null) {
                    long quiz_timeout_balance = quizTimeoutValue.get() - myTimer.getQuizTimer();
                    if(quiz_timeout_balance > 0) {
                        quizTimeout.get().setText(QuizTimer.durationFormat(quiz_timeout_balance, usef1.get()));
                    }
                    else {
                        quizTimeout.get().setText(QuizTimer.durationFormat(quiz_timeout_balance * -1, usef1.get()));
                        quizTimeout.get().setForeground(Color.red);
                    }
                }
                queryTimer.get().setText(QuizTimer.durationFormat(myTimer.getQuestionTimer(), usef1.get()));
            });
        });
        myTimer.start();
        time.start();
    }
    
    public static void initialize(JFrame wnd, JPanel main, BottomPanel bottom, Runnable initStartWindow, User u, ConnectDB conn, Test current) {
        LinkedList<Query> querys = new LinkedList<>();
        Pointer<TestResult> tres = new Pointer<>();
        
        TaskQueue.instance().addNewTask(() -> {
            LoadingWindow.Callback cb = LoadingWindow.showLoadingWindow(wnd, "Построение списка вопросов...");
            
            try {
                //LoadingWindow.sleep(2);
                Query.loadQuery(conn, (q) -> {
                    querys.add(q);
                }, current);
                if(querys.size() > 0) {
                    cb.setInformation("Построение списка вопросов...успешно");
                }
                else {
                    cb.setInformation("Построение списка вопросов...пусто", Color.red);
                }
                //LoadingWindow.sleep(2);
            } catch (SQLException ex) {
                cb.setInformation("Построение списка вопросов...ошибка", Color.red);
                LoadingWindow.sleep(3);
            }
            
            try {
                cb.setInformation("Создание списка результатов...");
                //LoadingWindow.sleep(2);
                tres.put(TestResult.createTestResult(conn, u.getUserEntity(), current));
                cb.setInformation("Создание списка результатов...успешно");
                //LoadingWindow.sleep(2);
            } catch (SQLException ex) {
                cb.setInformation("Создание списка результатов...ошибка", Color.red);
                LoadingWindow.sleep(3);
            }
            cb.exit();
            
            if((querys.size() > 0) && (tres.get() != null)) {
                EventQueue.invokeLater(() -> { 
                    InitializeTestView.implInitialize(wnd, main, bottom, initStartWindow, u, conn, current, querys, tres.get()); 
                });
            }
        });
    }
}
