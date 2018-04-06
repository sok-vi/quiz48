/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author vasya
 */
public class AppProperties {
    public final static String LookAndFeel;
    public final static String DBPath;
    public final static String DBLogin;
    public final static String DBPassword;
    public final static String ContentPath;
    
    static {
        String _LookAndFeel = null;
        String _DBPath = null;
        String _DBLogin = null;
        String _DBPassword = null;
        String _ContentPath = null;
        
        try {
            FileInputStream fis = new FileInputStream(PackageLocation.thisPackagePath + "quiz48.properties");
            Properties ps = new Properties();
            ps.load(fis);
            _LookAndFeel = ps.getProperty("LookAndFeel", null);
            _DBPath = ps.getProperty("db.path", PackageLocation.thisPackagePath + "/");
            _DBLogin = ps.getProperty("db.login", "vasya");
            _DBPassword = ps.getProperty("db.password", "65h90jgwc3j890hyg54jhpo453ujhip");
            _ContentPath = ps.getProperty("content.path", String.format("%1$s%2$s%3$s", PackageLocation.thisPackagePath, "content", "/"));
        }
        catch(IOException e) { }
        
        LookAndFeel = _LookAndFeel;
        DBPath = _DBPath;
        DBLogin = _DBLogin;
        DBPassword = _DBPassword;
        ContentPath = _ContentPath;
    }
}
