package com.javadata.ui;

import com.javadata.data.DatabaseManager;
import com.javadata.model.UserProfile;
import com.javadata.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class SettingsPanel extends JPanel {
    private final UserService userService;
    private final UserProfile currentUser;
    private final Color BACKGROUND_COLOR = new Color(45, 45, 45);
    private final Color FOREGROUND_COLOR = new Color(200, 200, 200);

    public SettingsPanel(UserProfile user) {
        this.currentUser = user;
        this.userService = new UserService();
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title Panel
        JLabel titleLabel = new JLabel("User Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(FOREGROUND_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Create styled components
        JTextField nameField = createStyledTextField(currentUser.getName());
        JTextField usernameField = createStyledTextField(currentUser.getUsername());
        JPasswordField currentPasswordField = createStyledPasswordField();
        JPasswordField newPasswordField = createStyledPasswordField();
        JButton saveButton = createStyledButton("Save Changes");
        
        // Add components to form
        addFormRow(formPanel, "Name:", nameField, gbc, 0);
        addFormRow(formPanel, "Username:", usernameField, gbc, 1);
        addFormRow(formPanel, "Current Password:", currentPasswordField, gbc, 2);
        addFormRow(formPanel, "New Password:", newPasswordField, gbc, 3);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(saveButton);

        // Add action listener
        saveButton.addActionListener(e -> {
            try {
                if (userService.authenticateUser(currentUser.getUsername(), 
                    new String(currentPasswordField.getPassword())) != null) {
                    
                    currentUser.setName(nameField.getText());
                    currentUser.setPassword(new String(newPasswordField.getPassword()));
                    
                    updateUserInDatabase(currentUser);
                    JOptionPane.showMessageDialog(this, 
                        "Settings updated successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Current password is incorrect!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error updating settings: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // Main panel assembly
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JTextField createStyledTextField(String text) {
        JTextField field = new JTextField(text, 20);
        field.setBackground(new Color(60, 63, 65));
        field.setForeground(FOREGROUND_COLOR);
        field.setCaretColor(FOREGROUND_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setBackground(new Color(60, 63, 65));
        field.setForeground(FOREGROUND_COLOR);
        field.setCaretColor(FOREGROUND_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return field;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(70, 120, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return button;
    }

    private void addFormRow(JPanel panel, String labelText, JComponent component, 
                          GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(labelText);
        label.setForeground(FOREGROUND_COLOR);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.1;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.9;
        panel.add(component, gbc);
    }

    private void updateUserInDatabase(UserProfile user) throws SQLException {
        String sql = "UPDATE users SET name = ?, password = ? WHERE username = ?";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getUsername());
            pstmt.executeUpdate();
        }
    }
}
