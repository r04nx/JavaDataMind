package com.javadata.ui;

import javax.swing.*;
import java.awt.*;

public class HelpAboutDialog extends JPanel {
    public HelpAboutDialog() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea aboutText = new JTextArea();
        aboutText.setText("""
            JavaDataMind - Data Visualization Tool
            Version 1.0
            
            A powerful tool for data analysis and visualization.
            
            Features:
            - CSV data import
            - Multiple chart types
            - Data management
            - User authentication
            
            © 2024 JavaDataMind
            """);
        aboutText.setEditable(false);
        aboutText.setBackground(null);
        aboutText.setFont(new Font("Dialog", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(aboutText);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }
}
