package com.javadata.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Dashboard extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public Dashboard() {
        setTitle("JavaDataMind");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set the layout to CardLayout for dynamic panel switching
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Add panels for each section
        mainPanel.add(new DataSourcePanel(), "Datasources");
        mainPanel.add(new SettingsPanel(), "Settings");
        mainPanel.add(new VisualizerPanel(), "Visualizer");
        mainPanel.add(new HelpAboutDialog(), "Help/About");

        // Add navigation panel for user to switch between sections
        JPanel navigationPanel = new JPanel();
        JButton datasourceBtn = new JButton("Datasources");
        JButton settingsBtn = new JButton("Settings");
        JButton visualizerBtn = new JButton("Visualizer");
        JButton helpBtn = new JButton("Help/About");

        // Action listeners for buttons
        datasourceBtn.addActionListener(e -> cardLayout.show(mainPanel, "Datasources"));
        settingsBtn.addActionListener(e -> cardLayout.show(mainPanel, "Settings"));
        visualizerBtn.addActionListener(e -> cardLayout.show(mainPanel, "Visualizer"));
        helpBtn.addActionListener(e -> cardLayout.show(mainPanel, "Help/About"));

        navigationPanel.add(datasourceBtn);
        navigationPanel.add(settingsBtn);
        navigationPanel.add(visualizerBtn);
        navigationPanel.add(helpBtn);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(navigationPanel, BorderLayout.NORTH);
        getContentPane().add(mainPanel, BorderLayout.CENTER);
    }
}
