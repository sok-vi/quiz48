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
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import quiz48.Pointer;
import quiz48.QuizTimer;
import quiz48.TaskQueue;
import quiz48.db.ConnectDB;
import quiz48.db.orm.Query;
import quiz48.db.orm.QueryResult;
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
    
    private interface AnswerValue {
        String getAnswerValue();
    }
    
    private static final class TextFieldValue implements AnswerValue {
        private final JTextField textField;
        public TextFieldValue(JTextField tf) { textField = tf; }
        @Override
        public String getAnswerValue() { return textField.getText(); }
    }
    
    private static final class ComboBoxValue implements AnswerValue {
        private final JComboBox<String> comboField;
        public ComboBoxValue(JComboBox<String> cf) { comboField = cf; }
        @Override
        public String getAnswerValue() { return (String)comboField.getSelectedItem(); }
        
    }
            
    public static void implInitialize(
            JFrame wnd, 
            JPanel main, 
            BottomPanel bottom, 
            Runnable initStartWindow, 
            User u, 
            ConnectDB conn, 
            Test current, 
            LinkedList<Query> querys, 
            TestResult tresult, 
            InitializeResultQuestionsView.SetCurrentTestResult initResultView) {
        
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
        Pointer<Long> quizTimeoutValue = new Pointer<>(),
                queryTimeoutValue = new Pointer<>();
        
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
            
            deleteQueryTimeoutLabels.put(() -> {
                if(getComponentCount() > 2) {
                    queryTimeout.put(null);
                    remove(2);
                    remove(2);
                    revalidate();
                    repaint();
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
                revalidate();
                repaint();
            });
        } }, _cc);
        
        //панель с вопросом
        Pointer<JLabel> queryTitleLabel = new Pointer<>(),
                queryBodyLabel = new Pointer<>(),
                queryAnswerLabel = new Pointer<>();
        Pointer<JPanel> queryAnswerPanel = new Pointer<>();
        
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
           add(new JPanel() { {//заголовок с указание теста и номера вопроса
               setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
               setLayout(new BorderLayout());
               add(new JLabel() { {
                   queryTitleLabel.put(this);
                   setHorizontalAlignment(JLabel.CENTER);
                   setHorizontalTextPosition(JLabel.CENTER);
               } }, BorderLayout.CENTER);
           } }, BorderLayout.NORTH);
           add(new JPanel() { {//панель с вопросом
               setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
               setLayout(new BorderLayout());
               add(new JLabel() { {
                   queryBodyLabel.put(this);
               } }, BorderLayout.CENTER);
           } }, BorderLayout.CENTER);
           add(new JPanel() { {//натель с контролом для ввода результата
               setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
               setLayout(new BorderLayout());
               add(new JPanel() { {
                   setLayout(new BorderLayout());
                   //панель в которой написано - выберите или введите название
                   add(new JPanel() { {
                       setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 0));
                       setLayout(new BorderLayout());
                       add(new JLabel() { {
                           queryAnswerLabel.put(this);
                       } }, BorderLayout.CENTER);
                   } }, BorderLayout.WEST);
                   //панель с контролом для ввода ответа
                   add(new JPanel() { {
                       setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
                       setLayout(new BorderLayout());
                       queryAnswerPanel.put(this);
                   } }, BorderLayout.CENTER);
                   //весёлая иконка, приглашающая в вводу ответа
                   add(new JLabel() { {
                       setText("jhjhj");
                   } }, BorderLayout.EAST);
               } }, BorderLayout.CENTER);
           } }, BorderLayout.SOUTH);
        } }, _cc);
        
        QuizTimer myTimer = new QuizTimer();
        Pointer<Boolean> usef1 = new Pointer<>(true);
        Pointer<Integer> usefc1 = new Pointer<>(0);
        Pointer<QueryResult> qresult = new Pointer<>();
        Pointer<Timer> timerPtr = new Pointer<>();
        Pointer<AnswerValue> answerPtr = new Pointer<>();
        LinkedList<QueryResult> queryResults = new LinkedList<>();

        Runnable initNewQuery = () -> {
            Query currQuery = querys.get(queryIndex.get());
            if(currQuery.time > 0) {
                if(queryTimeout.get() == null) {
                    createQueryTimeoutLabels.get().run();
                }
            }
            else {
                if(queryTimeout.get() != null) {
                    deleteQueryTimeoutLabels.get().run();
                }
            }
            
            if(queryTimeout.get() != null) {
                queryTimeoutValue.put((long)currQuery.time * 1000);
                queryTimeout.get().setText(QuizTimer.durationFormat(queryTimeoutValue.get(), usef1.get()));
            }
            
            queryTimer.get().setText(QuizTimer.durationFormat(myTimer.getQuestionTimer(), usef1.get()));

            queryTitleLabel.get().setText(
                    String.format("<html>"
                            + "<div style=\"color: blue; font-size: 24pt;\">"
                            + "<strong>%1$s (<span style=\"color: green;\">вопрос №%2$s</span>)</strong>"
                            + "</div>"
                            + "</html>", current.name, Integer.toHexString(queryIndex.get() + 1)));
            queryBodyLabel.get().setText(currQuery.Query);
            
            queryAnswerPanel.get().removeAll();
            if(currQuery.isFix) {
                queryAnswerPanel.get().add(new JTextField() { {
                    answerPtr.put(new TextFieldValue(this));
                } }, BorderLayout.CENTER);
                queryAnswerLabel.get().setText("Введите значение:");
            }
            else {
                queryAnswerPanel.get().add(new JComboBox<String>() { {
                    this.addItem("");
                    for(int i = 0; i < currQuery.answers.size(); ++i) {
                        this.addItem(currQuery.answers.get(i));
                    }
                    answerPtr.put(new ComboBoxValue(this));
                } }, BorderLayout.CENTER);
                queryAnswerLabel.get().setText("Выберите вариант из списка:");
            }
        };
        
        bottom.clearButtons();
        initNewQuery.run();

        bottom.addButton(new JButton() { {
            setText("Следующий вопрос>");
            setIcon(AppIcons.instance().get("next_q32.png"));
            addActionListener((e) -> {
                TaskQueue.instance().addNewTask(() -> {
                    LoadingWindow.Callback cb = LoadingWindow.showLoadingWindow(wnd, "Загрузка следующего вопроса...");
                    
                    try {
                        //сначала сохраним результат в бд
                        //LoadingWindow.sleep(1);
                        cb.setInformation("Обновление результатов...");
                        myTimer.stop();//остановили счётчик
                        if(qresult.get() != null) {
                            //если был превышен таймаут вопроса сущность в бд уже создана
                            //обновим время
                            qresult.get().time((int)Math.round(((double)myTimer.getQuestionTimer()) / 1000), conn);
                            //обновим результат
                            qresult.get().answer(answerPtr.get().getAnswerValue(), conn);
                            queryResults.add(qresult.get());
                            qresult.put(null);//сбросили сущность бд
                        }
                        else {
                            queryResults.add(
                                    QueryResult.saveQueryResult(
                                            conn, 
                                            tresult, 
                                            querys.get(queryIndex.get()), 
                                            (int)Math.round(((double)myTimer.getQuestionTimer()) / 1000), 
                                            answerPtr.get().getAnswerValue(), 
                                            answerPtr.get().getAnswerValue().compareToIgnoreCase(
                                                    querys.get(queryIndex.get()).Answer) == 0 ? QueryResult.fail.ok : QueryResult.fail.fail));
                            
                        }

                        //LoadingWindow.sleep(2);
                        tresult.time((int)Math.round(((double)myTimer.getQuizTimer()) / 1000), conn);
                        cb.setInformation("Вывод нового вопроса...");

                        //установим счётчик на следующий вопрос
                        queryIndex.put(queryIndex.get() + 1);
                        if(queryIndex.get() < querys.size()) {
                            //ещё остались вопросы --- переходим к следующему шагу
                            EventQueue.invokeAndWait(() -> {
                                initNewQuery.run();//вывод пороса
                            });
                        }
                        else {
                            //auf wiedersehen
                            EventQueue.invokeAndWait(() -> {
                                timerPtr.get().stop();//остановить таймер
                                initResultView.run(tresult, queryResults);//вывод результатов
                            });
                        }
                        
                        myTimer.resetQuestionTimer();//сбросили счётчик времени вороса
                        myTimer.start();//запустили счётчик
                    }
                    catch(InterruptedException|InvocationTargetException exe) {
                        cb.setInformation("Ошибка при обновлении...", Color.RED);
                        LoadingWindow.sleep(3);
                        System.exit(0);
                    }
                    catch(SQLException exes) {
                        cb.setInformation("Ошибка базы данных...", Color.RED);
                        LoadingWindow.sleep(3);
                        //System.out.println(exes);
                        exes.printStackTrace();
                        System.exit(0);
                    }
                    cb.exit();
                });
            });
        } });
        
        bottom.addButton(new JButton() { {
            setText("<К списку задач");
            setHorizontalTextPosition(JButton.LEFT);
            setIcon(AppIcons.instance().get("qlist32.png"));
            addActionListener((e) -> { initStartWindow.run(); });
        } });
        
        wnd.revalidate();
        wnd.repaint();
        
        quizTimer.get().setText(QuizTimer.durationFormat(myTimer.getQuizTimer(), usef1.get()));
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
                if(queryTimeout.get() != null) {
                    long query_timeout_balance = queryTimeoutValue.get() - myTimer.getQuestionTimer();
                    if(query_timeout_balance > 0) {
                        queryTimeout.get().setText(QuizTimer.durationFormat(query_timeout_balance, usef1.get()));
                    }
                    else {
                        TaskQueue.instance().addNewTask(() -> {
                            if(qresult.get() == null) {
                                LoadingWindow.Callback cb = LoadingWindow.showLoadingWindow(wnd, "Время на вопрос закончилось!!!");
                                myTimer.stop();//остановили счётчик
                                try {
                                    LoadingWindow.sleep(2);
                                    qresult.put(QueryResult.saveQueryResult(conn, tresult, querys.get(queryIndex.get()), 0, "", QueryResult.fail.timeout));
                                } catch (SQLException ex) {
                                    cb.setInformation("Ошибка базы данных", Color.red);
                                    LoadingWindow.sleep(3);
                                    System.exit(0);
                                }
                                myTimer.start();//запустили счётчик
                                cb.exit();
                            }
                        });
                        
                        queryTimeout.get().setText(QuizTimer.durationFormat(query_timeout_balance * -1, usef1.get()));
                        queryTimeout.get().setForeground(Color.red);
                    }
                }
            });
        });
        timerPtr.put(time);
        myTimer.start();
        time.start();
    }
    
    public static void initialize(
            JFrame wnd, 
            JPanel main, 
            BottomPanel bottom, 
            Runnable initStartWindow, 
            User u, 
            ConnectDB conn, 
            Test current, 
            InitializeResultQuestionsView.SetCurrentTestResult initResultView) {
        
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
                    InitializeTestView.implInitialize(wnd, main, bottom, initStartWindow, u, conn, current, querys, tres.get(), initResultView); 
                });
            }
        });
    }
}
