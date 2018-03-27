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
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import quiz48.AppProperties;
import quiz48.Pointer;
import quiz48.WindowLocation;
import quiz48.gui.AppIcons;

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
        Pointer<JButton> bt_start = new Pointer<>();
        Pointer<JLabel> lb_db_path = new Pointer<>(),
                lb_db_login = new Pointer<>(),
                lb_db_pwd = new Pointer<>();
        Pointer<JTextField> tf_db_paht = new Pointer<>(),
                tf_db_login = new Pointer<>();
        Pointer<JPasswordField> pf_db_pwd = new Pointer<>();

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
                    add(new JLabel("Пароль:"), _cc0);
                    
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
                });
                bt_start.put(this);
            } });
            add(new JButton() { {
                setText("Exit");
                addActionListener((e) -> { System.exit(0); });
            } });
        } }, BorderLayout.SOUTH);
        
        pack();
        
        WindowLocation.WindowSetCenterScreenLocation(this);
    }
}
