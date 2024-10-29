package com.r04nx.javadatamind;

import javax.swing.*;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.imageio.ImageIO;

public class LoginForm {

    public static void main(String[] args) {
        // Set the FlatLaf Look and Feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // Ensure GUI creation is done on the Event Dispatch Thread
        SwingUtilities.invokeLater(LoginForm::createAndShowLoginForm);
    }

    public static void createAndShowLoginForm() {
        // Create a JFrame
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 250);
        frame.setLocationRelativeTo(null); // Center on screen

        // Create a JPanel with a gradient background
        JPanel panel = new GradientPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title Label
        JLabel titleLabel = new JLabel("Welcome Back!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across 2 columns
        panel.add(titleLabel, gbc);

        // Logo
        ImageIcon logoIcon = null;
        try {
            logoIcon = new ImageIcon(ImageIO.read(new File("assets/logo.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        JLabel logoLabel = new JLabel(logoIcon);
        gbc.gridy = 1;
        gbc.gridwidth = 2; // Span across 2 columns
        panel.add(logoLabel, gbc);

        // Username Label and Text Field
        JLabel userLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(userLabel, gbc);

        JTextField userText = new JTextField(15);
        gbc.gridx = 1;
        panel.add(userText, gbc);

        // Password Label and Text Field
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(passwordLabel, gbc);

        JPasswordField passwordText = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passwordText, gbc);

        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(Color.decode("#6C63FF")); // Color for the button
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                JOptionPane.showMessageDialog(frame, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        gbc.gridx = 1;
        gbc.gridy = 4; // Move the button down
        panel.add(loginButton, gbc);

        // Add panel to frame
        frame.getContentPane().add(panel);

        // Make the frame visible
        frame.setVisible(true);
    }

    // Custom JPanel for gradient background
    static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gp = new GradientPaint(0, 0, Color.decode("#FF6F61"), 0, getHeight(), Color.decode("#6C63FF")); // Gradient from Coral to Indigo
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
        }
    }
}
