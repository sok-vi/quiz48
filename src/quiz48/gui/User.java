/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.gui;

import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import quiz48.Pointer;
import quiz48.db.ConnectDB;

/**
 *
 * @author vasya
 */
public class User {
    private quiz48.db.orm.User m_UserEntity = null;
    
    public final quiz48.db.orm.User getUserEntity() {
        return m_UserEntity;
    }
    
    public interface SetUserEntity {
        void set(quiz48.db.orm.User ue);
    }
    
    public interface LoginListener {
        void loginEvent(boolean login);
    }
    
    private LoginListener m_LoginListener = null;
    
    public final void addLoginListener(LoginListener ll) {
        m_LoginListener = ll;
    }
    
    private final void doLoginEvent(boolean login) {
        if(m_LoginListener != null) {
            m_LoginListener.loginEvent(login);
        }
    }
    
    public final boolean isLogin() {
        return m_UserEntity != null;
    }
    
    public final void initializeUserPanel(JFrame wnd, JPanel up, ConnectDB conn) {
        up.removeAll();
        up.setLayout(new FlowLayout(FlowLayout.LEFT));
        if(m_UserEntity != null) {
            up.add(new JLabel(AppIcons.instance().get("user48.png")));
            up.add(new JLabel(
                    String.format(
                            "<html>%1$s  {<strong>%2$s</strong>}%3$s</html>", 
                            m_UserEntity.getName(), 
                            m_UserEntity.getLogin(), 
                            m_UserEntity.isAdmin ? 
                                    " (<strong><span color=\"red\">!</span></strong>)" : 
                                    "")));
            up.add(new JButton() {{
                setText("Изменить...");
                setIcon(AppIcons.instance().get("user_edit32.png"));
                addActionListener((e) -> {
                    Pointer<UserRegistration> pdlg = new Pointer<>();
                    UserRegistration dlg = new UserRegistration(
                            wnd, 
                            m_UserEntity, 
                            conn, 
                            (ue) -> {
                                m_UserEntity = ue;
                                initializeUserPanel(wnd, up, conn);
                                up.updateUI();
                            });
                    pdlg.put(dlg);
                    dlg.setVisible(true);
                });
            }});
            up.add(new JButton() {{
                setText("Выйти");
                setIcon(AppIcons.instance().get("logout32.png"));
                addActionListener((e) -> {
                    m_UserEntity = null;
                    doLoginEvent(false);
                    initializeUserPanel(wnd, up, conn);
                    up.updateUI();
                });
            }});
        }
        else {
            up.add(new JLabel(AppIcons.instance().get("user_anonymous48.png")));
            up.add(new JButton() { {
                setText("Войти...");
                setIcon(AppIcons.instance().get("auth32.png"));
                addActionListener((e) -> {
                    Pointer<UserLogin> pdlg = new Pointer<>();
                    UserLogin dlg = new UserLogin(
                            wnd, 
                            conn, 
                            (ue) -> { 
                                m_UserEntity = ue;
                                doLoginEvent(true);
                                pdlg.get().dispose();
                                initializeUserPanel(wnd, up, conn); 
                                up.updateUI();
                            });
                    pdlg.put(dlg);
                    dlg.setVisible(true);
                });
            } });
            up.add(new JButton() { {
                setText("Регистрация...");
                setIcon(AppIcons.instance().get("user_add32.png"));
                addActionListener((e) -> {
                    Pointer<UserRegistration> pdlg = new Pointer<>();
                    UserRegistration dlg = new UserRegistration(
                            wnd, 
                            null, 
                            conn, 
                            (ue) -> {
                                m_UserEntity = ue;
                                doLoginEvent(true);
                                pdlg.get().dispose();
                                initializeUserPanel(wnd, up, conn);
                                up.updateUI();
                            });
                    pdlg.put(dlg);
                    dlg.setVisible(true);
                });
            } });
        }
    }
}
