/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author vasya
 */
public class DuplicateCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setBackground((Boolean)value ? Color.ORANGE : Color.YELLOW);
        return super.getTableCellRendererComponent(table, (Boolean)value ? "да" : "нет", isSelected, hasFocus, row, column);
    }
    
}
