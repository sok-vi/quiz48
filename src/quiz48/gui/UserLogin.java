/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import quiz48.Pointer;
import quiz48.TaskQueue;
import quiz48.WindowLocation;
import quiz48.db.ConnectDB;

/**
 *
 * @author vasya
 */
public class UserLogin extends JDialog {
    public UserLogin(JFrame parent, ConnectDB conn, quiz48.gui.User.SetUserEntity ue) {
        super(parent);
        setResizable(false);
        setTitle("Авторизация");
        
        //поля
        Pointer<JTextField> loginField = new Pointer<>();
        Pointer<JPasswordField> pwdField = new Pointer<>();
        
        setLayout(new BorderLayout());
        getContentPane().add(new JPanel() { {
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setLayout(new GridBagLayout());
        
            GridBagConstraints _cc = new GridBagConstraints();
            Insets _is1 = new Insets(0, 0, 5, 5),
                    is2 = _cc.insets;
        
            _cc.gridx = 0;
            _cc.gridy = 0;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = 1;
            _cc.gridheight = 1;
            _cc.insets = _is1;
            _cc.fill = GridBagConstraints.NONE;
            _cc.anchor = GridBagConstraints.EAST;
            add(new JLabel("Логин:"), _cc);

            _cc.gridx = 1;
            _cc.gridy = 0;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = 1;
            _cc.gridheight = 1;
            _cc.insets = is2;
            _cc.fill = GridBagConstraints.HORIZONTAL;
            _cc.anchor = GridBagConstraints.CENTER;
            JTextField _login = new JTextField();
            loginField.put(_login);
            _login.setColumns(25);
            add(_login, _cc);

            _cc.gridx = 0;
            _cc.gridy = 1;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = 1;
            _cc.gridheight = 1;
            _cc.insets = _is1;
            _cc.fill = GridBagConstraints.NONE;
            _cc.anchor = GridBagConstraints.EAST;
            add(new JLabel("Пароль:"), _cc);

            _cc.gridx = 1;
            _cc.gridy = 1;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = 1;
            _cc.gridheight = 1;
            _cc.insets = is2;
            _cc.fill = GridBagConstraints.HORIZONTAL;
            _cc.anchor = GridBagConstraints.CENTER;
            JPasswordField _pwd = new JPasswordField();
            _pwd.setEchoChar('*');
            pwdField.put(_pwd);
            _pwd.setColumns(25);
            add(_pwd, _cc);
        }}, BorderLayout.CENTER);
        
        
        Pointer<JDialog> thisDlg = new Pointer<>(this);
        Pointer<Color> defaultTextColor = new Pointer<>(loginField.get().getBackground());
        getContentPane().add(new JPanel() {{
            setLayout(new FlowLayout(FlowLayout.RIGHT));
            add(new JButton() {{
                setText("Войти");
                addActionListener((e) -> {
                    boolean _fail = false;
                    
                    if(loginField.get().getText().length() == 0) {
                        loginField.get().setBackground(Color.red);
                        _fail = true;
                    }
                    else {
                        loginField.get().setBackground(defaultTextColor.get());
                    }
                    if(pwdField.get().getPassword().length == 0) {
                        pwdField.get().setBackground(Color.red);
                        _fail = true;
                    }
                    else {
                        pwdField.get().setBackground(defaultTextColor.get());
                    }
                    
                    if(_fail) { return; }
                    
                    String __login = loginField.get().getText(),
                            __pwd = new String(pwdField.get().getPassword());
                    
                    TaskQueue.instance().addNewTask(() -> {
                        LoadingWindow.Callback cb = LoadingWindow.showLoadingWindow(thisDlg.get(), "Проверка авторизации...");
                        try {
                            //LoadingWindow.sleep(2);
                            Pointer<quiz48.db.orm.User> _ue = new Pointer<>(quiz48.db.orm.User.loadUser(conn, __login, __pwd));
                            if(_ue.get() != null) {
                                cb.setInformation("Проверка авторизации... успешно!!!");
                                //LoadingWindow.sleep(2);
                                ue.set(_ue.get());
                            }
                            else {
                                cb.setInformation("Проверка авторизации... не удалось!!!", Color.red);
                                LoadingWindow.sleep(2);
                            }
                        } catch (SQLException ex) {
                            cb.setInformation("Проверка авторизации... ошибка", Color.RED);
                            LoadingWindow.sleep(3);
                        }
                        cb.exit();
                    });
                });
            }});
            add(new JButton() {{
                setText("Отмена");
                addActionListener((e) -> {
                    dispose();
                });
            }});
        }}, BorderLayout.SOUTH);
        
        pack();
        setModal(true);
        WindowLocation.DialogSetCenterParentWindowLocation(parent, this);
    }
}
