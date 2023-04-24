package com.linx.PasswordStorer;

import com.linx.Dh;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StoredPasswordsUI extends JFrame{
    private JTextField searchField;
    private JList<PasswordDataClass> listView;
    private JButton deleteButton;
    private JButton saveButton;
    private JButton generateButton;
    private JTextField descriptionField;
    private JTextField usernameField;
    private JTextField passwordField;
    private JPanel viewPanel;
    private JButton deselectButton;
    private JLabel strengthLabel;
    private JButton clearFieldsButton;

    private static final Dh dh = new Dh();
    private static final PasswordStrengthEvaluator passwordStrengthEvaluator = new PasswordStrengthEvaluator();

    public StoredPasswordsUI(){
        // fetch list on launch
        generateListView();
        strengthLabel.setText("");

        /**
         * event listener for save button
         * called when the button is clicked
         */
        saveButton.addActionListener(e -> {
            // get string values input into the text boxes
            String description = descriptionField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (description.length() != 0 && username.length() !=0 && password.length() != 0) {
                // fields are not null

                if (listView.isSelectionEmpty()) {
                    // if no element has been selected in the list consider this as a new entry
                    PasswordDataClass passwordDataClass = new PasswordDataClass();
                    passwordDataClass.setDescription(description);
                    passwordDataClass.setUsername(username);
                    passwordDataClass.setPassword(password);

                    dh.addEntry(passwordDataClass);
                } else {
                    // list element is selected so update the value in the database
                    PasswordDataClass update = listView.getSelectedValue();

                    update.setDescription(description);
                    update.setUsername(username);
                    update.setPassword(password);

                    strengthLabel.setText("");

                    dh.updateEntry(update);

                }

                // on successful update clear the text fields and refresh the list again to get updated values
                strengthLabel.setText("");
                descriptionField.setText("");
                usernameField.setText("");
                passwordField.setText("");
                generateListView();
            }
            else {
                // fields are null
                JOptionPane.showMessageDialog(null, "Fields are null");
            }

        });

        /**
         * event listener for listView
         * called when an element in the list is clicked
         */
        listView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                // get the object matching the selected entry
                PasswordDataClass selected = listView.getSelectedValue();

                for (PasswordDataClass entry: dh.fetchAll()){
                    // check for it's matching entry in the database
                    // on successful match set the text fields with its values
                    if (selected.equals(entry)){
                        descriptionField.setText(selected.getDescription());
                        usernameField.setText(selected.getUsername());
                        passwordField.setText(selected.getPassword());
                        setStrengthLabel(passwordField.getText());
                    }
                }
            }
        });

        /**
         * event listener for delete button
         * called when the button is clicked
         */
        deleteButton.addActionListener(e -> {
            try {
                // get the object matching to the selected entry
                // and delete the entry matching with this object
                PasswordDataClass selected = listView.getSelectedValue();
                dh.deleteEntry(selected);

                // after deletion set clear the text fields and regenerate the list view to match the updates
                descriptionField.setText("");
                usernameField.setText("");
                passwordField.setText("");
                strengthLabel.setText("");
                generateListView();
            }catch(Exception exception){
                JOptionPane.showMessageDialog(null, "Please select an item");
            }
        });

        /**
         * event listener for the deselect button.
         * called when the button is clicked
         */
        deselectButton.addActionListener(e -> {
                // if a list element is selected
                // deselect the selected list item and clear the text boxes
                if (!listView.isSelectionEmpty()) {
                    listView.clearSelection();
                    descriptionField.setText("");
                    usernameField.setText("");
                    passwordField.setText("");
                    strengthLabel.setText("");
                }
            }
        );

        /**
         * event listener for the text field
         * called every time a key has been released
         */
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                // similar to the generateListView() method except in this case it is filtered
                // using the user provided text

                DefaultListModel<PasswordDataClass> defaultListModel = new DefaultListModel<>();

                for (PasswordDataClass entry: dh.fetchAll()){
                    if (entry.getDescription().toLowerCase().contains(searchField.getText().toLowerCase())){
                        defaultListModel.addElement(entry);
                    }
                }
                listView.setModel(defaultListModel);
            }
        });

        /**
         * event listener for generate button
         * called when the button gets clicked
         */
        generateButton.addActionListener(e -> {
            // calls the PasswordGenerator which returns a randomly generated string and applies it to the passwordField
            passwordField.setText(
                    new PasswordGenerator().generateRandomString()
            );

            setStrengthLabel(passwordField.getText());
        });
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                setStrengthLabel(passwordField.getText());
            }
        });

        /**
         *  event listener for clear fields button
         *  called when the button gets clicked
         */
        clearFieldsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                descriptionField.setText("");
                usernameField.setText("");
                passwordField.setText("");
                strengthLabel.setText("");
            }
        });
    }

    /**
     * Obtains an ArrayList of password entries from the database and attaches it to the jlist view
     */
    public void generateListView(){
        DefaultListModel<PasswordDataClass> defaultListModel = new DefaultListModel<>();

        for (PasswordDataClass entry: dh.fetchAll()){
            defaultListModel.addElement(entry);
        }

        listView.setModel(defaultListModel);
    }

    public JPanel getViewPanel(){
        return viewPanel;
    }

    /**
     * sets the strengthLabel with password strength messages according to a preset score through PasswordStrengthEvaluator
     * @param pass the password whose strength must be measured
     */
    public void setStrengthLabel(String pass){
        int score = passwordStrengthEvaluator.evaluatePasswordStrength(pass);

        if (score == 0){
            strengthLabel.setText("Invalid Password");
            strengthLabel.setForeground(Color.decode("#FF0000"));
        } else if (score == 1) {
            // very weak
            strengthLabel.setText("Strength: Very Weak");
            strengthLabel.setForeground(Color.decode("#FF0000"));
        } else if (score == 2) {
            // weak
            strengthLabel.setText("Strength: Weak");
            strengthLabel.setForeground(Color.decode("#FFA500"));

        } else if (score == 3) {
            // fair
            strengthLabel.setText("Strength: Fair");
            strengthLabel.setForeground(Color.decode("#FFFF00"));

        } else if (score == 4) {
            // good
            strengthLabel.setText("Strength: Good");
            strengthLabel.setForeground(Color.decode("#00FF00"));
        } else if (score == 5) {
            // great
            strengthLabel.setText("Strength: Great");
            strengthLabel.setForeground(Color.decode("#006400"));
        }

    }
}