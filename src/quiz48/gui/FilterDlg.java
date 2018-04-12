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
import java.awt.Window;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import quiz48.Pointer;
import quiz48.WindowLocation;
import quiz48.db.orm.TestResultWithRating.SQLWhereCreator;

/**
 *
 * @author vasya
 */
public class FilterDlg extends JDialog {
    public enum filterType {
        test,//по названию теста
        date,//по дате (интервал один день) или интервалу дат
        login,//по логину пользователя
        name//по имени пользователя
    }
    
    
    public static abstract class FilterWidget extends JPanel implements Filter {
        protected final JLabel m_Label;
        protected final JButton m_Button;
        
        public FilterWidget() {
            super();
            setLayout(new FlowLayout(FlowLayout.LEFT));
            m_Label = new JLabel();
            add(m_Label);
            m_Button = new JButton();
            m_Button.setIcon(AppIcons.instance().get("close12.png"));
            add(m_Button);
            setBorder(BorderFactory.createEtchedBorder());
        }
        
        public final void setCloseEvent(DeleteFilterEvent de) {
            m_Button.addActionListener((e) -> { de.deleteFilter(this); });
        }
    }
    
    public interface DeleteFilterEvent {
        void deleteFilter(FilterWidget f);
    }
    
    public interface Filter {
        void SetSQLWhere(SQLWhereCreator i);
    }
    
    public interface FilterSetCallback {
        void set(FilterWidget newFilter);
    }
    
    public static class TestFilter extends FilterWidget implements Filter {
        private final String test;
        public TestFilter(String test) {
            super();
            this.test = test;
            m_Label.setText(String.format("Тест: %1$s", test));
        }
        
        @Override
        public void SetSQLWhere(SQLWhereCreator i) {
            i.where(
                    "(SELECT COUNT(*) FROM quiz qzt WHERE qzt.id=qr.quiz_id AND name LIKE ?)>0", 
                    (s, si) -> { s.setString(si, "%" + test + "%"); return 1; }
            );
        }
        
    }
    
    public static class DataFilter extends FilterWidget implements Filter {
        private final Date date1, date2;
        public DataFilter(Date date) {
            super();
            date1 = date;
            date2 = null;
            m_Label.setText(String.format("Дата: %1$td.%1$tm.%1$tY", date));
        }
        
        public DataFilter(Date date1, Date date2) {
            super();
            this.date1 = date1;
            this.date2 = date2;
            m_Label.setText(String.format("Дата: %1$td.%1$tm.%1$tY %1$tH:%1$tM - %2$td.%2$tm.%2$tY %2$tH:%2$tM", date1, date2));
        }
        
        @Override
        public void SetSQLWhere(SQLWhereCreator i) {
            i.where(
                    "qr.date>? AND qr.date<?", 
                    (s, si) -> { 
                        s.setTimestamp(si, new Timestamp(date1.getTime()));
                        if(date2 == null) {
                            s.setTimestamp(si + 1, new Timestamp(date1.getTime() + 1000 * 60 * 60 * 24));
                        }
                        else {
                            s.setTimestamp(si + 1, new Timestamp(date2.getTime()));
                        }
                        return 2; 
                    }
            );
        }
        
    }
    
    public static class LoginFilter extends FilterWidget implements Filter {
        private final String login;
        public LoginFilter(String login) {
            super();
            this.login = login;
            m_Label.setText(String.format("Логин: %1$s", login));
        }
        
        @Override
        public void SetSQLWhere(SQLWhereCreator i) {
            i.where(
                    "(SELECT COUNT(*) FROM users usrs0 WHERE usrs0.id=qr.user_id AND login LIKE ?)>0", 
                    (s, si) -> { s.setString(si, "%" + login + "%"); return 1; }
            );
        }
        
    }
    
    public static class NameFilter extends FilterWidget implements Filter {
        private final String name;
        public NameFilter(String name) {
            super();
            this.name = name;
            m_Label.setText(String.format("Имя: %1$s", name));
        }
        
