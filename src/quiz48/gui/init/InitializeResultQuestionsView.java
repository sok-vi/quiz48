/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.gui.init;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import quiz48.db.ConnectDB;
import quiz48.db.orm.QueryResult;
import quiz48.db.orm.TestResult;
import quiz48.gui.AppIcons;
import quiz48.gui.BottomPanel;

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
    
    private final static class WeightValue {
        private final double proc;
        public WeightValue(double proc) { this.proc = proc; }
        public final double getProcValue() { return proc; }
    }
    
    private final static class WeightValueCellRenderer extends JPanel implements TableCellRenderer {
        private final JLabel text = new JLabel();
        private final JProgressBar prog = new JProgressBar();
        
        {
            setLayout(new BorderLayout());
            add(text, BorderLayout.WEST);
            add(prog, BorderLayout.CENTER);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            text.setText(String.format("%1$f", ((WeightValue)value).getProcValue()));
            return this;
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
        
        main.removeAll();
        main.setLayout(new BorderLayout());
        
        bottom.clearButtons();
        
        main.add(new JPanel() { {} }, BorderLayout.NORTH);
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
                       setDefaultRenderer(WeightValue.class, new WeightValueCellRenderer());
                       
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
                                       return new WeightValue(qresults.get(rowIndex).query.weight);
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
                                       return WeightValue.class;
                               }
                               return super.getColumnClass(columnIndex); 
                           }
                           
                           
                       });
                    } }
            ), BorderLayout.CENTER);
        } }, BorderLayout.CENTER);

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
