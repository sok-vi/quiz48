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
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import quiz48.WindowLocation;
import quiz48.gui.AppIcons;

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
        
        Pointer<JRadioButton> db_rb_default = new Pointer<>(),
                db_rb_exist = new Pointer<>(),
                db_rb_new = new Pointer<>();
        Pointer<JLabel> db_lb_path = new Pointer<>(),
                db_lb_user = new Pointer<>(),
                db_lb_pwd = new Pointer<>(),
                db_lb_pwd_conf = new Pointer<>();
        Pointer<JTextField> db_tf_path = new Pointer<>(),
                db_tf_user = new Pointer<>();
        Pointer<JPasswordField> db_pf_pwd = new Pointer<>(),
                db_pf_pwd_conf =  new Pointer<>();
        Pointer<JButton> db_bt_path = new Pointer<>();
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
            }
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
                        } });
                        add(Box.createVerticalStrut(5));
                        add(new JCheckBox() { {
                            setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
                            setText("- удалить существующие записи");
                        } });

                        add(Box.createVerticalStrut(5));
                        add(new JCheckBox() { {
                            setText("- пользователи, если есть");
                            setSelected(true);
                        } });
                        ButtonGroup g1 = new ButtonGroup();
                        add(new JRadioButton() { {
                            g1.add(this);
                            setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
                            setText("- удалить существующие записи");
                        } });
                        add(new JRadioButton() { {
                            g1.add(this);
                            setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
                            setText("- не удалять");
                        } });
                        add(new JRadioButton() { {
                            g1.add(this);
                            setSelected(true);
                            setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
                            setText("- не удалять и если существуют пропускать");
                        } });

                        add(Box.createVerticalStrut(5));
                        add(new JCheckBox() { {
                            setText("- результаты, если есть");
                            setSelected(true);
                        } });
                        add(Box.createVerticalStrut(5));
                        add(new JCheckBox() { {
                            setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
                            setSelected(true);
                            setText("- пропускать записи, которые не удаётся привязать к пользователям");
                        } });
                        add(Box.createVerticalStrut(5));
                        add(new JCheckBox() { {
                            setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
                            setText("- удалить существующие");
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

                            _cc0.gridx = 0;
                            _cc0.gridy = 0;
                            _cc0.weightx = 3;
                            _cc0.weighty = 0;
                            _cc0.gridwidth = 3;
                            _cc0.gridheight = 1;
                            _cc0.insets = _is1;
                            _cc0.fill = GridBagConstraints.NONE;
                            _cc0.anchor = GridBagConstraints.WEST;
                            add(new JCheckBox() { {
                                setText("- развернуть контент из бэкапа, если есть");
                                setSelected(true);
                                addChangeListener((e) -> { 

                                });
                            } }, _cc0);

                            _cc0.gridx = 0;
                            _cc0.gridy = 1;
                            _cc0.weightx = 1;
                            _cc0.weighty = 0;
                            _cc0.gridwidth = 1;
                            _cc0.gridheight = 1;
                            _cc0.insets = _is1;
                            _cc0.fill = GridBagConstraints.NONE;
                            _cc0.anchor = GridBagConstraints.EAST;
                            add(new JLabel() { {
                                setText("Путь к разворачиваемому контенту:");
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
                                setText(PackageLocation.thisPackagePath + "content" + File.separator);
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
                            } }, _cc0);
                            
                            _cc0.gridx = 0;
                            _cc0.gridy = 2;
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
                } });
                add(new JButton() { {
                    setText("...");
                    addActionListener((e) -> { 
                    });
                } });
            } });
            
        } }, BorderLayout.CENTER);
        
        add(new JPanel() { {
            setLayout(new FlowLayout(FlowLayout.RIGHT));
            add(new JButton() { {
                setIcon(AppIcons.instance().get("runprog.png"));
                setText("Запустить восстановление из резервной копии...");
                addActionListener((e) -> { 
                });
            } });
            add(new JButton() { {
                setText("Exit");
                addActionListener((e) -> { System.exit(0); });
            } });
        } }, BorderLayout.SOUTH);
        
        db_upd_fields.run();
        
        pack();
        
        WindowLocation.WindowSetCenterScreenLocation(this);
    }
}
