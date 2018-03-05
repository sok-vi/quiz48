/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author vasya
 */
public class BottomPanel {
    private final JPanel m_ButtonsPanel;
    
    public interface ClearPanelListener {
        void clearPanelEvent();
    }
    
    private LinkedList<ClearPanelListener> m_ClearPanelListeners = new LinkedList<>();
    
    public final void addClearPanelListener(ClearPanelListener cpl) {
        m_ClearPanelListeners.add(cpl);
    }
    
    private final void doClearPanelEvent() {
        for(ClearPanelListener cpl : m_ClearPanelListeners) {
            cpl.clearPanelEvent();
        }
        m_ClearPanelListeners.clear();
    }
    
    public BottomPanel(JPanel bottomButtons, Window wnd) {
        m_ButtonsPanel = new JPanel() { {
            setLayout(new FlowLayout(FlowLayout.RIGHT));
            add(new JButton() { {
                setText("Завершить работу");
                setIcon(AppIcons.instance().get("exit32.png"));
                addActionListener((e)  -> {
                    wnd.dispatchEvent(new WindowEvent(wnd, WindowEvent.WINDOW_CLOSING));
                });
            } });
        } };
        
        bottomButtons.setLayout(new BorderLayout());
        bottomButtons.add(m_ButtonsPanel, BorderLayout.EAST);
        bottomButtons.add(new JPanel(), BorderLayout.CENTER);
        bottomButtons.add(new JPanel() { {
            setLayout(new FlowLayout(FlowLayout.CENTER));
            add(new JButton() { {
                setIcon(AppIcons.instance().get("about32.png"));
                setToolTipText("О программе...");
                addActionListener((e) -> {
                    AboutDlg dlg = new AboutDlg(wnd);
                    dlg.setVisible(true);
                });
            } });
        } }, BorderLayout.WEST);
    }
    
    public final void clearButtons() {
        while(m_ButtonsPanel.getComponentCount() > 1) {
            m_ButtonsPanel.remove(0);
        }
    }
    
    public final void addButton(JButton b) {
        m_ButtonsPanel.add(b, 0);
    }
}
