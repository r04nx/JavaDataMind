/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.r04nx.javadatamind;

/**
 *
 * @author Rohan
 */
import javax.swing.*;

public class SimpleSwingApp {

    public static void main(String[] args) {
        // Ensure GUI creation is done on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Create a JFrame
            JFrame frame = new JFrame("Simple Swing Application");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            
            // Add a JLabel
            JLabel label = new JLabel("Hello, Swing!");
            frame.getContentPane().add(label);
            
            // Make the frame visible
            frame.setVisible(true);
        });
    }
}
