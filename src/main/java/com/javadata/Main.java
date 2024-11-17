package com.javadata;

import com.formdev.flatlaf.FlatDarkLaf;
import com.javadata.ui.Dashboard;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set the Look and Feel
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // Launch the Dashboard
        SwingUtilities.invokeLater(() -> {
            Dashboard dashboard = new Dashboard();
            dashboard.setVisible(true);
        });
    }
}
