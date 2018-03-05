/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import quiz48.Pointer;
import quiz48.TaskQueue;
import quiz48.WindowLocation;
import quiz48.db.ConnectDB;
import quiz48.md5.md5;

/**
 *
 * @author vasya
 */
public class UserRegistration extends JDialog {
    public UserRegistration(Window wnd, quiz48.db.orm.User udb, ConnectDB conn, quiz48.gui.User.SetUserEntity ue) {
        super(wnd);
        setResizable(false);
        setTitle(
                udb == null ? 
                        "Создать нового пользователя" : 
                        "Редактировать данные пользователя");
        
        Pointer<JDialog> thisDlg = new Pointer<>(this);
        Pointer<JTextField> plogin = new Pointer<>(),
                pname = new Pointer<>();
        Pointer<JPasswordField> ppwd = new Pointer<>(),
                pcpwd = new Pointer<>();
        Pointer<Color> defaultBackground = new Pointer<>();
        
        setLayout(new BorderLayout());
        getContentPane().add(new JPanel() { {
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setLayout(new GridBagLayout());
        
            GridBagConstraints _cc = new GridBagConstraints();
            Insets _is1 = new Insets(0, 0, 5, 5),
                    is2 = _cc.insets,
                    _is3 = new Insets(3, 0, 3, 0);
        
            _cc.gridx = 0;
            _cc.gridy = 0;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = 1;
            _cc.gridheight = 1;
            _cc.insets = _is1;
            _cc.fill = GridBagConstraints.NONE;
            _cc.anchor = GridBagConstraints.EAST;
            add(new JLabel("Логин ([3-30] сиволов):"), _cc);

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
            defaultBackground.put(_login.getBackground());
            plogin.put(_login);
            _login.setColumns(25);
            if(udb != null) { _login.setText(udb.getLogin()); }
            add(_login, _cc);
            
            if(udb != null) {
                _cc.gridx = 2;
                _cc.gridy = 0;
                _cc.weightx = 0;
                _cc.weighty = 0;
                _cc.gridwidth = 1;
                _cc.gridheight = 1;
                _cc.insets = _is1;
                _cc.fill = GridBagConstraints.NONE;
                _cc.anchor = GridBagConstraints.CENTER;
                add(new JButton() { {
                    setIcon(AppIcons.instance().get("save16.png"));
                    setToolTipText("Сохранить изменения");
                    addActionListener((e) -> {
                        if(plogin.get().getText().length() < 3) {
                            plogin.get().setBackground(Color.red);
                            return;
                        }
                        else {
                            plogin.get().setBackground(defaultBackground.get());
                        }
                        
                        String __login = plogin.get().getText();
                        
                        TaskQueue.instance().addNewTask(() -> {
                            LoadingWindow.Callback cb = LoadingWindow.showLoadingWindow(thisDlg.get(), "Обновление логина пользователя...");
                            LoadingWindow.sleep(2);
                            try {
                                udb.putLogin(__login, conn);
                                cb.setInformation("Обновление логина пользователя... успешно");
                                LoadingWindow.sleep(2);
                                EventQueue.invokeLater(() -> { ue.set(udb); });
                            } catch (SQLException ex) {
                                cb.setInformation("Обновление логина пользователя... ошибка");
                                LoadingWindow.sleep(2);
                            }
                            cb.exit();
                        });
                    });
                } }, _cc);
            }
        
            _cc.gridx = 0;
            _cc.gridy = 1;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = udb != null ? 3 : 2;
            _cc.gridheight = 1;
            _cc.insets = _is3;
            _cc.fill = GridBagConstraints.HORIZONTAL;
            _cc.anchor = GridBagConstraints.CENTER;
            add(new JSeparator(), _cc);
            
            _cc.gridx = 0;
            _cc.gridy = 2;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = 1;
            _cc.gridheight = 1;
            _cc.insets = _is1;
            _cc.fill = GridBagConstraints.NONE;
            _cc.anchor = GridBagConstraints.EAST;
            add(new JLabel("Имя ([3-255] сиволов):"), _cc);

            _cc.gridx = 1;
            _cc.gridy = 2;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = 1;
            _cc.gridheight = 1;
            _cc.insets = is2;
            _cc.fill = GridBagConstraints.HORIZONTAL;
            _cc.anchor = GridBagConstraints.CENTER;
            JTextField _name = new JTextField();
            pname.put(_name);
            _name.setColumns(25);
            if(udb != null) { _name.setText(udb.getName()); }
            add(_name, _cc);
            
            if(udb != null) {
                _cc.gridx = 2;
                _cc.gridy = 2;
                _cc.weightx = 0;
                _cc.weighty = 0;
                _cc.gridwidth = 1;
                _cc.gridheight = 1;
                _cc.insets = _is1;
                _cc.fill = GridBagConstraints.NONE;
                _cc.anchor = GridBagConstraints.CENTER;
                add(new JButton() { {
                    setIcon(AppIcons.instance().get("save16.png"));
                    setToolTipText("Сохранить изменения");
                    addActionListener((e) -> {
                        if(pname.get().getText().length() < 3) {
                            pname.get().setBackground(Color.red);
                            return;
                        }
                        else {
                            pname.get().setBackground(defaultBackground.get());
                        }
                        
                        String __name = pname.get().getText();
                        
                        TaskQueue.instance().addNewTask(() -> {
                            LoadingWindow.Callback cb = LoadingWindow.showLoadingWindow(thisDlg.get(), "Обновление имени пользователя...");
                            LoadingWindow.sleep(2);
                            try {
                                udb.putName(__name, conn);
                                cb.setInformation("Обновление имени пользователя... успешно");
                                LoadingWindow.sleep(2);
                                EventQueue.invokeLater(() -> { ue.set(udb); });
                            } catch (SQLException ex) {
                                cb.setInformation("Обновление имени пользователя... ошибка");
                                LoadingWindow.sleep(2);
                            }
                            cb.exit();
                        });
                    });
                } }, _cc);
            }
        
            _cc.gridx = 0;
            _cc.gridy = 3;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = udb != null ? 3 : 2;
            _cc.gridheight = 1;
            _cc.insets = _is3;
            _cc.fill = GridBagConstraints.HORIZONTAL;
            _cc.anchor = GridBagConstraints.CENTER;
            add(new JSeparator(), _cc);
            
            _cc.gridx = 0;
            _cc.gridy = 4;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = 1;
            _cc.gridheight = 1;
            _cc.insets = _is1;
            _cc.fill = GridBagConstraints.NONE;
            _cc.anchor = GridBagConstraints.EAST;
            add(new JLabel("Пароль (минимум 3 сивола):"), _cc);

            _cc.gridx = 1;
            _cc.gridy = 4;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = 1;
            _cc.gridheight = 1;
            _cc.insets = is2;
            _cc.fill = GridBagConstraints.HORIZONTAL;
            _cc.anchor = GridBagConstraints.CENTER;
            JPasswordField _pwd = new JPasswordField();
            ppwd.put(_pwd);
            _pwd.setEchoChar('*');
            _pwd.setColumns(25);
            add(_pwd, _cc);
            
            _cc.gridx = 0;
            _cc.gridy = 5;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = 1;
            _cc.gridheight = 1;
            _cc.insets = _is1;
            _cc.fill = GridBagConstraints.NONE;
            _cc.anchor = GridBagConstraints.EAST;
            add(new JLabel("Повтор пароля:"), _cc);

            _cc.gridx = 1;
            _cc.gridy = 5;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = 1;
            _cc.gridheight = 1;
            _cc.insets = is2;
            _cc.fill = GridBagConstraints.HORIZONTAL;
            _cc.anchor = GridBagConstraints.CENTER;
            JPasswordField _pwdc = new JPasswordField();
            pcpwd.put(_pwdc);
            _pwdc.setEchoChar('*');
            _pwdc.setColumns(25);
            add(_pwdc, _cc);
            
            if(udb != null) {
                _cc.gridx = 2;
                _cc.gridy = 5;
                _cc.weightx = 0;
                _cc.weighty = 0;
                _cc.gridwidth = 1;
                _cc.gridheight = 1;
                _cc.insets = _is1;
                _cc.fill = GridBagConstraints.NONE;
                _cc.anchor = GridBagConstraints.CENTER;
                add(new JButton() { {
                    setIcon(AppIcons.instance().get("save16.png"));
                    setToolTipText("Сохранить изменения");
                    addActionListener((e) -> {
                        if(ppwd.get().getPassword().length < 3) {
                            ppwd.get().setBackground(Color.red);
                            return;
                        }
                        else {
                            ppwd.get().setBackground(defaultBackground.get());
                        }
                        
                        if(pcpwd.get().getPassword().length < 3) {
                            pcpwd.get().setBackground(Color.red);
                            return;
                        }
                        else {
                            pcpwd.get().setBackground(defaultBackground.get());
                        }
                        
                        if((new String(ppwd.get().getPassword())).compareTo(new String(pcpwd.get().getPassword())) != 0) {
                            pcpwd.get().setBackground(Color.red);
                            return;
                        }
                        else {
                            pcpwd.get().setBackground(defaultBackground.get());
                        }
                        
                        String __pwd = new String(ppwd.get().getPassword());
                        
                        TaskQueue.instance().addNewTask(() -> {
                            LoadingWindow.Callback cb = LoadingWindow.showLoadingWindow(thisDlg.get(), "Обновление пароля пользователя...");
                            LoadingWindow.sleep(2);
                            try {
                                String __pwd_hash = md5.calculate(__pwd);
                                udb.putPwd(__pwd_hash, conn);
                                cb.setInformation("Обновление пароля пользователя... успешно");
                                LoadingWindow.sleep(2);
                                EventQueue.invokeLater(() -> { ue.set(udb); });
                            } catch (SQLException ex) {
                                cb.setInformation("Обновление пароля пользователя... ошибка");
                                LoadingWindow.sleep(2);
                            }
                            cb.exit();
                        });
                    });
                } }, _cc);
            }
        
        } }, BorderLayout.CENTER);
        
        getContentPane().add(new JPanel() { {
            setLayout(new FlowLayout(FlowLayout.RIGHT));
            if(udb == null) {
                add(new JButton() { {
                    setText("Создать");
                    addActionListener((e) -> {
                        boolean fail = false;

                        if(plogin.get().getText().length() < 3) {
                            plogin.get().setBackground(Color.red);
                            fail = true;
                        }
                        else {
                            plogin.get().setBackground(defaultBackground.get());
                        }
                        if(pname.get().getText().length() < 3) {
                            pname.get().setBackground(Color.red);
                            fail = true;
                        }
                        else {
                            pname.get().setBackground(defaultBackground.get());
                        }
                        if(ppwd.get().getPassword().length < 3) {
                            ppwd.get().setBackground(Color.red);
                            fail = true;
                        }
                        else {
                            ppwd.get().setBackground(defaultBackground.get());
                        }
                        if(pcpwd.get().getPassword().length < 3) {
                            pcpwd.get().setBackground(Color.red);
                            fail = true;
                        }
                        else {
                            pcpwd.get().setBackground(defaultBackground.get());
                        }

                        if((new String(pcpwd.get().getPassword())).compareTo(new String(ppwd.get().getPassword())) != 0) {
                            pcpwd.get().setBackground(Color.red);
                            fail = true;
                        }
                        else {
                            pcpwd.get().setBackground(defaultBackground.get());
                        }

                        if(fail) { return; }
                        
                        String __login = plogin.get().getText(),
                                __name = pname.get().getText(),
                                __pwd = new String(ppwd.get().getPassword());

                        TaskQueue.instance().addNewTask(() -> {
                            LoadingWindow.Callback cb = LoadingWindow.showLoadingWindow(thisDlg.get(), "Создание нового пользователя...");
                            LoadingWindow.sleep(3);
                            Pointer<quiz48.db.orm.User> nue = new Pointer<>();
                            try {
                                nue.put(quiz48.db.orm.User.createUser(conn, __login, __name, __pwd));
                            } catch (SQLException ex) {
                                cb.setInformation("Создание нового пользователя... ошибка", Color.red);
                                System.out.println(ex);
                                LoadingWindow.sleep(3);
                                cb.exit();
                            }

                            if(nue.get() != null) {
                                cb.setInformation("Создание нового пользователя... успешно");
                                LoadingWindow.sleep(3);
                                cb.exit();
                                EventQueue.invokeLater(() -> { ue.set(nue.get()); });
                            }
                        });
                    });
                } });
            }
            add(new JButton() { {
                setText("Выход");
                addActionListener((e) -> {
                    thisDlg.get().dispose();
                });
            } });
        } }, BorderLayout.SOUTH);
        
        pack();
        setModal(true);
        WindowLocation.DialogSetCenterParentWindowLocation(wnd, this);
    }
}
