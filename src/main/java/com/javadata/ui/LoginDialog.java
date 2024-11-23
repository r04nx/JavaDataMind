package com.javadata.ui;

import com.javadata.model.UserProfile;
import com.javadata.service.UserService;
import com.javadata.ui.components.PlaceholderPasswordField;
import com.javadata.ui.components.PlaceholderTextField;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class LoginDialog extends JDialog {
    private final UserService userService;
    private final Frame owner;
    private boolean authenticated = false;
    private UserProfile currentUser;
    private static final Color TRANSPARENT_BACKGROUND = new Color(0, 0, 0, 180);
    private static final Color BUTTON_COLOR = new Color(70, 120, 180);
    private static final Color TEXT_COLOR = new Color(240, 240, 240);

    public LoginDialog(Frame owner) {
        super(owner, "Login", true);
        this.owner = owner;
        this.userService = new UserService();
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setSize(1000, 500);
        setLocationRelativeTo(owner);

        // Create background panel with image
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new GridBagLayout());

        // Create main panel with semi-transparent background
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(TRANSPARENT_BACKGROUND);
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title
        JLabel titleLabel = new JLabel("JavaDataMind", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        mainPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Data Visualization Tool", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitleLabel.setForeground(TEXT_COLOR);
        mainPanel.add(subtitleLabel, gbc);

        // Add some spacing
        mainPanel.add(Box.createVerticalStrut(20), gbc);

        // Input fields with placeholders
        PlaceholderTextField usernameField = new PlaceholderTextField("Enter username");
        styleTextField(usernameField);
        PlaceholderPasswordField passwordField = new PlaceholderPasswordField("Password");
        styleTextField(passwordField);
        
        mainPanel.add(usernameField, gbc);
        mainPanel.add(passwordField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        JButton loginButton = createStyledButton("Login");
        JButton registerButton = createStyledButton("Register");

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        mainPanel.add(buttonPanel, gbc);

        // Add action listeners
        loginButton.addActionListener(e -> handleLogin(usernameField, passwordField));
        registerButton.addActionListener(e -> showRegistrationDialog());

        backgroundPanel.add(mainPanel);
        add(backgroundPanel);
    }

    private void styleTextField(JTextField field) {
        field.setBackground(new Color(255, 255, 255, 220));
        field.setCaretColor(Color.BLACK);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_COLOR.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_COLOR);
            }
        });
        
        return button;
    }

    private void handleLogin(JTextField usernameField, JPasswordField passwordField) {
        try {
            currentUser = userService.authenticateUser(
                usernameField.getText(),
                new String(passwordField.getPassword())
            );
            if (currentUser != null) {
                authenticated = true;
                dispose();
            } else {
                showError("Invalid credentials");
            }
        } catch (Exception ex) {
            showError("Login error: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, 
            message, 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }

    // Background Panel class
    private class BackgroundPanel extends JPanel {
        private BufferedImage backgroundImage;

        public BackgroundPanel() {
            setOpaque(false);
            loadBackgroundImage();
        }

        private void loadBackgroundImage() {
            try {
                URL imageUrl = new URL("https://picsum.photos/800/600");
                backgroundImage = ImageIO.read(imageUrl);
                // Apply a darker filter to the image
                darkenImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void darkenImage() {
            if (backgroundImage == null) return;
            
            for (int x = 0; x < backgroundImage.getWidth(); x++) {
                for (int y = 0; y < backgroundImage.getHeight(); y++) {
                    Color color = new Color(backgroundImage.getRGB(x, y));
                    color = new Color(
                        color.getRed() / 2,
                        color.getGreen() / 2,
                        color.getBlue() / 2
                    );
                    backgroundImage.setRGB(x, y, color.getRGB());
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    // Existing methods remain the same
    public boolean isAuthenticated() {
        return authenticated;
    }

    public UserProfile getCurrentUser() {
        return currentUser;
    }

    private void showRegistrationDialog() {
        JDialog dialog = new JDialog(this, "Register", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new GridBagLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(TRANSPARENT_BACKGROUND);
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        mainPanel.add(titleLabel, gbc);

        mainPanel.add(Box.createVerticalStrut(20), gbc);

        // Input fields with placeholders
        PlaceholderTextField nameField = new PlaceholderTextField("Enter your full name");
        PlaceholderTextField usernameField = new PlaceholderTextField("Choose a username");
        PlaceholderPasswordField passwordField = new PlaceholderPasswordField("Enter password");
        
        // Style the fields
        styleTextField(nameField);
        styleTextField(usernameField);
        styleTextField(passwordField);

        mainPanel.add(nameField, gbc);
        mainPanel.add(usernameField, gbc);
        mainPanel.add(passwordField, gbc);

        JButton registerButton = createStyledButton("Register");
        mainPanel.add(registerButton, gbc);

        registerButton.addActionListener(e -> {
            try {
                UserProfile newUser = new UserProfile(
                    nameField.getText(),
                    usernameField.getText(),
                    new String(passwordField.getPassword())
                );
                userService.createUser(newUser);
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Registration successful!");
            } catch (Exception ex) {
                showError("Registration error: " + ex.getMessage());
            }
        });

        backgroundPanel.add(mainPanel);
        dialog.add(backgroundPanel);
        dialog.setVisible(true);
    }
} 