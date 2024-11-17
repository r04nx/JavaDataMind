package com.javadata.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SettingsPanel extends JPanel {
    public SettingsPanel() {
        setLayout(new BorderLayout());
        JTextField nameField = new JTextField(20);
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);

        JButton changePasswordBtn = new JButton("Change Password");

        JPanel settingsForm = new JPanel(new GridLayout(4, 2));
        settingsForm.add(new JLabel("Name:"));
        settingsForm.add(nameField);
        settingsForm.add(new JLabel("Username:"));
        settingsForm.add(usernameField);
        settingsForm.add(new JLabel("Password:"));
        settingsForm.add(passwordField);
        settingsForm.add(new JLabel());
        settingsForm.add(changePasswordBtn);

        add(settingsForm, BorderLayout.CENTER);

        changePasswordBtn.addActionListener(e -> {
            // Logic to change the password (simulated here)
            JOptionPane.showMessageDialog(this, "Password Changed!");
        });
    }
}
