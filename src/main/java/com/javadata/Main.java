package com.javadata;

import com.formdev.flatlaf.FlatDarkLaf;
import com.javadata.ui.Dashboard;
import com.javadata.ui.LoginDialog;
import com.javadata.data.DatabaseManager;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Show splash screen
        JWindow splash = new JWindow();
        JLabel splashLabel = new JLabel(new ImageIcon("resources/bg_white.png"));
        splash.getContentPane().add(splashLabel);
        splash.pack();
        splash.setLocationRelativeTo(null);
        splash.setVisible(true);

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseManager.getInstance().closeConnection();
        }));

        SwingUtilities.invokeLater(() -> {
            JFrame dummy = new JFrame();
            LoginDialog loginDialog = new LoginDialog(dummy);
            loginDialog.setVisible(true);

            if (loginDialog.isAuthenticated()) {
                Dashboard dashboard = new Dashboard(loginDialog.getCurrentUser());
                dashboard.setVisible(true);
            } else {
                System.exit(0);
            }
            dummy.dispose();
        });
    }
}
