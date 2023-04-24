package com.linx.MasterPassword;

import com.linx.Dh;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.NoSuchAlgorithmException;

public class MasterPasswordCheck extends JFrame{
    private JPasswordField enterPassword;
    private JPanel checkerPanel;
    private JButton submitButton;

    private boolean flag = false;

    private static final Dh dh = new Dh();

    private MasterPasswordCheck() {
        /**
         * event listener for submit button
         * called when the button gets clicked
         */
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (dh.checkMasterPassword(String.valueOf(enterPassword.getPassword()))){
                        // if password matches the stored password

                        // TODO
                        flag = true;

                        // close the window upon clicking the submit button
                        Window window = SwingUtilities.getWindowAncestor(submitButton);
                        if (window instanceof JDialog){
                            JDialog dialog = (JDialog) window;
                            dialog.dispose();
                        }
                    }
                    else{
                        // password does not match the stored password
                        // inform user and clear text fields
                        JOptionPane.showMessageDialog(null, "Invalid password");
                        enterPassword.setText("");

                    }
                } catch (NoSuchAlgorithmException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public boolean getFlag(){
        return flag;
    }


    public static boolean masterPasswordChecker(){
        MasterPasswordCheck masterPasswordCheck = new MasterPasswordCheck();
        JDialog dialog = new JDialog(masterPasswordCheck, "Password Manager", true); // create a modal dialog
        dialog.setContentPane(masterPasswordCheck.getCheckerPanel());
        dialog.setSize(200, 120);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // exit the dialog on close
        dialog.setVisible(true); // show the dialog

        return masterPasswordCheck.getFlag();
    }

    public JPanel getCheckerPanel(){
        return checkerPanel;
    }
}
