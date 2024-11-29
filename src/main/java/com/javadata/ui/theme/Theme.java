package com.javadata.ui.theme;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class Theme {
    // Colors for buttons only
    public static final Color PRIMARY_COLOR = new Color(60, 141, 188);    // Blue
    public static final Color SECONDARY_COLOR = new Color(40, 96, 144);   // Dark Blue
    public static final Color ACCENT_COLOR = new Color(255, 152, 0);      // Orange
    public static final Color SUCCESS_COLOR = new Color(40, 167, 69);     // Green
    public static final Color NEUTRAL_COLOR = new Color(108, 117, 125);   // Gray
    
    public static void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }
} 