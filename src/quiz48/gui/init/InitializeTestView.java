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
import quiz48.TaskQueue;
import quiz48.db.ConnectDB;
import quiz48.db.orm.Query;
import quiz48.db.orm.Test;
import quiz48.gui.AppIcons;
import quiz48.gui.BottomPanel;
import quiz48.gui.LoadingWindow;
import quiz48.gui.User;

/**
 *
 * @author vasya
 */
public class InitializeTestView {
    
    private static final class QuizTimer {
        private long m_QuizTimer,
                m_QuestionTimer, 
                m_TimerValue;
        private boolean m_Started;
        private final Object m_Synch;
        
        public QuizTimer(boolean start) {
            m_Synch = new Object();
            synchronized(m_Synch) {
                m_QuizTimer = 0;
                m_QuestionTimer = 0;
                m_Started = start;
                m_TimerValue = 0;
                if(start) { start(); }
            }
        }
        
        public QuizTimer() { this(false); }
        
        public final void update() {
            synchronized(m_Synch) {
                if(m_Started) {
                    long newt = System.currentTimeMillis();
                    m_QuestionTimer += newt - m_TimerValue;
                    m_QuizTimer += newt - m_TimerValue;
                    m_TimerValue = newt;
                }
            }
        }
        
        public final void start() {
            synchronized(m_Synch) {
                if(!m_Started) {
                    m_TimerValue = System.currentTimeMillis();
                    m_Started = true;
                }
            }
        }
        
        public final void stop() {
            synchronized(m_Synch) {
                if(m_Started) {
                    update();
                    m_Started = false;
                }
            }
        }
        
        public final long getQuizTimer() {
            synchronized(m_Synch) {
                return m_QuizTimer;
            }
        }
        
        public final long getQuestionTimer() {
            synchronized(m_Synch) {
                return m_QuestionTimer;
            }
        }
        
        public final void resetQuizTimer() {
            synchronized(m_Synch) {
                m_QuizTimer = 0;
            }
        }
        
        public final void resetQuestionTimer() {
            synchronized(m_Synch) {
                m_QuestionTimer = 0;
            }
        }
        
        public static String durationFormat(long d, boolean isTSPC) {
            String format1 = "%1$02d:%2$02d:%3$02d",
                    format2 = "%1$02d %2$02d %3$02d";
            int _h = (int)(d / (60 * 60 * 1000)),
                    _m = (int)(d / (60 * 1000)) - _h * 60,
                    _s = (int)(d / 1000) - _m * 60 - _h * 60 * 60;
            return String.format(isTSPC ? format1 : format2, _h, _m, _s);
        }
    }
    
    public interface SetCurrentTest {
        void run(Test t);
    }
            
    public static void initialize(JFrame wnd, JPanel main, BottomPanel bottom, Runnable initStartWindow, User u, ConnectDB conn, Test current) {
        LinkedList<Query> querys = new LinkedList<>();
        
        TaskQueue.instance().addNewTask(() -> {
            LoadingWindow.Callback cb = LoadingWindow.showLoadingWindow(wnd, "Построение списка вопросов...");
            try {
                Query.loadQuery(conn, (q) -> {
                    querys.add(q);
                }, current);
            } catch (SQLException ex) {
            }
            cb.exit();
        });
        
        main.removeAll();
        main.setLayout(new BorderLayout());
        
        main.setLayout(new GridBagLayout());
        GridBagConstraints _cc = new GridBagConstraints();
        Insets _is1 = new Insets(5, 5, 5, 5),
                _is2 = new Insets(0, 0, 3, 3);
        
        //формируем панель сверху
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
        
        //панель конкретоного вопроса
        Pointer<JLabel> queryTimer = new Pointer<>();
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
                setText(u.getUserEntity().getLogin());
                setForeground(Color.blue);
            } }, _cc0);
        } }, _cc);
        
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
            setBackground(Color.red);
        } }, _cc);
        
        bottom.clearButtons();

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
}
