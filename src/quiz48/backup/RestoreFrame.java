/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.backup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import quiz48.AppProperties;
import quiz48.PackageLocation;
import quiz48.Pointer;
import quiz48.TaskQueue;
import quiz48.WindowLocation;
import quiz48.gui.AppIcons;
import quiz48.gui.LoadingWindow;

/**
 *
 * @author vasya
 */
public class RestoreFrame extends JFrame {
    public RestoreFrame() {
        super();
        setTitle("Восстановление из резервной копии");
        setResizable(false);

        setLayout(new BorderLayout());
        GridBagConstraints _cc = new GridBagConstraints();
        Insets _is1 = new Insets(0, 0, 5, 5),
                is2 = _cc.insets;
        
        Pointer<JFrame> thisFrame = new Pointer<>(this);
        Pointer<JRadioButton> db_rb_default = new Pointer<>(),
                db_rb_exist = new Pointer<>(),
                db_rb_new = new Pointer<>(),
                lo_rb_users_del = new Pointer<>(),
                lo_rb_users_nodel = new Pointer<>(),
                lo_rb_users_nodel_skip = new Pointer<>(),
                cn_rb_not = new Pointer<>(),
                cn_rb_default = new Pointer<>(),
                cn_rb_path = new Pointer<>();
        Pointer<JLabel> db_lb_path = new Pointer<>(),
                db_lb_user = new Pointer<>(),
                db_lb_pwd = new Pointer<>(),
                db_lb_pwd_conf = new Pointer<>(),
                cn_lb_path = new Pointer<>(),
                db_lb_dbname = new Pointer<>();
        Pointer<JTextField> db_tf_path = new Pointer<>(),
                db_tf_user = new Pointer<>(),
                cn_tf_path = new Pointer<>(),
                bk_tf_path = new Pointer<>(),
                db_tf_dbname = new Pointer<>();
        Pointer<JPasswordField> db_pf_pwd = new Pointer<>(),
                db_pf_pwd_conf =  new Pointer<>();
        Pointer<JButton> db_bt_path = new Pointer<>(),
                cn_bt_path = new Pointer<>();
        Pointer<JCheckBox> lo_ch_tests = new Pointer<>(),
                lo_ch_tests_del = new Pointer<>(),
                lo_ch_user = new Pointer<>(),
                lo_ch_results = new Pointer<>(),
                lo_ch_results_skip = new Pointer<>(),
                lo_ch_results_del = new Pointer<>(),
                us_ch_set_setting = new Pointer<>();
        Pointer<Color> tf_background_color = new Pointer<>();
        
        Runnable db_upd_fields = () -> {
            if(db_rb_default.get().isSelected()) {
                db_lb_path.get().setEnabled(false);
                db_tf_path.get().setEnabled(false);
                db_bt_path.get().setEnabled(false);
                db_lb_user.get().setEnabled(false);
                db_tf_user.get().setEnabled(false);
                db_lb_pwd.get().setEnabled(false);
                db_pf_pwd.get().setEnabled(false);
                db_lb_pwd_conf.get().setEnabled(false);
                db_pf_pwd_conf.get().setEnabled(false);
                db_lb_dbname.get().setEnabled(false);
                db_tf_dbname.get().setEnabled(false);
            }
            else if(db_rb_exist.get().isSelected()) {
                db_lb_path.get().setEnabled(true);
                db_tf_path.get().setEnabled(true);
                db_bt_path.get().setEnabled(true);
                db_lb_user.get().setEnabled(true);
                db_tf_user.get().setEnabled(true);
                db_lb_pwd.get().setEnabled(true);
                db_pf_pwd.get().setEnabled(true);
                db_lb_pwd_conf.get().setEnabled(false);
                db_pf_pwd_conf.get().setEnabled(false);
                db_lb_dbname.get().setEnabled(false);
                db_tf_dbname.get().setEnabled(false);
            }
            else if(db_rb_new.get().isSelected()) {
                db_lb_path.get().setEnabled(true);
                db_tf_path.get().setEnabled(true);
                db_bt_path.get().setEnabled(true);
                db_lb_user.get().setEnabled(true);
                db_tf_user.get().setEnabled(true);
                db_lb_pwd.get().setEnabled(true);
                db_pf_pwd.get().setEnabled(true);
                db_lb_pwd_conf.get().setEnabled(true);
                db_pf_pwd_conf.get().setEnabled(true);
                db_lb_dbname.get().setEnabled(true);
                db_tf_dbname.get().setEnabled(true);
            }
            
            lo_ch_tests_del.get().setEnabled(
                    lo_ch_tests.get().isSelected() && 
                            (db_rb_default.get().isSelected() || 
                            db_rb_exist.get().isSelected()));
            
            boolean _user_lo = lo_ch_user.get().isSelected() && 
                    (db_rb_default.get().isSelected() || 
                    db_rb_exist.get().isSelected());
            lo_rb_users_del.get().setEnabled(_user_lo);
            lo_rb_users_nodel.get().setEnabled(_user_lo);
            lo_rb_users_nodel_skip.get().setEnabled(_user_lo);
            
            boolean _results_lo = lo_ch_results.get().isSelected() && 
                    (db_rb_default.get().isSelected() || 
                    db_rb_exist.get().isSelected());
            lo_ch_results_skip.get().setEnabled(_results_lo);
            lo_ch_results_del.get().setEnabled(_results_lo);
        };
        
        Runnable cn_upd_fields = () -> {
            boolean def_use = cn_rb_path.get().isSelected();
            cn_bt_path.get().setEnabled(def_use);
            cn_lb_path.get().setEnabled(def_use);
            cn_tf_path.get().setEnabled(def_use);
        };
        
        add(new JPanel() { {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            add(new JTabbedPane() { {
                addTab("База данных", new JPanel() { {
                    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                    add(new JPanel() { {
                        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                        add(Box.createVerticalStrut(5));
                        ButtonGroup g1 = new ButtonGroup();
                        setAlignmentX(Component.RIGHT_ALIGNMENT);
                        add(new JRadioButton() { {
                            setText("- по умолчанию");
                            setSelected(true);
                            g1.add(this);
                            db_rb_default.put(this);
                            setAlignmentX(Component.LEFT_ALIGNMENT);
                            addChangeListener((e) -> { db_upd_fields.run(); });
                        } });
                        add(Box.createVerticalStrut(5));
                        add(new JRadioButton() { {
                            setText("- использовать существующую");
                            g1.add(this);
                            db_rb_exist.put(this);
                            setAlignmentX(Component.LEFT_ALIGNMENT);
                            addChangeListener((e) -> { db_upd_fields.run(); });
                        } });
                        add(Box.createVerticalStrut(5));
                        add(new JRadioButton() { {
                            setText("- создать новую");
                            g1.add(this);
                            db_rb_new.put(this);
                            setAlignmentX(Component.LEFT_ALIGNMENT);
                            addChangeListener((e) -> { db_upd_fields.run(); });
                        } });
                        add(Box.createVerticalStrut(10));
                    } });
                    add(new JPanel() { {
                        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Подключение"));
                        setLayout(new BorderLayout());
                        add(new JPanel() { {
                            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                            setLayout(new GridBagLayout());
                            GridBagConstraints _cc0 = new GridBagConstraints();

                            _cc0.gridx = 0;
                            _cc0.gridy = 0;
                            _cc0.weightx = 100;
                            _cc0.weighty = 0;
                            _cc0.gridwidth = 1;
                            _cc0.gridheight = 1;
                            _cc0.insets = _is1;
                            _cc0.fill = GridBagConstraints.NONE;
                            _cc0.anchor = GridBagConstraints.EAST;
                            add(new JLabel() { {
                                setText("Путь к базе данных:");
                                db_lb_path.put(this);
                            } }, _cc0);

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
                                tf_background_color.put(getBackground());
                                setColumns(25);
                                setText(AppProperties.DBPath);
                                db_tf_path.put(this);
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
                                db_bt_path.put(this);
                                addActionListener((e) -> {
                                    File f = new File(db_tf_path.get().getText());
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
                                        db_tf_path.get().setText(dir);
                                    }
                                });
                            } }, _cc0);

                            _cc0.gridx = 0;
                            _cc0.gridy = 1;
                            _cc0.weightx = 100;
                            _cc0.weighty = 0;
                            _cc0.gridwidth = 1;
                            _cc0.gridheight = 1;
                            _cc0.insets = _is1;
                            _cc0.fill = GridBagConstraints.NONE;
                            _cc0.anchor = GridBagConstraints.EAST;
                            add(new JLabel() { {
                                setText("Пользователь:");
                                db_lb_user.put(this);
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
                                setText(AppProperties.DBLogin);
                                db_tf_user.put(this);
                            } }, _cc0);

                            _cc0.gridx = 0;
                            _cc0.gridy = 2;
                            _cc0.weightx = 100;
                            _cc0.weighty = 0;
                            _cc0.gridwidth = 1;
                            _cc0.gridheight = 1;
                            _cc0.insets = _is1;
                            _cc0.fill = GridBagConstraints.NONE;
                            _cc0.anchor = GridBagConstraints.EAST;
                            add(new JLabel() { {
                                setText("Пароль:");
                                db_lb_pwd.put(this);
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
                            add(new JPasswordField() { {
                                setColumns(25);
                                setText(AppProperties.DBPassword);
                                db_pf_pwd.put(this);
                            } }, _cc0);

                            _cc0.gridx = 0;
                            _cc0.gridy = 3;
                            _cc0.weightx = 100;
                            _cc0.weighty = 0;
                            _cc0.gridwidth = 1;
                            _cc0.gridheight = 1;
                            _cc0.insets = _is1;
                            _cc0.fill = GridBagConstraints.NONE;
                            _cc0.anchor = GridBagConstraints.EAST;
                            add(new JLabel() { {
                                setText("Повторить пароль:");
                                db_lb_pwd_conf.put(this);
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
                                setText(AppProperties.DBPassword);
                                db_pf_pwd_conf.put(this);
                            } }, _cc0);

                            _cc0.gridx = 0;
                            _cc0.gridy = 4;
                            _cc0.weightx = 100;
                            _cc0.weighty = 0;
                            _cc0.gridwidth = 1;
                            _cc0.gridheight = 1;
                            _cc0.insets = _is1;
                            _cc0.fill = GridBagConstraints.NONE;
                            _cc0.anchor = GridBagConstraints.EAST;
                            add(new JLabel() { {
                                setText("Имя новой базы дынных:");
                                db_lb_dbname.put(this);
                            } }, _cc0);

                            _cc0.gridx = 1;
                            _cc0.gridy = 4;
                            _cc0.weightx = 0;
                            _cc0.weighty = 0;
                            _cc0.gridwidth = 1;
                            _cc0.gridheight = 1;
                            _cc0.insets = _is1;
                            _cc0.fill = GridBagConstraints.NONE;
                            _cc0.anchor = GridBagConstraints.WEST;
                            add(new JTextField() { {
                                setColumns(25);
                                setText("newdb" + (new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")).format(new Date()));
                                db_tf_dbname.put(this);
                            } }, _cc0);
                        } }, BorderLayout.CENTER);
                    } });
                } });

                addTab("Загрузка данных в базу данных", new JPanel() { {
                    setLayout(new BorderLayout());
                    add(new JPanel() { {
                        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                        setAlignmentX(Component.RIGHT_ALIGNMENT);
                        add(Box.createVerticalStrut(5));
                        add(new JCheckBox() { {
                            setText("- тесты и вопросы");
                            setSelected(true);
                            lo_ch_tests.put(this);
                            addChangeListener((e) -> { db_upd_fields.run(); });
                        } });
                        add(Box.createVerticalStrut(5));
                        add(new JCheckBox() { {
                            setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
                            setText("- удалить существующие записи");
                            lo_ch_tests_del.put(this);
                            addChangeListener((e) -> { db_upd_fields.run(); });
                        } });

                        add(Box.createVerticalStrut(5));
                        add(new JCheckBox() { {
                            setText("- пользователи, если есть");
                            setSelected(true);
                            lo_ch_user.put(this);
                            addChangeListener((e) -> { db_upd_fields.run(); });
                        } });
                        ButtonGroup g1 = new ButtonGroup();
                        add(new JRadioButton() { {
                            g1.add(this);
                            setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
                            setText("- удалить существующие записи");
                            addChangeListener((e) -> { db_upd_fields.run(); });
                            lo_rb_users_del.put(this);
                        } });
                        add(new JRadioButton() { {
                            g1.add(this);
                            setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
                            setText("- не удалять");
                            lo_rb_users_nodel.put(this);
                            addChangeListener((e) -> { db_upd_fields.run(); });
                        } });
                        add(new JRadioButton() { {
                            g1.add(this);
                            setSelected(true);
                            setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
                            setText("- не удалять и если существуют пропускать");
                            lo_rb_users_nodel_skip.put(this);
                            addChangeListener((e) -> { db_upd_fields.run(); });
                        } });

                        add(Box.createVerticalStrut(5));
                        add(new JCheckBox() { {
                            setText("- результаты, если есть");
                            setSelected(true);
                            lo_ch_results.put(this);
                            addChangeListener((e) -> { db_upd_fields.run(); });
                        } });
                        add(Box.createVerticalStrut(5));
                        add(new JCheckBox() { {
                            setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
                            setSelected(true);
                            setText("- пропускать записи, которые не удаётся привязать к пользователям");
                            lo_ch_results_skip.put(this);
                            addChangeListener((e) -> { db_upd_fields.run(); });
                        } });
                        add(Box.createVerticalStrut(5));
                        add(new JCheckBox() { {
                            setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
                            setText("- удалить существующие");
                            lo_ch_results_del.put(this);
                            addChangeListener((e) -> { db_upd_fields.run(); });
                        } });

                        add(Box.createVerticalStrut(5));
                    } }, BorderLayout.CENTER);
                } });

                addTab("Контент", new JPanel() { {
                    setLayout(new BorderLayout());
                    add(
                        new JPanel() { {
                        setLayout(new BorderLayout());
                        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                        add(new JPanel() { {
                            setLayout(new GridBagLayout());

                            GridBagConstraints _cc0 = new GridBagConstraints();

                            ButtonGroup g1 = new ButtonGroup();
                            _cc0.gridx = 0;
                            _cc0.gridy = 0;
                            _cc0.weightx = 3;
                            _cc0.weighty = 0;
                            _cc0.gridwidth = 3;
                            _cc0.gridheight = 1;
                            _cc0.insets = _is1;
                            _cc0.fill = GridBagConstraints.NONE;
                            _cc0.anchor = GridBagConstraints.WEST;
                            add(new JRadioButton() { {
                                setText("- развернуть контент из бэкапа в папку по умолчанию, если есть");
                                setSelected(true);
                                addChangeListener((e) -> { cn_upd_fields.run(); });
                                cn_rb_default.put(this);
                                g1.add(this);
                            } }, _cc0);

                            _cc0.gridx = 0;
                            _cc0.gridy = 1;
                            _cc0.weightx = 3;
                            _cc0.weighty = 0;
                            _cc0.gridwidth = 3;
                            _cc0.gridheight = 1;
                            _cc0.insets = _is1;
                            _cc0.fill = GridBagConstraints.NONE;
                            _cc0.anchor = GridBagConstraints.WEST;
                            add(new JRadioButton() { {
                                setText("- не разворачивать");
                                addChangeListener((e) -> { cn_upd_fields.run(); });
                                cn_rb_not.put(this);
                                g1.add(this);
                            } }, _cc0);

                            _cc0.gridx = 0;
                            _cc0.gridy = 2;
                            _cc0.weightx = 3;
                            _cc0.weighty = 0;
                            _cc0.gridwidth = 3;
                            _cc0.gridheight = 1;
                            _cc0.insets = _is1;
                            _cc0.fill = GridBagConstraints.NONE;
                            _cc0.anchor = GridBagConstraints.WEST;
                            add(new JRadioButton() { {
                                setText("- развернуть контент из бэкапав указанную папку, если есть");
                                addChangeListener((e) -> { cn_upd_fields.run(); });
                                cn_rb_path.put(this);
                                g1.add(this);
                            } }, _cc0);

                            _cc0.gridx = 0;
                            _cc0.gridy = 3;
                            _cc0.weightx = 1;
                            _cc0.weighty = 0;
                            _cc0.gridwidth = 1;
                            _cc0.gridheight = 1;
                            _cc0.insets = _is1;
                            _cc0.fill = GridBagConstraints.NONE;
                            _cc0.anchor = GridBagConstraints.EAST;
                            add(new JLabel() { {
                                setText("Путь к разворачиваемому контенту:");
                                cn_lb_path.put(this);
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
                            add(new JTextField() { {
                                setColumns(25);
                                setText(PackageLocation.thisPackagePath + "content" + File.separator);
                                cn_tf_path.put(this);
                            } }, _cc0);

                            _cc0.gridx = 2;
                            _cc0.gridy = 3;
                            _cc0.weightx = 0;
                            _cc0.weighty = 0;
                            _cc0.gridwidth = 1;
                            _cc0.gridheight = 1;
                            _cc0.insets = _is1;
                            _cc0.fill = GridBagConstraints.NONE;
                            _cc0.anchor = GridBagConstraints.CENTER;
                            add(new JButton() { {
                                setText("...");
                                cn_bt_path.put(this);
                                addActionListener((e) -> {
                                    File f = new File(cn_tf_path.get().getText());
                                    JFileChooser fod = new JFileChooser();
                                    fod.setDialogType(JFileChooser.OPEN_DIALOG);
                                    fod.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                                    if(f.exists()) {
                                        fod.setSelectedFile(f);
                                    }
                                    else {
                                        fod.setCurrentDirectory(new File(f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(File.separator))));
                                    }
                                    fod.setDialogTitle("Указать расположение контента");
                                    int ret = fod.showDialog(thisFrame.get(), "Открыть..");
                                    if(ret == JFileChooser.APPROVE_OPTION) {
                                        String dir = fod.getSelectedFile().getAbsolutePath();
                                        if((dir.length() > 0) && 
                                                (dir.charAt(dir.length() - 1) != '\\')) { 
                                            dir += "\\"; 
                                        }
                                        cn_tf_path.get().setText(dir);
                                    }
                                });
                            } }, _cc0);
                            
                            _cc0.gridx = 0;
                            _cc0.gridy = 4;
                            _cc0.weightx = 3;
                            _cc0.weighty = 100;
                            _cc0.gridwidth = 3;
                            _cc0.gridheight = 1;
                            _cc0.insets = _is1;
                            _cc0.fill = GridBagConstraints.NONE;
                            _cc0.anchor = GridBagConstraints.WEST;
                            add(new JPanel(), _cc0);
                        } }, BorderLayout.CENTER);
                    } }, BorderLayout.CENTER);
                } });
            } });
            
            add(Box.createVerticalStrut(5));
            add(new JSeparator(JSeparator.HORIZONTAL));
            add(Box.createVerticalStrut(5));
            
            add(new JPanel() { {
                setLayout(new FlowLayout(FlowLayout.RIGHT));
                add(new JLabel("Путь к бэкапу:"));
                add(new JTextField() { {
                    setColumns(25);
                    bk_tf_path.put(this);
                } });
                add(new JButton() { {
                    setText("...");
                    addActionListener((e) -> {
                        File f = new File(bk_tf_path.get().getText());
                        JFileChooser fod = new JFileChooser();
                        fod.setFileFilter(new BackupFileFilter());
                        fod.setDialogType(JFileChooser.OPEN_DIALOG);
                        fod.setFileSelectionMode(JFileChooser.FILES_ONLY);
                        fod.setSelectedFile(f);
                        int ret = fod.showDialog(thisFrame.get(), "Открыть файл");
                        if(ret == JFileChooser.APPROVE_OPTION) {
                            File rf = fod.getSelectedFile();
                            if(rf.getAbsolutePath().endsWith(".quiz.backup")) {
                                bk_tf_path.get().setText(rf.getAbsolutePath());
                            }
                            else {
                                bk_tf_path.get().setText(rf.getAbsolutePath() + ".quiz.backup");
                            }
                        }
                    });
                } });
            } });
            
            add(new JPanel() { {
                setLayout(new FlowLayout(FlowLayout.RIGHT));
                add(new JCheckBox() { {
                    setText("- сохранить указанные параметры в кофигурации приложения");
                    setSelected(true);
                    us_ch_set_setting.put(this);
                } });
            } });
        } }, BorderLayout.CENTER);
        
        add(new JPanel() { {
            setLayout(new FlowLayout(FlowLayout.RIGHT));
            add(new JButton() { {
                setIcon(AppIcons.instance().get("runprog.png"));
                setText("Запустить восстановление из резервной копии...");
                addActionListener((e) -> {
                    String _dbpath = db_rb_new.get().isSelected() ? 
                                        String.format(
                                                "%1$s%2$s%3$s", 
                                                db_tf_path.get().getText(), 
                                                db_tf_dbname.get().getText(), 
                                                File.separator) : 
                                        db_tf_path.get().getText(),
                            _dblogin = db_tf_user.get().getText(),
                            _dbpwd = new String(db_pf_pwd.get().getPassword()),
                            _bk_path = bk_tf_path.get().getText();
                    Boolean _user_loading = lo_ch_user.get().isSelected();
                    Backup.OptUser uopt = lo_rb_users_del.get().isSelected() ? 
                                                Backup.OptUser.delete : 
                                                (lo_rb_users_nodel.get().isSelected() ? 
                                                            Backup.OptUser.no_delete : 
                                                            Backup.OptUser.no_delete_skip);
                    
                    Pointer<Backup.OptDB> opt = new Pointer<>(Backup.OptDB.defaultDB);
                    if(db_rb_exist.get().isSelected()) {
                        opt.put(Backup.OptDB.setPathDB);
                    }
                    else if(db_rb_new.get().isSelected()) {
                        if(_dbpwd.compareTo(new String(db_pf_pwd_conf.get().getPassword())) == 0) {
                            db_pf_pwd_conf.get().setBackground(tf_background_color.get());
                            opt.put(Backup.OptDB.newDB);
                        }
                        else {
                            db_pf_pwd_conf.get().setBackground(Color.red);
                            return;
                        }
                        
                        if(db_tf_dbname.get().getText().length() == 0) {
                            db_tf_dbname.get().setBackground(Color.red);
                            return;
                        }
                        else {
                            db_tf_dbname.get().setBackground(tf_background_color.get());
                        }
                    }
                    
                    TaskQueue.instance().addNewTask(() -> {
                        LoadingWindow.Callback cb = LoadingWindow.showLoadingWindow(thisFrame.get(), "Восстановление базы данных из бэкапа...");
                      /*  try {
                            Backup.restore(opt.get(), _dbpath, _dblogin, _dbpwd, _bk_path, _user_loading, uopt);
                        } catch (IOException|SQLException ex) {
                            LoadingWindow.sleep(3);
                            cb.setInformation(ex.toString(), Color.RED);
                            ex.printStackTrace();
                        }*/
                        cb.setInformation(String.format("Данные восстановлены из бэкапа"));
                        LoadingWindow.sleep(2);
                        cb.exit();
                    });
                });
            } });
            add(new JButton() { {
                setText("Exit");
                addActionListener((e) -> { System.exit(0); });
            } });
        } }, BorderLayout.SOUTH);
        
        db_upd_fields.run();
        cn_upd_fields.run();
        
        pack();
        
        WindowLocation.WindowSetCenterScreenLocation(this);
    }
}
