package com.javadata.ui;

import javax.swing.*;

public class HelpAboutDialog extends JPanel {
    public HelpAboutDialog() {
        JTextArea aboutText = new JTextArea();
        aboutText.setText("JavaDataMind\nVersion 1.0\n\nDeveloped by Your Name\nContact: youremail@example.com");
        aboutText.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(aboutText);
        add(scrollPane);
    }
}
