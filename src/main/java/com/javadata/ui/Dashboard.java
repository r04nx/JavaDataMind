package com.javadata.ui;

import com.javadata.model.UserProfile;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.Box;

public class Dashboard extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private UserProfile currentUser;

    public Dashboard(UserProfile user) {
        this.currentUser = user;
        setTitle("JavaDataMind");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set the layout to CardLayout for dynamic panel switching
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Add panels for each section
        mainPanel.add(new DataSourcePanel(currentUser), "Datasources");
        mainPanel.add(new SettingsPanel(currentUser), "Settings");
        mainPanel.add(new VisualizerPanel(), "Visualizer");
        mainPanel.add(new HelpAboutDialog(), "Help/About");

        // Add navigation panel for user to switch between sections
        JPanel navigationPanel = new JPanel();
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.X_AXIS));
        
        // Create buttons with icons
        JButton datasourceBtn = createButtonWithIcon("Datasources", "/icons/datasource.png");
        JButton settingsBtn = createButtonWithIcon("Settings", "/icons/settings.png");
        JButton visualizerBtn = createButtonWithIcon("Visualizer", "/icons/visualizer.png");
        JButton helpBtn = createButtonWithIcon("Help/About", "/icons/help.png");
        JButton logoutBtn = createButtonWithIcon("Logout", "/icons/logout.png");

        // Action listeners for buttons
        datasourceBtn.addActionListener(e -> cardLayout.show(mainPanel, "Datasources"));
        settingsBtn.addActionListener(e -> cardLayout.show(mainPanel, "Settings"));
        visualizerBtn.addActionListener(e -> cardLayout.show(mainPanel, "Visualizer"));
        helpBtn.addActionListener(e -> cardLayout.show(mainPanel, "Help/About"));

        // Logout button action
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose(); // Close the dashboard
                // Optionally, you can show the login dialog again here if needed
                JFrame dummy = new JFrame();
                LoginDialog loginDialog = new LoginDialog(dummy);
                loginDialog.setVisible(true);
                dummy.dispose();
            }
        });

        // Add components with spacing
        navigationPanel.add(Box.createHorizontalStrut(10));
        navigationPanel.add(datasourceBtn);
        navigationPanel.add(Box.createHorizontalStrut(10));
        navigationPanel.add(settingsBtn);
        navigationPanel.add(Box.createHorizontalStrut(10));
        navigationPanel.add(visualizerBtn);
        navigationPanel.add(Box.createHorizontalStrut(10));
        navigationPanel.add(helpBtn);
        navigationPanel.add(Box.createHorizontalStrut(10));
        navigationPanel.add(logoutBtn);
        navigationPanel.add(Box.createHorizontalGlue());  // Push user label to the right
        
        JLabel userLabel = new JLabel("Logged in as: " + user.getName());
        userLabel.setForeground(Color.WHITE);
        navigationPanel.add(userLabel);
        navigationPanel.add(Box.createHorizontalStrut(10));

        // Style the navigation panel
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        navigationPanel.setBackground(new Color(60, 63, 65));

        // Style the buttons
        Component[] buttons = navigationPanel.getComponents();
        for (Component c : buttons) {
            if (c instanceof JButton) {
                JButton button = (JButton) c;
                button.setPreferredSize(new Dimension(120, 30));
                button.setFocusPainted(false);
            }
        }

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(navigationPanel, BorderLayout.NORTH);
        getContentPane().add(mainPanel, BorderLayout.CENTER);

        // Add padding to main panel
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private JButton createButtonWithIcon(String text, String iconPath) {
        JButton button = new JButton(text);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            // Resize icon to appropriate size (e.g., 16x16 pixels)
            Image img = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
            button.setIconTextGap(8); // Space between icon and text
        } catch (Exception e) {
            System.err.println("Could not load icon: " + iconPath);
        }
        button.setPreferredSize(new Dimension(120, 30));
        button.setFocusPainted(false);
        return button;
    }
}
