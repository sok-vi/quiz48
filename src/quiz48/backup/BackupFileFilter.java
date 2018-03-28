/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.backup;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author vasya
 */
public class BackupFileFilter extends FileFilter {
    @Override
    public boolean accept(File f) {
        return f.isFile() ? (f.getAbsolutePath().endsWith(".quiz.backup")) : true;
    }

    @Override
    public String getDescription() {
        return "Quiz48 backup (*.quiz.backup)";
    }
}