        @Override
        public void SetSQLWhere(SQLWhereCreator i) {
            i.where(
                    "(SELECT COUNT(*) FROM users usrs0 WHERE usrs0.id=qr.user_id AND name LIKE ?)>0", 
                    (s, si) -> { s.setString(si, "%" + name + "%"); return 1; }
            );
        }
        
    }
    
    public FilterDlg(Window wnd, filterType t, FilterSetCallback fcb) {
        super(wnd);
        setModal(true);
        setResizable(false);
        
        Pointer<JDialog> thisDlg = new Pointer<>(this);
        setLayout(new BorderLayout());
        JButton setFilterButton = new JButton("Установить");
        
        add(new JPanel() { {
            setLayout(new FlowLayout(FlowLayout.RIGHT));
            add(setFilterButton);
            add(new JButton() { {
                setText("Закрыть");
                addActionListener((e) -> { thisDlg.get().dispose(); });
            } });
        } }, BorderLayout.SOUTH);
        
        JPanel inpPanel = new JPanel();
        inpPanel.setLayout(new GridBagLayout());
        inpPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        add(inpPanel, BorderLayout.CENTER);
        
        Pointer<Color> defBkColor = new Pointer<>();
        GridBagConstraints _cc = new GridBagConstraints();
        Insets _is1 = new Insets(0, 0, 5, 5), is2 = _cc.insets;
        
        switch(t) {
            case test:
                Pointer<JTextField> testField = new Pointer<>();
                setTitle("Фильтр по названю теста");
                _cc.gridx = 0;
                _cc.gridy = 0;
                _cc.weightx = 0;
                _cc.weighty = 0;
                _cc.gridwidth = 1;
                _cc.gridheight = 1;
                _cc.insets = _is1;
                _cc.fill = GridBagConstraints.NONE;
                _cc.anchor = GridBagConstraints.EAST;
                inpPanel.add(new JLabel("Название теста:"), _cc);

                _cc.gridx = 1;
                _cc.gridy = 0;
                _cc.weightx = 0;
                _cc.weighty = 0;
                _cc.gridwidth = 1;
                _cc.gridheight = 1;
                _cc.insets = is2;
                _cc.fill = GridBagConstraints.HORIZONTAL;
                _cc.anchor = GridBagConstraints.CENTER;
                JTextField _test_field = new JTextField();
                _test_field.setColumns(25);
                inpPanel.add(_test_field, _cc);
                setFilterButton.addActionListener((e) -> {
                    if(_test_field.getText().length() == 0) {
                        _test_field.setBackground(Color.red);
                    }
                    else {
                        fcb.set(new TestFilter(_test_field.getText()));
                        dispose();
                    }
                });
                break;
            case date:
                Pointer<JTextField> date1Field = new Pointer<>(),
                        date2Field = new Pointer<>();
                Pointer<JLabel> label1 = new Pointer<>(),
                        label0 = new Pointer<>(),
                        label2 = new Pointer<>(),
                        label3 = new Pointer<>();
                Pointer<JCheckBox> chBox = new Pointer<>();
                String date1lablel = "Укажите дату:",
                        date2label = "Дата/время с:",
                        date3label = "<html><span style=\"color:green;font-size: 10pt;\">формат: дд.мм.гггг</span></html>",
                        date4label = "<html><span style=\"color:green;font-size: 10pt;\">формат: дд.мм.гггг чч:мм</span></html>";
                setTitle("Фильтр по дате прохождения");
                _cc.gridx = 0;
                _cc.gridy = 0;
                _cc.weightx = 0;
                _cc.weighty = 0;
                _cc.gridwidth = 1;
                _cc.gridheight = 1;
                _cc.insets = _is1;
                _cc.fill = GridBagConstraints.NONE;
                _cc.anchor = GridBagConstraints.EAST;
                inpPanel.add(new JLabel() { {
                    setText(date1lablel);
                    label0.put(this);
                } }, _cc);

                _cc.gridx = 1;
                _cc.gridy = 0;
                _cc.weightx = 0;
                _cc.weighty = 0;
                _cc.gridwidth = 1;
                _cc.gridheight = 1;
                _cc.insets = is2;
                _cc.fill = GridBagConstraints.HORIZONTAL;
                _cc.anchor = GridBagConstraints.CENTER;
                JTextField _date1_field = new JTextField();
                _date1_field.setText((new SimpleDateFormat("dd.MM.yyyy")).format(new Date()));
                defBkColor.put(_date1_field.getBackground());
                date1Field.put(_date1_field);
                _date1_field.setColumns(25);
                inpPanel.add(_date1_field, _cc);
                
                _cc.gridx = 1;
                _cc.gridy = 1;
                _cc.weightx = 0;
                _cc.weighty = 0;
                _cc.gridwidth = 1;
                _cc.gridheight = 1;
                _cc.insets = _is1;
                _cc.fill = GridBagConstraints.NONE;
                _cc.anchor = GridBagConstraints.EAST;
                inpPanel.add(new JLabel() { {
                    setText(date3label);
                    label1.put(this);
                } }, _cc);
                
                _cc.gridx = 0;
                _cc.gridy = 2;
                _cc.weightx = 0;
                _cc.weighty = 0;
                _cc.gridwidth = 2;
                _cc.gridheight = 1;
                _cc.insets = _is1;
                _cc.fill = GridBagConstraints.NONE;
                _cc.anchor = GridBagConstraints.WEST;
                inpPanel.add(new JCheckBox() { {
                    setText("- указать интервал");
                    chBox.put(this);
                    addChangeListener((e) -> {
                        boolean en = chBox.get().isSelected();
                        label0.get().setText(!en ? date1lablel : date2label);
                        label1.get().setText(en ? date4label : date3label);
                        label2.get().setEnabled(en);
                        label3.get().setEnabled(en);
                        date2Field.get().setEnabled(en);
                        
                        String sdate = date1Field.get().getText();
                        if(en) {
                            if(sdate.length() > 0) {
                                try {
                                    Date d = (new SimpleDateFormat("dd.MM.yyyy")).parse(sdate);
                                    SimpleDateFormat dtf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                                    date1Field.get().setText(dtf.format(d));
                                    d.setTime(d.getTime() + 1000 * 60 * 60 * 24);
                                    date2Field.get().setText(dtf.format(d));
                                } catch (ParseException ex) { }
                            }
                        }
                        else {
                            try {
                                date1Field.get().setText(
                                        (new SimpleDateFormat("dd.MM.yyyy")).format(
                                                (new SimpleDateFormat("dd.MM.yyyy HH:mm")).parse(sdate)));
                            } catch (ParseException ex) { }
                        }
                    });
                } }, _cc);
                
                _cc.gridx = 0;
                _cc.gridy = 3;
                _cc.weightx = 0;
                _cc.weighty = 0;
                _cc.gridwidth = 1;
                _cc.gridheight = 1;
                _cc.insets = _is1;
                _cc.fill = GridBagConstraints.NONE;
                _cc.anchor = GridBagConstraints.EAST;
                inpPanel.add(new JLabel() { {
                    setText("Дата/время по:");
                    setEnabled(false);
                    label2.put(this);
                } }, _cc);

                _cc.gridx = 1;
                _cc.gridy = 3;
                _cc.weightx = 0;
                _cc.weighty = 0;
                _cc.gridwidth = 1;
                _cc.gridheight = 1;
                _cc.insets = is2;
                _cc.fill = GridBagConstraints.HORIZONTAL;
                _cc.anchor = GridBagConstraints.CENTER;
                JTextField _date2_field = new JTextField();
                date2Field.put(_date2_field);
                _date2_field.setEnabled(false);
                _date2_field.setColumns(25);
                inpPanel.add(_date2_field, _cc);
                
                _cc.gridx = 1;
                _cc.gridy = 4;
                _cc.weightx = 0;
                _cc.weighty = 0;
                _cc.gridwidth = 1;
                _cc.gridheight = 1;
                _cc.insets = _is1;
                _cc.fill = GridBagConstraints.NONE;
                _cc.anchor = GridBagConstraints.EAST;
                inpPanel.add(new JLabel() { {
                    setText(date4label);
                    setEnabled(false);
                    label3.put(this);
                } }, _cc);
                setFilterButton.addActionListener((e) -> {
                    if(chBox.get().isSelected()) {
                        boolean result = true;
                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                        Date d1 = null, d2 = null;
                        date1Field.get().setBackground(defBkColor.get());
                        date2Field.get().setBackground(defBkColor.get());
                        try {
                            d1 = sdf.parse(date1Field.get().getText());
                        } catch (ParseException ex) {
                            date1Field.get().setBackground(Color.red);
                            return;
                        }
                        try {
                            d2 = sdf.parse(date2Field.get().getText());
                        } catch (ParseException ex) {
                            date2Field.get().setBackground(Color.red);
                            return;
                        }
                        if(d1.getTime() >= d2.getTime()) {
                            date2Field.get().setBackground(Color.red);
                            return;
                        }
                        fcb.set(new DataFilter(d1, d2));
                        dispose();
                    }
                    else {
                        boolean result = true;
                        try {
                            fcb.set(new DataFilter((new SimpleDateFormat("dd.MM.yyyy")).parse(date1Field.get().getText())));
                            dispose();
                        } catch (ParseException ex) { result = true; }
                        if(!result) { date1Field.get().setBackground(Color.red); }
                    }
                });
                break;
            case login:
                Pointer<JTextField> _loginField = new Pointer<>();
                setTitle("Фильтр по логину тестируемого");
                _cc.gridx = 0;
                _cc.gridy = 0;
                _cc.weightx = 0;
                _cc.weighty = 0;
                _cc.gridwidth = 1;
                _cc.gridheight = 1;
                _cc.insets = _is1;
                _cc.fill = GridBagConstraints.NONE;
                _cc.anchor = GridBagConstraints.EAST;
                inpPanel.add(new JLabel("Логин тестируемого:"), _cc);

                _cc.gridx = 1;
                _cc.gridy = 0;
                _cc.weightx = 0;
                _cc.weighty = 0;
                _cc.gridwidth = 1;
                _cc.gridheight = 1;
                _cc.insets = is2;
                _cc.fill = GridBagConstraints.HORIZONTAL;
                _cc.anchor = GridBagConstraints.CENTER;
                JTextField _login_field = new JTextField();
                _login_field.setColumns(25);
                inpPanel.add(_login_field, _cc);
                setFilterButton.addActionListener((e) -> {
                    if(_login_field.getText().length() == 0) {
                        _login_field.setBackground(Color.red);
                    }
                    else {
                        fcb.set(new LoginFilter(_login_field.getText()));
                        dispose();
                    }
                });
                break;
            case name:
                Pointer<JTextField> _nameField = new Pointer<>();
                setTitle("Фильтр по имени");
                _cc.gridx = 0;
                _cc.gridy = 0;
                _cc.weightx = 0;
                _cc.weighty = 0;
                _cc.gridwidth = 1;
                _cc.gridheight = 1;
                _cc.insets = _is1;
                _cc.fill = GridBagConstraints.NONE;
                _cc.anchor = GridBagConstraints.EAST;
                inpPanel.add(new JLabel("Имя тестируемого:"), _cc);

                _cc.gridx = 1;
                _cc.gridy = 0;
                _cc.weightx = 0;
                _cc.weighty = 0;
                _cc.gridwidth = 1;
                _cc.gridheight = 1;
                _cc.insets = is2;
                _cc.fill = GridBagConstraints.HORIZONTAL;
                _cc.anchor = GridBagConstraints.CENTER;
                JTextField _name_field = new JTextField();
                _name_field.setColumns(25);
                inpPanel.add(_name_field, _cc);
                setFilterButton.addActionListener((e) -> {
                    if(_name_field.getText().length() == 0) {
                        _name_field.setBackground(Color.red);
                    }
                    else {
                        fcb.set(new NameFilter(_name_field.getText()));
                        dispose();
                    }
                });
                break;
        }
        
        pack();
        WindowLocation.DialogSetCenterParentWindowLocation(wnd, this);
    }
}
