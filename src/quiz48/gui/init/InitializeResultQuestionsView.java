/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.gui.init;

import quiz48.gui.PercentCellValue;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import quiz48.Pointer;
import quiz48.db.ConnectDB;
import quiz48.db.orm.QueryResult;
import quiz48.db.orm.TestResult;
import quiz48.gui.AppIcons;
import quiz48.gui.BottomPanel;
import quiz48.gui.QueryResultDetailViewDlg;

/**
 *
 * @author vasya
 */
public class InitializeResultQuestionsView {
    
    public interface SetCurrentTestResult {
        void run(TestResult r, List<QueryResult> qrs);
    }
    
    private final static class ResultCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            QueryResult.fail resultValue = (QueryResult.fail)value;
            setBackground(resultValue.getResultColor());
            return super.getTableCellRendererComponent(table, resultValue.getResultString(), isSelected, hasFocus, row, column);
        }
    }
    
    public static void initialize(
            JFrame wnd, 
            JPanel main, 
            BottomPanel bottom, 
            Runnable initStartWindow, 
            Runnable initStatWindow, 
            ConnectDB conn, 
            TestResult current,
            List<QueryResult> qresults) {
        
        Pointer<Integer> sumWeight = new Pointer<>(0),
                sumResult = new Pointer<>(0);
        for(QueryResult qr : qresults) {
            sumWeight.put(sumWeight.get() + qr.query.weight);
            sumResult.put(sumResult.get() + (qr.fail() == QueryResult.fail.ok ? qr.query.weight : 0));
        }
        
        if(sumWeight.get() == 0) { sumWeight.put(0); }
        
        main.removeAll();
        main.setLayout(new BorderLayout());
        
        bottom.clearButtons();
        
        main.add(new JPanel() { {
            setLayout(new BorderLayout());
            add(new JPanel() { {
                setLayout(new BorderLayout());
                setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                add(
                        new JLabel(
                                String.format(
                                        "<html>"
                                        + "<div style=\"font-size: 24pt; color: blue;\">Тест: <span style=\"color: green;\"><strong>%1$s</strong></span></div>"
                                        + "<div style=\"font-size: 20pt; color: blue;\">Затрачено: <span style=\"color: green;\"><strong>%2$s%3$s</strong></span></div>"
                                        + "<div style=\"font-size: 20pt; color: blue;\">Дата теста: <span style=\"color: green;\"><strong>%4$s</strong></span></div>"
                                                + "%5$s"
                                        + "</html>", current.test.name, 
                                        quiz48.QuizTimer.durationFormat(current.time() * 1000, true),
                                        current.test.time > 0 ? 
                                                String.format(" из %1$s", quiz48.QuizTimer.durationFormat(current.test.time * 1000, true)) : "", 
                                        new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(current.date), 
                                        current.duplicate ? "<div style=\"font-size: 20pt; color: white;background-color: red;padding:3px;\">Повторно</div>" : "")
                        ), BorderLayout.CENTER);
                add(new JLabel(AppIcons.instance().get("test_result64.png")), BorderLayout.EAST);
            } }, BorderLayout.CENTER);
        } }, BorderLayout.NORTH);
        main.add(new JPanel() { {
            setLayout(new BorderLayout());
            add(new JScrollPane(
                    new JTable() { {
                        /**
                        * колонки
                        * 1 - номер вопроса
                        * 2 - результат
                        * 3 - вес
                        */
                        setDefaultRenderer(QueryResult.fail.class, new ResultCellRenderer());
                        setDefaultRenderer(PercentCellValue.class, new PercentCellValue.PercentCellValueRenderer());
                        setRowMargin(2);
                        setDragEnabled(false);
                        getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                       
                        setModel(new AbstractTableModel() {
                            @Override
                            public int getRowCount() { return qresults.size(); }

                            @Override
                            public int getColumnCount() {
                                return 3;
                            }

                            @Override
                            public Object getValueAt(int rowIndex, int columnIndex) {
                                switch(columnIndex) {
                                    case 0:
                                        return rowIndex + 1;
                                    case 1:
                                        return qresults.get(rowIndex).fail();
                                    case 2:
                                        return new PercentCellValue((double)qresults.get(rowIndex).query.weight * 100 / sumWeight.get());
                                }

                                return 0;
                            }

                            @Override
                            public String getColumnName(int column) {
                                switch(column) {
                                    case 0:
                                        return "Номер вопроса";
                                    case 1:
                                        return "Результат";
                                    case 2:
                                        return "Вес, %";
                                }

                                return super.getColumnName(column);
                            }

                            @Override
                            public Class<?> getColumnClass(int columnIndex) {
                                switch(columnIndex) {
                                    case 1:
                                        return QueryResult.fail.class;
                                    case 2:
                                        return PercentCellValue.class;
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
                                        QueryResultDetailViewDlg dlg = 
                                                new QueryResultDetailViewDlg(
                                                        wnd, 
                                                        qresults.get(sel));
                                        dlg.setVisible(true);
                                    }
                                }
                                super.mouseClicked(e);
                            }
                        });
                    } }
            ), BorderLayout.CENTER);
        } }, BorderLayout.CENTER);
        main.add(new JPanel() { {
            setLayout(new BorderLayout());
            add(new JPanel(), BorderLayout.CENTER);
            add(new JPanel() { {
                setLayout(new BorderLayout());
                setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                add(new JLabel(String.format(
                                "<html>"
                                        + "<div style=\"font-size: 30pt; color: black;\">"
                                        + "<strong>"
                                        + "Итог: <span style=\"color: green;\">%1$02.2f%%</span>"
                                        + "</strong>"
                                        + "</div>"
                                        + "</html>", 
                                (double)sumResult.get() * 100 / (double)sumWeight.get())), BorderLayout.CENTER);
            } }, BorderLayout.EAST);
        } }, BorderLayout.SOUTH);

        bottom.addButton(new JButton() { {
            setText("<К списку результатов тестов");
            setHorizontalTextPosition(JButton.LEFT);
            setIcon(AppIcons.instance().get("result_view32.png"));
            addActionListener((e) -> { initStatWindow.run(); });
        } });
        
        bottom.addButton(new JButton() { {
            setText("<<К списку задач");
            setHorizontalTextPosition(JButton.LEFT);
            setIcon(AppIcons.instance().get("qlist32.png"));
            addActionListener((e) -> { initStartWindow.run(); });
        } });
        
        wnd.revalidate();
        wnd.repaint();
    }
}
