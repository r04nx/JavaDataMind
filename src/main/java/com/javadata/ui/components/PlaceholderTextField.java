package com.javadata.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class PlaceholderTextField extends JTextField {
    private String placeholder;
    private boolean showingPlaceholder;
    private Color placeholderColor = new Color(150, 150, 150);
    private Color textColor = Color.BLACK;

    public PlaceholderTextField(String placeholder) {
        this.placeholder = placeholder;
        this.showingPlaceholder = true;
        
        setForeground(placeholderColor);
        setText(placeholder);
        
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (showingPlaceholder) {
                    setText("");
                    setForeground(textColor);
                    showingPlaceholder = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getText().isEmpty()) {
                    setText(placeholder);
                    setForeground(placeholderColor);
                    showingPlaceholder = true;
                }
            }
        });
    }

    @Override
    public String getText() {
        return showingPlaceholder ? "" : super.getText();
    }
} 