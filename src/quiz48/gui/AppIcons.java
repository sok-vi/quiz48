/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.gui;

import java.util.HashMap;
import java.util.Random;
import javax.swing.ImageIcon;

/**
 *
 * @author vasya
 */
public final class AppIcons {
    private final static int MAX_QUESTION = 9;
    private final HashMap<String, ImageIcon> m_IconMap = new HashMap<>();
    private final Class<?> m_ThisClass = AppIcons.class;
    
    public final ImageIcon get(String resouceKey) {
        if(!m_IconMap.containsKey(resouceKey)) {
            m_IconMap.put(
                    resouceKey, 
                    new ImageIcon(
                            m_ThisClass.getResource(
                                    String.format("resource/%1$s", resouceKey))));
        }
        
        return m_IconMap.get(resouceKey);
    }
    
    public final ImageIcon getRandomQuestion() {
        Random rnd = new Random(System.currentTimeMillis());
        return get(String.format("q%1$d.png", rnd.nextInt(MAX_QUESTION)));
    }

    private static AppIcons gInstance;
    
    public static AppIcons instance() {
        if(gInstance == null) {  gInstance = new AppIcons(); }
        return gInstance;
    }
}
