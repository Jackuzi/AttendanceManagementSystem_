package GUI;

import javax.swing.*;
import java.awt.event.*;

public class DeleteConfirmDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel dialogText;
    private boolean go = true;


    public DeleteConfirmDialog(String message) {
        setContentPane(contentPane);
        setModal(true);
        setLocationRelativeTo(null);
        dialogText.setText(message);


        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
                //setGo(go);
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
                //setGo(go);
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
                //setGo(go);
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }


    public boolean getGo() {
        return go;

    }

    private void onOK() {
// add your code here
        go = true;
        dispose();

    }

    private void onCancel() {
// add your code here if necessary
        go = false;
        dispose();


    }

}
