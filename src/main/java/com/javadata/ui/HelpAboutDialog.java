package com.javadata.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.net.URL;

public class HelpAboutDialog extends JPanel {
    private static final Color DARK_BG = new Color(32, 33, 36);
    private static final Color DARKER_BG = new Color(28, 29, 32);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    
    public HelpAboutDialog() {
        setLayout(new BorderLayout());
        setBackground(DARK_BG);

        // Main content panel with all content (including logo)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(DARK_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top logo panel with larger dimensions
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setBackground(DARK_BG);
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/images/javadatamind-logo.png"));
        // Maintain aspect ratio while scaling
        int logoWidth = 400;  // Increased size
        int logoHeight = (logoWidth * logoIcon.getIconHeight()) / logoIcon.getIconWidth();
        Image scaledLogo = logoIcon.getImage().getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoPanel.add(logoLabel);
        contentPanel.add(logoPanel);
        contentPanel.add(Box.createVerticalStrut(30));

        // About text with dark theme
        JTextArea aboutText = new JTextArea();
        aboutText.setText("""
            JavaDataMind - Data Visualization Tool
            Version 1.0
            
            A powerful tool for data analysis and visualization that helps you
            understand and interpret your data with ease and precision.
            
            Features:
            • CSV data import and processing
            • Multiple interactive chart types
            • Advanced data management
            • Secure user authentication
            • Real-time data updates
            """);
        aboutText.setEditable(false);
        aboutText.setBackground(DARK_BG);
        aboutText.setForeground(TEXT_COLOR);
        aboutText.setFont(new Font("Dialog", Font.PLAIN, 14));
        contentPanel.add(aboutText);
        contentPanel.add(Box.createVerticalStrut(30));

        // Creators panel with dark theme
        JPanel creatorsPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        creatorsPanel.setBackground(DARK_BG);

        // Using picsum.photos for placeholder images
        JPanel creator1Panel = createCreatorPanel(
            "https://picsum.photos/seed/creator1/200",
            "John Smith",
            "Lead Developer",
            "Full-stack developer with expertise in data visualization"
        );

        JPanel creator2Panel = createCreatorPanel(
            "https://picsum.photos/seed/creator2/200",
            "Emma Johnson",
            "UI/UX Designer",
            "Expert in user interface design and user experience"
        );

        creatorsPanel.add(creator1Panel);
        creatorsPanel.add(creator2Panel);
        contentPanel.add(creatorsPanel);
        contentPanel.add(Box.createVerticalStrut(30));

        // Project Guide with dark theme
        JPanel guidePanel = new JPanel();
        guidePanel.setBackground(DARK_BG);
        JLabel guideLabel = new JLabel("""
            <html><div style='text-align: center; color: rgb(220, 220, 220);'>
            <p><b>Project Guide:</b></p>
            <p>Dr. Robert Wilson</p>
            <p>Professor, Department of Computer Science</p>
            </div></html>
            """);
        guidePanel.add(guideLabel);
        contentPanel.add(guidePanel);

        // Copyright with dark theme
        JLabel copyrightLabel = new JLabel("© 2024 JavaDataMind. All rights reserved.", SwingConstants.CENTER);
        copyrightLabel.setForeground(TEXT_COLOR);
        copyrightLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        contentPanel.add(copyrightLabel);

        // Add scrolling to the entire content
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(DARK_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        // Dark theme for scroll bar
        scrollPane.getVerticalScrollBar().setBackground(DARKER_BG);
        scrollPane.getViewport().setBackground(DARK_BG);
        add(scrollPane);
    }

    private JPanel createCreatorPanel(String imageUrl, String name, String role, String description) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(DARKER_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        try {
            // Load image from URL
            Image image = new ImageIcon(new URL(imageUrl)).getImage();
            Image scaledImage = image.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(createCircularImage(scaledImage)));
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(imageLabel);
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
        }

        panel.add(Box.createVerticalStrut(15));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel roleLabel = new JLabel(role);
        roleLabel.setFont(new Font("Dialog", Font.ITALIC, 14));
        roleLabel.setForeground(TEXT_COLOR);
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JTextArea descLabel = new JTextArea(description);
        descLabel.setWrapStyleWord(true);
        descLabel.setLineWrap(true);
        descLabel.setEditable(false);
        descLabel.setBackground(DARKER_BG);
        descLabel.setForeground(TEXT_COLOR);
        descLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(roleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(descLabel);

        return panel;
    }

    private BufferedImage createCircularImage(Image image) {
        int diameter = 100;
        BufferedImage circularImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = circularImage.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Ellipse2D.Double circle = new Ellipse2D.Double(0, 0, diameter, diameter);
        g2.setClip(circle);
        g2.drawImage(image, 0, 0, diameter, diameter, null);
        
        g2.dispose();
        return circularImage;
    }
}
