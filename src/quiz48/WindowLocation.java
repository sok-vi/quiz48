/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;

/**
 *
 * @author vasya
 */
public final class WindowLocation {
    private WindowLocation() { }

    public static void WindowSetCenterScreenLocation(Window wnd) {
        Toolkit t = Toolkit.getDefaultToolkit();
        Dimension ss = t.getScreenSize();
        wnd.setLocation((int)((ss.getWidth() - wnd.getWidth()) / 2), 
                (int)((ss.getHeight() - wnd.getHeight()) / 2));
    }
    
    public static void WindowSetCenterScreenLocation23(Window wnd) {
        Toolkit t = Toolkit.getDefaultToolkit();
        Dimension ss = t.getScreenSize();
        wnd.setSize((int)(ss.getWidth() * 2 / 3), (int)(ss.getHeight() * 2 / 3));
        wnd.setLocation((int)((ss.getWidth() - wnd.getWidth()) / 2), 
                (int)((ss.getHeight() - wnd.getHeight()) / 2));
    }
    
    public static void DialogSetCenterParentWindowLocation(Window parent, Window dlg) {
        Dimension s = dlg.getSize();
        Dimension ps = parent.getSize();
        Point pl = parent.getLocation();
        dlg.setLocation(
                (int)(pl.getX() + (ps.getWidth() - s.getWidth()) / 2), 
                (int)(pl.getY() + (ps.getHeight() - s.getHeight()) / 2));
    }
}
