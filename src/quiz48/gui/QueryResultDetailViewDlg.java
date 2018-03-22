/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import quiz48.Pointer;
import quiz48.QuizTimer;
import quiz48.WindowLocation;
import quiz48.db.orm.QueryResult;

/**
 *
 * @author vasya
 */
public class QueryResultDetailViewDlg extends JDialog{
    public QueryResultDetailViewDlg(Window wnd, QueryResult rq) {
        super(wnd);
        setTitle("Результат");
        setResizable(false);
        setModal(true);
        Pointer<JDialog> thisDlg = new Pointer<>(this);

        setLayout(new BorderLayout());
        add(new JPanel() { {
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
            _cc.anchor = GridBagConstraints.NORTHEAST;
            add(new JLabel("Вопрос:"), _cc);

            _cc.gridx = 1;
            _cc.gridy = 0;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = 1;
            _cc.gridheight = 1;
            _cc.insets = is2;
            _cc.fill = GridBagConstraints.HORIZONTAL;
            _cc.anchor = GridBagConstraints.CENTER;
            add(new JScrollPane(new JPanel() { {
                setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                setLayout(new BorderLayout());
                setMaximumSize(new Dimension(700, 550));
                add(new JLabel(rq.query.Query), BorderLayout.CENTER);
            } }), _cc);
            
            _cc.gridx = 0;
            _cc.gridy = 1;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = 1;
            _cc.gridheight = 1;
            _cc.insets = _is1;
            _cc.fill = GridBagConstraints.NONE;
            _cc.anchor = GridBagConstraints.NORTHEAST;
            add(new JLabel("Ответ:"), _cc);

            _cc.gridx = 1;
            _cc.gridy = 1;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = 1;
            _cc.gridheight = 1;
            _cc.insets = is2;
            _cc.fill = GridBagConstraints.HORIZONTAL;
            _cc.anchor = GridBagConstraints.CENTER;
            add(new JLabel(rq.answer()), _cc);
            
            _cc.gridx = 0;
            _cc.gridy = 2;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = 1;
            _cc.gridheight = 1;
            _cc.insets = _is1;
            _cc.fill = GridBagConstraints.NONE;
            _cc.anchor = GridBagConstraints.NORTHEAST;
            add(new JLabel("Правильный ответ:"), _cc);

            _cc.gridx = 1;
            _cc.gridy = 2;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = 1;
            _cc.gridheight = 1;
            _cc.insets = is2;
            _cc.fill = GridBagConstraints.HORIZONTAL;
            _cc.anchor = GridBagConstraints.CENTER;
            add(new JLabel() { {
                if(rq.query.isVisibleAnswerInResult) {
                    setText(rq.query.Answer);
                }
                else {
                    setText("<html><strong style=\"color: red;\">запрещено к показу</strong></html>");
                }
            } }, _cc);
            
            _cc.gridx = 0;
            _cc.gridy = 3;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = 1;
            _cc.gridheight = 1;
            _cc.insets = _is1;
            _cc.fill = GridBagConstraints.NONE;
            _cc.anchor = GridBagConstraints.NORTHEAST;
            add(new JLabel("Затраченное время:"), _cc);

            _cc.gridx = 1;
            _cc.gridy = 3;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = 1;
            _cc.gridheight = 1;
            _cc.insets = is2;
            _cc.fill = GridBagConstraints.HORIZONTAL;
            _cc.anchor = GridBagConstraints.CENTER;
            add(new JLabel(QuizTimer.durationFormat(rq.time() * 1000, true)), _cc);
            
            _cc.gridx = 0;
            _cc.gridy = 4;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = 1;
            _cc.gridheight = 1;
            _cc.insets = _is1;
            _cc.fill = GridBagConstraints.NONE;
            _cc.anchor = GridBagConstraints.NORTHEAST;
            add(new JLabel("Результат:"), _cc);

            _cc.gridx = 1;
            _cc.gridy = 4;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = 1;
            _cc.gridheight = 1;
            _cc.insets = is2;
            _cc.fill = GridBagConstraints.HORIZONTAL;
            _cc.anchor = GridBagConstraints.CENTER;
            Color bgc = rq.fail().getResultColor();
            add(new JLabel(
                    String.format(
                            "<html>"
                                    + "<div style=\"padding: 5px; "
                                    + "background-color: #%2$02x%3$02x%4$02x;\">"
                                    + "%1$s</div</html>", 
                            rq.fail().getResultString(), bgc.getRed(), bgc.getGreen(), bgc.getBlue())), _cc);
            
            _cc.gridx = 0;
            _cc.gridy = 5;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = 1;
            _cc.gridheight = 1;
            _cc.insets = _is1;
            _cc.fill = GridBagConstraints.NONE;
            _cc.anchor = GridBagConstraints.NORTHEAST;
            add(new JLabel("Повторно:"), _cc);

            _cc.gridx = 1;
            _cc.gridy = 5;
            _cc.weightx = 0;
            _cc.weighty = 0;
            _cc.gridwidth = 1;
            _cc.gridheight = 1;
            _cc.insets = is2;
            _cc.fill = GridBagConstraints.HORIZONTAL;
            _cc.anchor = GridBagConstraints.CENTER;
            add(new JLabel(rq.duplicate ? 
                    "<html><div style=\"padding: 5px; background-color: yellow;\">Да</div></html>" : 
                    "<html><div style=\"padding: 5px; background-color: green;\">Нет</div></html>"), _cc);
        } }, BorderLayout.CENTER);
        add(new JPanel() { {
            setLayout(new FlowLayout(FlowLayout.RIGHT));
            add(new JButton() { {
                setText("Закрыть");
                addActionListener((e) -> { thisDlg.get().dispose(); });
            } });
        } }, BorderLayout.SOUTH);
        
        pack();
        
        WindowLocation.DialogSetCenterParentWindowLocation(wnd, this);
    }
}
