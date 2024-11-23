package com.javadata.ui;

import com.javadata.model.UserProfile;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        JButton datasourceBtn = new JButton("Datasources");
        JButton settingsBtn = new JButton("Settings");
        JButton visualizerBtn = new JButton("Visualizer");
        JButton helpBtn = new JButton("Help/About");
        JButton logoutBtn = new JButton("Logout");

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

        navigationPanel.add(datasourceBtn);
        navigationPanel.add(settingsBtn);
        navigationPanel.add(visualizerBtn);
        navigationPanel.add(helpBtn);
        navigationPanel.add(logoutBtn); // Add the logout button

        JLabel userLabel = new JLabel("Logged in as: " + user.getName());
        userLabel.setForeground(Color.WHITE);
        navigationPanel.add(userLabel);

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
}
