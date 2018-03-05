/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48;

import java.io.File;
/**
 *
 * @author vasya
 */
public final class PackageLocation {
    public static final String thisPackagePath = PackageLocation.packageDirectory(PackageLocation.class);
    
    public static String packageDirectory(Class<?> c) {
        String _path = null;
        
        try {
            File _pp = new File(
                    c.getProtectionDomain().getCodeSource().getLocation().toURI());
            
            _path = _pp.getPath();
            
            if(_pp.isFile()) {
                _path = _path.substring(0, _path.length() - _pp.getName().length() - 1);
            }
        }
        catch(java.net.URISyntaxException e){
            
        }
        
        return _path + File.separator;
    }
    
    private PackageLocation() {}
}