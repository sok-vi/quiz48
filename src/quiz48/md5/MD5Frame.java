/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.md5;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import quiz48.Pointer;
import quiz48.WindowLocation;

/**
 *
 * @author vasya
 */
public class MD5Frame extends JFrame {
    public MD5Frame() {
        super();
        setTitle("MD5 calculate");
        setResizable(false);

        setLayout(new BorderLayout());
        Container _cp = getContentPane();

        //поля
        Pointer<JTextField> textPwd = new Pointer<>(),
                textMD5 = new Pointer<>();
        _cp.add(new JPanel() { {
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
            add(new JLabel("Password:"), _cc);

            _cc.gridx = 1;
            _cc.gridy = 0;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = 1;
            _cc.gridheight = 1;
            _cc.insets = is2;
            _cc.fill = GridBagConstraints.HORIZONTAL;
            _cc.anchor = GridBagConstraints.CENTER;
            JTextField _password = new JTextField();
            _password.setColumns(25);
            add(_password, _cc);
            textPwd.put(_password);

            _cc.gridx = 0;
            _cc.gridy = 1;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = 1;
            _cc.gridheight = 1;
            _cc.insets = _is1;
            _cc.fill = GridBagConstraints.NONE;
            _cc.anchor = GridBagConstraints.EAST;
            add(new JLabel("MD5:"), _cc);

            _cc.gridx = 1;
            _cc.gridy = 1;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = 1;
            _cc.gridheight = 1;
            _cc.insets = is2;
            _cc.fill = GridBagConstraints.HORIZONTAL;
            _cc.anchor = GridBagConstraints.CENTER;
            JTextField _md5 = new JTextField();
            _md5.setColumns(25);
            add(_md5, _cc);
            textMD5.put(_md5);

        } }, BorderLayout.CENTER);
        
        _cp.add(new JPanel() { {
            setLayout(new FlowLayout(FlowLayout.RIGHT));

            JButton _btn = new JButton("Calculate");
            Color _textFieldBackground = textPwd.get().getBackground();
            _btn.addActionListener((ActionEvent e) -> {
                String _pwd = textPwd.get().getText();
                if(_pwd.length() == 0) {
                    textPwd.get().setBackground(Color.RED);
                    return;
                }
                else {
                    textPwd.get().setBackground(_textFieldBackground);
                }

                textMD5.get().setText(md5.calculate(_pwd));
            });
            add(_btn);

            JButton _btn0 = new JButton("Exit");
            _btn0.addActionListener((ActionEvent e) -> { System.exit(0); });
            add(_btn0);
        
        } }, BorderLayout.SOUTH);

        pack();

        WindowLocation.WindowSetCenterScreenLocation(this);
    }
}
