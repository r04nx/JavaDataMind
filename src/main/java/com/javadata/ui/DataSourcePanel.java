package com.javadata.ui;

import com.javadata.data.DatasetManager;
import com.javadata.model.Dataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;

public class DataSourcePanel extends JPanel {
    private final JTable datasetsTable;
    private final DefaultTableModel tableModel;
    private final DatasetManager datasetManager;

    public DataSourcePanel() {
        setLayout(new BorderLayout());
        datasetManager = DatasetManager.getInstance();

        // Create table model and table
        String[] columns = {"Name", "Type", "Color", "Uploaded By"};
        tableModel = new DefaultTableModel(columns, 0);
        datasetsTable = new JTable(tableModel);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addDatasetBtn = new JButton("Add Dataset");
        JButton refreshBtn = new JButton("Refresh");
        buttonPanel.add(addDatasetBtn);
        buttonPanel.add(refreshBtn);

        // Add components to panel
        add(buttonPanel, BorderLayout.NORTH);
        add(new JScrollPane(datasetsTable), BorderLayout.CENTER);

        // Add dataset button action
        addDatasetBtn.addActionListener(e -> showAddDatasetDialog());
        refreshBtn.addActionListener(e -> refreshDatasetsList());

        // Initial load
        refreshDatasetsList();
    }

    private void showAddDatasetDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Dataset", true);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField nameField = new JTextField();
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"CSV", "Database"});
        JTextField colorField = new JTextField();
        JTextField uploaderField = new JTextField();

        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Type:"));
        form.add(typeCombo);
        form.add(new JLabel("Color:"));
        form.add(colorField);
        form.add(new JLabel("Uploaded By:"));
        form.add(uploaderField);

        JButton selectFileBtn = new JButton("Select File");
        JButton saveBtn = new JButton("Save");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(selectFileBtn);
        buttonPanel.add(saveBtn);

        final File[] selectedFile = {null};

        selectFileBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                selectedFile[0] = fileChooser.getSelectedFile();
            }
        });

        saveBtn.addActionListener(e -> {
            try {
                Dataset dataset = new Dataset(
                        typeCombo.getSelectedItem().toString(),
                        nameField.getText(),
                        colorField.getText(),
                        uploaderField.getText()
                );
                datasetManager.addDataset(dataset, selectedFile[0]);
                refreshDatasetsList();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding dataset: " + ex.getMessage());
            }
        });

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void refreshDatasetsList() {
        tableModel.setRowCount(0);
        for (Dataset dataset : datasetManager.getAllDatasets()) {
            tableModel.addRow(new Object[]{
                    dataset.getName(),
                    dataset.getType(),
                    dataset.getColor(),
                    dataset.getUploadedBy()
            });
        }
    }
}