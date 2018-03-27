/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.backup;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import quiz48.AppProperties;
import quiz48.PackageLocation;
import quiz48.Pointer;
import quiz48.TaskQueue;
import quiz48.WindowLocation;
import quiz48.db.ConnectDB;
import quiz48.gui.AppIcons;
import quiz48.gui.LoadingWindow;

/**
 *
 * @author vasya
 */
public class BackupFrame extends JFrame {
    public BackupFrame() {
        super();
        setTitle("Создание резервной копии");
        setResizable(false);
        
        Pointer<JCheckBox> ch_tests = new Pointer<>(),
                ch_users = new Pointer<>(),
                ch_results = new Pointer<>(),
                ch_default_db = new Pointer<>();
        Pointer<JButton> bt_start = new Pointer<>(),
                bt_sel_db_path = new Pointer<>();
        Pointer<JLabel> lb_db_path = new Pointer<>(),
                lb_db_login = new Pointer<>(),
                lb_db_pwd = new Pointer<>();
        Pointer<JTextField> tf_db_paht = new Pointer<>(),
                tf_db_login = new Pointer<>(),
                tf_backup_path = new Pointer<>();
        Pointer<JPasswordField> pf_db_pwd = new Pointer<>();
        Pointer<JFrame> thisFrame = new Pointer<>(this);
        
        Runnable db_fields_enable = () -> {
            boolean en = !ch_default_db.get().isSelected();
            lb_db_path.get().setEnabled(en);
            lb_db_login.get().setEnabled(en);
            lb_db_pwd.get().setEnabled(en);
            tf_db_login.get().setEnabled(en);
            pf_db_pwd.get().setEnabled(en);
            tf_db_paht.get().setEnabled(en);
            bt_sel_db_path.get().setEnabled(en);
        };

        setLayout(new BorderLayout());
        getContentPane().add(new JPanel() { {
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setLayout(new GridBagLayout());
            
            GridBagConstraints _cc = new GridBagConstraints();
            Insets _is1 = new Insets(0, 0, 5, 5),
                    is2 = _cc.insets;

            _cc.gridx = 0;
            _cc.gridy = 0;
            _cc.weightx = 100;
            _cc.weighty = 0;
            _cc.gridwidth = 3;
            _cc.gridheight = 1;
            _cc.insets = _is1;
            _cc.fill = GridBagConstraints.HORIZONTAL;
            _cc.anchor = GridBagConstraints.CENTER;
            add(new JPanel() { {
                setLayout(new BorderLayout());
                setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Данные"));
                add(new JPanel() { {
                    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                    add(new JCheckBox() { {
                        setText("- тесты и вопросы");
                        setAlignmentX(Component.LEFT_ALIGNMENT);
                        setSelected(true);
                        ch_tests.put(this);
                        addChangeListener((e) -> {
                            boolean en = ch_tests.get().isSelected();
                            ch_users.get().setEnabled(en);
                            ch_results.get().setEnabled(en);
                            bt_start.get().setEnabled(en);
                        });
                    } });
                    add(Box.createVerticalStrut(10));
                    add(new JCheckBox() { {
                        setText("- пользователи");
                        setAlignmentX(Component.LEFT_ALIGNMENT);
                        setSelected(true);
                        ch_users.put(this);
                        addChangeListener((e) -> {
                            ch_results.get().setEnabled(ch_users.get().isSelected());
                        });
                    } });
                    add(Box.createVerticalStrut(10));
                    add(new JCheckBox() { {
                        setText("- результаты");
                        setAlignmentX(Component.LEFT_ALIGNMENT);
                        setSelected(true);
                        ch_results.put(this);
                    } });
                } }, BorderLayout.CENTER);
            } }, _cc);
            
            
            _cc.gridx = 0;
            _cc.gridy = 1;
            _cc.weightx = 100;
            _cc.weighty = 0;
            _cc.gridwidth = 3;
            _cc.gridheight = 1;
            _cc.insets = _is1;
            _cc.fill = GridBagConstraints.HORIZONTAL;
            _cc.anchor = GridBagConstraints.CENTER;
            add(new JPanel() { {
                setLayout(new BorderLayout());
                setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "База данных"));
                add(new JPanel() { {
                    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    setLayout(new GridBagLayout());
                    
                    GridBagConstraints _cc0 = new GridBagConstraints();
                    
                    _cc0.gridx = 0;
                    _cc0.gridy = 0;
                    _cc0.weightx = 100;
                    _cc0.weighty = 0;
                    _cc0.gridwidth = 2;
                    _cc0.gridheight = 1;
                    _cc0.insets = _is1;
                    _cc0.fill = GridBagConstraints.NONE;
                    _cc0.anchor = GridBagConstraints.WEST;
                    add(new JCheckBox() { {
                        setText("- база данных по умолчанию");
                        setSelected(true);
                        ch_default_db.put(this);
                        addChangeListener((e) -> { db_fields_enable.run(); });
                    } }, _cc0);
                    
                    _cc0.gridx = 0;
                    _cc0.gridy = 1;
                    _cc0.weightx = 0;
                    _cc0.weighty = 0;
                    _cc0.gridwidth = 1;
                    _cc0.gridheight = 1;
                    _cc0.insets = _is1;
                    _cc0.fill = GridBagConstraints.NONE;
                    _cc0.anchor = GridBagConstraints.EAST;
                    add(new JLabel() { {
                        setText("Путь к базе данных:");
                        lb_db_path.put(this);
                    } }, _cc0);
                    
                    _cc0.gridx = 1;
                    _cc0.gridy = 1;
                    _cc0.weightx = 0;
                    _cc0.weighty = 0;
                    _cc0.gridwidth = 1;
                    _cc0.gridheight = 1;
                    _cc0.insets = _is1;
                    _cc0.fill = GridBagConstraints.NONE;
                    _cc0.anchor = GridBagConstraints.WEST;
                    add(new JTextField() { {
                        setColumns(25);
                        setText(AppProperties.DBPath);
                        tf_db_paht.put(this);
                    } }, _cc0);
                    
                    _cc0.gridx = 2;
                    _cc0.gridy = 1;
                    _cc0.weightx = 0;
                    _cc0.weighty = 0;
                    _cc0.gridwidth = 1;
                    _cc0.gridheight = 1;
                    _cc0.insets = _is1;
                    _cc0.fill = GridBagConstraints.NONE;
                    _cc0.anchor = GridBagConstraints.CENTER;
                    add(new JButton() { {
                        setText("...");
                        bt_sel_db_path.put(this);
                        addActionListener((e) -> {
                            File f = new File(tf_db_paht.get().getText());
                            JFileChooser fod = new JFileChooser();
                            fod.setDialogType(JFileChooser.OPEN_DIALOG);
                            fod.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                            if(f.exists()) {
                                fod.setSelectedFile(f);
                            }
                            else {
                                fod.setCurrentDirectory(new File(f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(File.separator))));
                            }
                            fod.setDialogTitle("Указать расположение БД");
                            int ret = fod.showDialog(thisFrame.get(), "Открыть..");
                            if(ret == JFileChooser.APPROVE_OPTION) {
                                String dir = fod.getSelectedFile().getAbsolutePath();
                                if((dir.length() > 0) && 
                                        (dir.charAt(dir.length() - 1) != '\\')) { 
                                    dir += "\\"; 
                                }
                                tf_db_paht.get().setText(dir);
                            }
                        });
                    } }, _cc0);
                    
                    _cc0.gridx = 0;
                    _cc0.gridy = 2;
                    _cc0.weightx = 0;
                    _cc0.weighty = 0;
                    _cc0.gridwidth = 1;
                    _cc0.gridheight = 1;
                    _cc0.insets = _is1;
                    _cc0.fill = GridBagConstraints.NONE;
                    _cc0.anchor = GridBagConstraints.EAST;
                    add(new JLabel() { {
                        setText("Пользователь:");
                        lb_db_login.put(this);
                    } }, _cc0);
                    
                    _cc0.gridx = 1;
                    _cc0.gridy = 2;
                    _cc0.weightx = 0;
                    _cc0.weighty = 0;
                    _cc0.gridwidth = 1;
                    _cc0.gridheight = 1;
                    _cc0.insets = _is1;
                    _cc0.fill = GridBagConstraints.NONE;
                    _cc0.anchor = GridBagConstraints.WEST;
                    add(new JTextField() { {
                        setColumns(25);
                        setText(AppProperties.DBLogin);
                        tf_db_login.put(this);
                    } }, _cc0);
                    
                    _cc0.gridx = 0;
                    _cc0.gridy = 3;
                    _cc0.weightx = 0;
                    _cc0.weighty = 0;
                    _cc0.gridwidth = 1;
                    _cc0.gridheight = 1;
                    _cc0.insets = _is1;
                    _cc0.fill = GridBagConstraints.NONE;
                    _cc0.anchor = GridBagConstraints.EAST;
                    add(new JLabel() { {
                        setText("Пароль:");
                        lb_db_pwd.put(this);
                    } }, _cc0);
                    
                    _cc0.gridx = 1;
                    _cc0.gridy = 3;
                    _cc0.weightx = 0;
                    _cc0.weighty = 0;
                    _cc0.gridwidth = 1;
                    _cc0.gridheight = 1;
                    _cc0.insets = _is1;
                    _cc0.fill = GridBagConstraints.NONE;
                    _cc0.anchor = GridBagConstraints.WEST;
                    add(new JPasswordField() { {
                        setColumns(25);
                        setEchoChar('*');
                        setText(AppProperties.DBPassword);
                        pf_db_pwd.put(this);
                    } }, _cc0);
                } }, BorderLayout.CENTER);
            } }, _cc);
            
            _cc.gridx = 0;
            _cc.gridy = 2;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = 3;
            _cc.gridheight = 1;
            _cc.insets = _is1;
            _cc.fill = GridBagConstraints.NONE;
            _cc.anchor = GridBagConstraints.EAST;
            add(new JPanel() { {
                setLayout(new BorderLayout());
                setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                add(new JPanel() { {
                    setLayout(new GridBagLayout());
                    
                    GridBagConstraints _cc0 = new GridBagConstraints();
                    
                    _cc0.gridx = 0;
                    _cc0.gridy = 0;
                    _cc0.weightx = 0;
                    _cc0.weighty = 0;
                    _cc0.gridwidth = 1;
                    _cc0.gridheight = 1;
                    _cc0.insets = _is1;
                    _cc0.fill = GridBagConstraints.NONE;
                    _cc0.anchor = GridBagConstraints.EAST;
                    add(new JLabel("Путь к базе данных:"), _cc0);
                    
                    _cc0.gridx = 1;
                    _cc0.gridy = 0;
                    _cc0.weightx = 0;
                    _cc0.weighty = 0;
                    _cc0.gridwidth = 1;
                    _cc0.gridheight = 1;
                    _cc0.insets = _is1;
                    _cc0.fill = GridBagConstraints.NONE;
                    _cc0.anchor = GridBagConstraints.WEST;
                    add(new JTextField() { {
                        setColumns(25);
                        setText(
                                PackageLocation.thisPackagePath + 
                                        String.format(
                                                "backup_%1$s.quiz.backup", 
                                                (new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")).format(new Date())
                                        )
                        );
                        tf_backup_path.put(this);
                    } }, _cc0);
                    
                    _cc0.gridx = 2;
                    _cc0.gridy = 0;
                    _cc0.weightx = 0;
                    _cc0.weighty = 0;
                    _cc0.gridwidth = 1;
                    _cc0.gridheight = 1;
                    _cc0.insets = _is1;
                    _cc0.fill = GridBagConstraints.NONE;
                    _cc0.anchor = GridBagConstraints.CENTER;
                    add(new JButton() { {
                        setText("...");
                        addActionListener((e) -> {
                            File f = new File(tf_backup_path.get().getText());
                            JFileChooser fod = new JFileChooser();
                            fod.setFileFilter(new FileNameExtensionFilter("Quiz48 backup", "quiz.backup"));
                            fod.setDialogType(JFileChooser.SAVE_DIALOG);
                            fod.setFileSelectionMode(JFileChooser.FILES_ONLY);
                            if(f.exists()) {
                                fod.setSelectedFile(f);
                            }
                            else {
                                fod.setCurrentDirectory(new File(f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(File.separator))));
                            }
                            int ret = fod.showDialog(thisFrame.get(), "Сохранить файл");
                            if(ret == JFileChooser.APPROVE_OPTION) {
                                File rf = fod.getSelectedFile();
                                if(rf.toPath().endsWith(".quiz.backup")) {
                                    tf_backup_path.get().setText(rf.getAbsolutePath());
                                }
                                else {
                                    tf_backup_path.get().setText(rf.getAbsolutePath() + ".quiz.backup");
                                }
                            }
                        });
                    } }, _cc0);
                } }, BorderLayout.CENTER);
            } }, _cc);
            
        } }, BorderLayout.CENTER);
        
        getContentPane().add(new JPanel() { {
            setLayout(new FlowLayout(FlowLayout.RIGHT));
            add(new JButton() { {
                setText("Запустить создание резервной копии...");
                setIcon(AppIcons.instance().get("runprog.png"));
                addActionListener((e) -> {
                    String _dbPath = tf_db_paht.get().getText(),
                            _dbLogin = tf_db_login.get().getText(),
                            _dbPass = new String(pf_db_pwd.get().getPassword()),
                            _backupPath = tf_backup_path.get().getText();
                    Boolean _sUsers = ch_users.get().isSelected(),
                            _sResults = ch_results.get().isSelected();
                    TaskQueue.instance().addNewTask(() -> {
                        LoadingWindow.Callback cb = LoadingWindow.showLoadingWindow(thisFrame.get(), "Подключение к БД...");
                        try {
                            try(ConnectDB conn = ConnectDB.connect(
                                    _dbPath, 
                                    _dbLogin, 
                                    _dbPass)) {
                                cb.setInformation("Подключение к БД... успешно");
                                Backup.storeBackup(conn, _sUsers, _sResults, _backupPath, cb);
                            }
                        } catch (Exception ex) {
                            cb.setInformation(ex.toString(), Color.RED);
                            LoadingWindow.sleep(5);
                        }
                        cb.setInformation(String.format("Бэкап создан {%1$s}", _backupPath));
                        LoadingWindow.sleep(2);
                        cb.exit();
                    });
                });
                bt_start.put(this);
            } });
            add(new JButton() { {
                setText("Exit");
                addActionListener((e) -> { System.exit(0); });
            } });
        } }, BorderLayout.SOUTH);
        
        db_fields_enable.run();
        
        pack();
        
        WindowLocation.WindowSetCenterScreenLocation(this);
    }
}
