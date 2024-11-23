package com.javadata.ui;

import com.javadata.data.DatasetManager;
import com.javadata.model.Dataset;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class VisualizerPanel extends JPanel {
    private final JComboBox<String> datasetComboBox;
    private final JComboBox<String> visualizationTypeComboBox;
    private final DatasetManager datasetManager;
    private final JList<String> columnList;
    private final JButton refreshButton;
    private final JButton generateButton;
    private final JButton clearButton;
    private final JButton exportButton;
    private JTextField startDateField;
    private JTextField endDateField;
    private String xAxisColumn;

    public VisualizerPanel() {
        setLayout(new BorderLayout());
        datasetManager = DatasetManager.getInstance();

        // Initialize components
        datasetComboBox = new JComboBox<>();
        visualizationTypeComboBox = new JComboBox<>(new String[]{
            "Bar Chart", "Line Chart", "Pie Chart", "Area Chart", "Scatter Plot", "Histogram",
            "Stacked Bar Chart", "Grouped Bar Chart", "Time Series"
        });

        // Initialize buttons
        generateButton = new JButton("Generate Visualization");
        clearButton = new JButton("Clear Selections");
        exportButton = new JButton("Export Data");
        refreshButton = new JButton("Refresh Data");

        columnList = new JList<>();
        columnList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Initialize date fields
        startDateField = new JTextField(10);
        endDateField = new JTextField(10);
        startDateField.setToolTipText("Enter start date (YYYY-MM-DD)");
        endDateField.setToolTipText("Enter end date (YYYY-MM-DD)");

        // Add components to control panel
        JPanel controlsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Layout setup
        addControlComponents(controlsPanel, gbc);
        add(controlsPanel, BorderLayout.NORTH);

        // Load initial datasets
        loadDatasets();

        // Action listeners
        generateButton.addActionListener(e -> {
            if (validateInputs()) {
                List<String> selectedColumns = columnList.getSelectedValuesList();
                visualizeColumns(selectedColumns);
            }
        });

        clearButton.addActionListener(e -> clearSelections());
        exportButton.addActionListener(e -> exportCurrentView());
        refreshButton.addActionListener(e -> refreshData());

        // Add dataset change listener
        datasetComboBox.addActionListener(e -> {
            loadColumns();
            updateDateFieldsForDataset();
        });
    }

    private void addControlComponents(JPanel controlsPanel, GridBagConstraints gbc) {
        // Add components to the controls panel
        gbc.gridx = 0; gbc.gridy = 0;
        controlsPanel.add(new JLabel("Dataset:"), gbc);
        gbc.gridx = 1;
        controlsPanel.add(datasetComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        controlsPanel.add(new JLabel("Visualization:"), gbc);
        gbc.gridx = 1;
        controlsPanel.add(visualizationTypeComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        controlsPanel.add(new JLabel("Date Range:"), gbc);
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.add(new JLabel("From:"));
        datePanel.add(startDateField);
        datePanel.add(new JLabel("To:"));
        datePanel.add(endDateField);
        gbc.gridx = 1;
        controlsPanel.add(datePanel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        JScrollPane columnScroll = new JScrollPane(columnList);
        columnScroll.setPreferredSize(new Dimension(300, 150));
        controlsPanel.add(columnScroll, gbc);

        gbc.gridy = 4;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(generateButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(refreshButton);
        controlsPanel.add(buttonPanel, gbc);
    }

    private void loadDatasets() {
        List<Dataset> datasets = datasetManager.getAllDatasets();
        datasetComboBox.removeAllItems();
        for (Dataset dataset : datasets) {
            datasetComboBox.addItem(dataset.getName());
        }
    }

    private void visualizeColumns(List<String> selectedColumns) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<String[]> data = datasetManager.getDatasetContent((String) datasetComboBox.getSelectedItem());

        // Determine the X-axis column
        xAxisColumn = (String) JOptionPane.showInputDialog(this, "Select X-axis column:", "X-axis Selection",
                JOptionPane.QUESTION_MESSAGE, null, selectedColumns.toArray(), selectedColumns.get(0));

        if (xAxisColumn == null) {
            return; // User canceled the selection
        }

        for (String column : selectedColumns) {
            if (column.equals(xAxisColumn)) continue; // Skip the X-axis column
            int columnIndex = datasetManager.selectedColumnIndex(column, (String) datasetComboBox.getSelectedItem());
            for (int i = 1; i < data.size(); i++) { // Skip header
                String[] row = data.get(i);
                if (isNumeric(row[columnIndex])) {
                    dataset.addValue(Double.parseDouble(row[columnIndex]), column, row[getColumnIndex(xAxisColumn, data)]);
                }
            }
        }

        // Create and display the chart in a new window
        JFreeChart chart = createChart(dataset);
        displayChartInNewWindow(chart);
    }

    private JFreeChart createChart(DefaultCategoryDataset dataset) {
        String chartType = (String) visualizationTypeComboBox.getSelectedItem();
        PlotOrientation orientation = PlotOrientation.VERTICAL; // Default orientation

        switch (chartType) {
            case "Bar Chart":
                return ChartFactory.createBarChart("Visualization", xAxisColumn, "Value", dataset, orientation, true, true, false);
            case "Line Chart":
                return ChartFactory.createLineChart("Visualization", xAxisColumn, "Value", dataset, orientation, true, true, false);
            case "Pie Chart":
                // Implement pie chart logic if needed
                break;
            case "Area Chart":
                // Implement area chart logic if needed
                break;
            case "Scatter Plot":
                // Implement scatter plot logic if needed
                break;
            case "Histogram":
                // Implement histogram logic if needed
                break;
            case "Stacked Bar Chart":
                // Implement stacked bar chart logic if needed
                break;
            case "Grouped Bar Chart":
                // Implement grouped bar chart logic if needed
                break;
            case "Time Series":
                // Implement time series logic if needed
                break;
        }
        return null; // Return null if no chart is created
    }

    private void displayChartInNewWindow(JFreeChart chart) {
        JFrame chartFrame = new JFrame("Chart Visualization");
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.setSize(800, 600);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartFrame.add(chartPanel, BorderLayout.CENTER);
        chartFrame.setVisible(true);
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private int getColumnIndex(String columnName, List<String[]> data) {
        String[] headers = data.get(0);
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals(columnName)) {
                return i;
            }
        }
        return -1; // Not found
    }

    private void clearSelections() {
        datasetComboBox.setSelectedIndex(-1);
        columnList.clearSelection();
        startDateField.setText("");
        endDateField.setText("");
    }

    private void loadColumns() {
        String datasetName = (String) datasetComboBox.getSelectedItem();
        List<String> columns = datasetManager.getDatasetColumns(datasetName);
        columnList.setListData(columns.toArray(new String[0]));

        // Check if any selected column is a date column
        boolean hasDateColumn = columns.stream().anyMatch(column -> datasetManager.isDateOrTimestampColumn(datasetName, column));
        startDateField.setVisible(hasDateColumn);
        endDateField.setVisible(hasDateColumn);
    }

    private void updateDateFieldsForDataset() {
        // Logic to update date fields based on the selected dataset
    }

    private boolean validateInputs() {
        if (datasetComboBox.getSelectedItem() == null) {
            showError("Please select a dataset");
            return false;
        }
        if (columnList.getSelectedIndices().length == 0) {
            showError("Please select at least one column");
            return false;
        }
        if (!validateDateRange()) {
            return false;
        }
        return true;
    }

    private boolean validateDateRange() {
        if (!startDateField.getText().isEmpty() || !endDateField.getText().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                Date startDate = sdf.parse(startDateField.getText());
                Date endDate = sdf.parse(endDateField.getText());
                if (startDate.after(endDate)) {
                    showError("Start date must be before end date");
                    return false;
                }
            } catch (ParseException e) {
                showError("Invalid date format. Use YYYY-MM-DD");
                return false;
            }
        }
        return true;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void exportCurrentView() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Data");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getPath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }
            exportToCSV(datasetManager.getDatasetContent(
                (String) datasetComboBox.getSelectedItem()), filePath);
        }
    }

    private void refreshData() {
        loadDatasets();
    }

    private void exportToCSV(List<String[]> data, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String[] row : data) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
        } catch (IOException e) {
            showError("Error exporting data: " + e.getMessage());
        }
    }
}