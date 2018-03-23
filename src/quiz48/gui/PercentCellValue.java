/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author vasya
 */
public class PercentCellValue {
    private final double proc;
    public PercentCellValue(double proc) { this.proc = proc; }
    public final double getPercentValue() { return proc; }

    public final static class PercentCellValueRenderer extends JPanel implements TableCellRenderer {
        private final JLabel text = new JLabel();
        private final JProgressBar prog = new JProgressBar(0, 100);
        private final JPanel textPanel = new JPanel();
        
        {
            setLayout(new BorderLayout());
            textPanel.setLayout(new BorderLayout());
            textPanel.add(text, BorderLayout.CENTER);
            textPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
            add(textPanel, BorderLayout.WEST);
            add(prog, BorderLayout.CENTER);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            text.setText(String.format("%1$02.2f%%", ((PercentCellValue)value).getPercentValue()));
            prog.setValue((int)Math.round(((PercentCellValue)value).getPercentValue()));
            
            if(hasFocus) { 
                setBorder(UIManager.getBorder("Table.focusCellHighlightBorder")); 
            }
            else { 
                setBorder(null); 
            }
            
            if(isSelected) {
                setBackground(table.getSelectionBackground());
                textPanel.setBackground(table.getSelectionBackground());
                textPanel.setForeground(table.getSelectionForeground());
            }
            else {
                setBackground(UIManager.getColor("Table.dropCellBackground"));
                textPanel.setBackground(UIManager.getColor("Table.dropCellBackground"));
                textPanel.setForeground(UIManager.getColor("Table.dropCellForeground"));
            }
            
            return this;
        }
        
    }
}
