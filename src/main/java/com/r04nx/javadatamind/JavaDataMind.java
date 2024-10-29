package com.r04nx.javadatamind;

import javax.swing.*;
import com.formdev.flatlaf.FlatLightLaf;

public class JavaDataMind {
    public static void main(String[] args) {
        // Set the FlatLaf Look and Feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // Show the login form
        SwingUtilities.invokeLater(LoginForm::createAndShowLoginForm);

    }
}
