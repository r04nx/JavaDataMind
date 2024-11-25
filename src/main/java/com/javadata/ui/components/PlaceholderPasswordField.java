package com.javadata.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class PlaceholderPasswordField extends JPasswordField {
    private String placeholder;
    private boolean showingPlaceholder;
    private Color placeholderColor = new Color(150, 150, 150);
    private Color textColor = Color.BLACK;

    public PlaceholderPasswordField(String placeholder) {
        this.placeholder = placeholder;
        this.showingPlaceholder = true;
        
        setForeground(placeholderColor);
        setEchoChar((char) 0);
        setText(placeholder);
        
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (showingPlaceholder) {
                    setText("");
                    setEchoChar('•');
                    setForeground(textColor);
                    showingPlaceholder = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getPassword().length == 0) {
                    setEchoChar((char) 0);
                    setText(placeholder);
                    setForeground(placeholderColor);
                    showingPlaceholder = true;
                }
            }
        });
    }

    @Override
    public char[] getPassword() {
        return showingPlaceholder ? new char[0] : super.getPassword();
    }
} 