/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import quiz48.Pointer;
import quiz48.WindowLocation;

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
    
    public interface SQLParams {
        void where(String SQLWhere, Object[] params);
    }
    
    public interface DeleteFilterEvent {
        void deleteFilter(JPanel w, Filter f);
    }
    
    public interface Filter {
        JPanel createWidget(DeleteFilterEvent de);
        void SetSQLWhere(SQLParams i);
    }
    
    public FilterDlg(Window wnd, filterType t) {
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
                testField.put(_test_field);
                _test_field.setColumns(25);
                inpPanel.add(_test_field, _cc);
                setFilterButton.addActionListener((e) -> {
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
                _loginField.put(_login_field);
                _login_field.setColumns(25);
                inpPanel.add(_login_field, _cc);
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
                _nameField.put(_name_field);
                _name_field.setColumns(25);
                inpPanel.add(_name_field, _cc);
                break;
        }
        
        pack();
        WindowLocation.DialogSetCenterParentWindowLocation(wnd, this);
    }
}
