/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.md5;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author vasya
 */
public class md5 {
    public static String calculate(String pwd) {
        String md5 = "";
        
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(pwd.getBytes());
            byte[] d = md.digest();
            StringBuilder sb = new StringBuilder();
            for(byte bd : d) {
                sb.append(String.format("%02X", bd));
            }
            
            md5 = sb.toString();
        }
        catch(NoSuchAlgorithmException e0) { }
        
        return md5;
    }
    
    public static boolean checkPWD(String pwd, String hash) {
        String pwdh = md5.calculate(pwd);
        return (pwdh.compareTo(hash) == 0);
    }
}
