package com.linx;

import com.linx.MasterPassword.MasterPasswordCheck;
import com.linx.MasterPassword.MasterPasswordSet;
import com.linx.PasswordStorer.StoredPasswordsUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }


        Dh dh = new Dh();

        if (!dh.masterPasswordExists()) {
            // master password does not exist
            // prompt user to create master password
            if (MasterPasswordSet.masterPasswordSetter()) {
                // master password has been created successfully
                // prompt user to login using master password
                passwordStorageAuth();
            }
        } else {
            // master password already exists
            // prompt user to authenticate
            passwordStorageAuth();

        }
        System.exit(0);
    }

    private static void passwordStorageAuth() {
        if (MasterPasswordCheck.masterPasswordChecker()) {
            // user has authenticated correctly
            // display the password manager window
            StoredPasswordsUI storedPasswordsUI = new StoredPasswordsUI();

//            storedPasswordsUI.setContentPane(storedPasswordsUI.getViewPanel());
//            storedPasswordsUI.setTitle("Password Manager");
//            storedPasswordsUI.setSize(600, 400);
//            storedPasswordsUI.setVisible(true);
//            storedPasswordsUI.setResizable(false);
//            storedPasswordsUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JDialog dialog = new JDialog(storedPasswordsUI, " Password Manager ", true);
            dialog.setContentPane(storedPasswordsUI.getViewPanel());
            dialog.setSize(600, 400);
            dialog.setVisible(true);
            dialog.setResizable(false);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        }
    }
}