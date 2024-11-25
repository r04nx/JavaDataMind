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
import org.jfree.data.time.*;
import org.jfree.data.xy.*;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

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
        String datasetName = (String) datasetComboBox.getSelectedItem();
        List<String[]> data = datasetManager.getDatasetContent(datasetName);
        
        // First, determine column types
        Map<String, String> columnTypes = new HashMap<>();
        for (String column : selectedColumns) {
            columnTypes.put(column, determineColumnType(data, column));
        }
        
        // Let user select X-axis column with type information
        String[] options = selectedColumns.stream()
            .map(col -> col + " (" + columnTypes.get(col) + ")")
            .toArray(String[]::new);
        
        String selection = (String) JOptionPane.showInputDialog(
            this,
            "Select X-axis column:",
            "X-axis Selection",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (selection == null) return;
        
        xAxisColumn = selection.substring(0, selection.indexOf(" ("));
        String xAxisType = columnTypes.get(xAxisColumn);
        
        // Remove x-axis column from available Y-axis columns
        List<String> yAxisColumns = selectedColumns.stream()
            .filter(col -> !col.equals(xAxisColumn))
            .collect(Collectors.toList());
        
        if (yAxisColumns.isEmpty()) {
            showError("Please select at least one column for Y-axis");
            return;
        }

        // Create appropriate dataset based on X-axis type
        switch (xAxisType) {
            case "Date/Time" -> createTimeSeriesChart(data, xAxisColumn, yAxisColumns);
            case "Numeric" -> createNumericChart(data, xAxisColumn, yAxisColumns);
            case "Text" -> createCategoryChart(data, xAxisColumn, yAxisColumns);
            default -> showError("Unsupported X-axis type");
        }
    }

    private String determineColumnType(List<String[]> data, String columnName) {
        int colIndex = getColumnIndex(columnName, data);
        if (colIndex == -1) return "Unknown";
        
        // Check first few non-header rows
        for (int i = 1; i < Math.min(data.size(), 10); i++) {
            String value = data.get(i)[colIndex].trim();
            if (value.isEmpty()) continue;
            
            // Try parsing as date
            if (isDateValue(value)) return "Date/Time";
            
            // Try parsing as number
            if (isNumeric(value)) return "Numeric";
        }
        
        return "Text";
    }

    private boolean isDateValue(String value) {
        String[] datePatterns = {
            "yyyy-MM-dd", "MM/dd/yyyy", "dd-MM-yyyy",
            "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy HH:mm:ss"
        };
        
        for (String pattern : datePatterns) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                sdf.setLenient(false);
                sdf.parse(value);
                return true;
            } catch (ParseException ignored) {}
        }
        return false;
    }

    private void createTimeSeriesChart(List<String[]> data, String xColumn, List<String> yColumns) {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        int xColIndex = getColumnIndex(xColumn, data);
        
        for (String yColumn : yColumns) {
            TimeSeries series = new TimeSeries(yColumn);
            int yColIndex = getColumnIndex(yColumn, data);
            
            for (int i = 1; i < data.size(); i++) {
                String[] row = data.get(i);
                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(row[xColIndex]);
                    double value = Double.parseDouble(row[yColIndex]);
                    series.add(new Day(date), value);
                } catch (ParseException | NumberFormatException ignored) {}
            }
            dataset.addSeries(series);
        }
        
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "Time Series Analysis",
            xColumn,
            "Values",
            dataset,
            true,
            true,
            false
        );
        
        displayChartInNewWindow(chart);
    }

    private void createNumericChart(List<String[]> data, String xColumn, List<String> yColumns) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        int xColIndex = getColumnIndex(xColumn, data);
        
        for (String yColumn : yColumns) {
            XYSeries series = new XYSeries(yColumn);
            int yColIndex = getColumnIndex(yColumn, data);
            
            for (int i = 1; i < data.size(); i++) {
                String[] row = data.get(i);
                try {
                    double x = Double.parseDouble(row[xColIndex]);
                    double y = Double.parseDouble(row[yColIndex]);
                    series.add(x, y);
                } catch (NumberFormatException ignored) {}
            }
            dataset.addSeries(series);
        }
        
        String chartType = (String) visualizationTypeComboBox.getSelectedItem();
        JFreeChart chart = switch (chartType) {
            case "Scatter Plot" -> ChartFactory.createScatterPlot(
                "Scatter Plot", xColumn, "Values", dataset
            );
            default -> ChartFactory.createXYLineChart(
                "Numeric Analysis", xColumn, "Values", dataset
            );
        };
        
        displayChartInNewWindow(chart);
    }

    private void createCategoryChart(List<String[]> data, String xColumn, List<String> yColumns) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int xColIndex = getColumnIndex(xColumn, data);
        
        for (String yColumn : yColumns) {
            int yColIndex = getColumnIndex(yColumn, data);
            
            // Group data by categories
            Map<String, List<Double>> categoryValues = new HashMap<>();
            for (int i = 1; i < data.size(); i++) {
                String[] row = data.get(i);
                String category = row[xColIndex];
                try {
                    double value = Double.parseDouble(row[yColIndex]);
                    categoryValues.computeIfAbsent(category, k -> new ArrayList<>()).add(value);
                } catch (NumberFormatException ignored) {}
            }
            
            // Calculate averages for each category
            for (Map.Entry<String, List<Double>> entry : categoryValues.entrySet()) {
                double average = entry.getValue().stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
                dataset.addValue(average, yColumn, entry.getKey());
            }
        }
        
        String chartType = (String) visualizationTypeComboBox.getSelectedItem();
        JFreeChart chart = switch (chartType) {
            case "Bar Chart" -> ChartFactory.createBarChart(
                "Category Analysis", xColumn, "Values", dataset,
                PlotOrientation.VERTICAL, true, true, false
            );
            case "Line Chart" -> ChartFactory.createLineChart(
                "Category Analysis", xColumn, "Values", dataset,
                PlotOrientation.VERTICAL, true, true, false
            );
            case "Area Chart" -> ChartFactory.createAreaChart(
                "Category Analysis", xColumn, "Values", dataset,
                PlotOrientation.VERTICAL, true, true, false
            );
            case "Stacked Bar Chart" -> ChartFactory.createStackedBarChart(
                "Category Analysis", xColumn, "Values", dataset,
                PlotOrientation.VERTICAL, true, true, false
            );
            default -> ChartFactory.createBarChart(
                "Category Analysis", xColumn, "Values", dataset,
                PlotOrientation.VERTICAL, true, true, false
            );
        };
        
        displayChartInNewWindow(chart);
    }

    private JFreeChart createChart(DefaultCategoryDataset dataset) {
        String chartType = (String) visualizationTypeComboBox.getSelectedItem();
        String title = "Data Visualization";
        PlotOrientation orientation = PlotOrientation.VERTICAL;

        return switch (chartType) {
            case "Bar Chart" -> ChartFactory.createBarChart(title, xAxisColumn, "Value", dataset, orientation, true, true, false);
            case "Line Chart" -> ChartFactory.createLineChart(title, xAxisColumn, "Value", dataset, orientation, true, true, false);
            case "Area Chart" -> ChartFactory.createAreaChart(title, xAxisColumn, "Value", dataset, orientation, true, true, false);
            case "Stacked Bar Chart" -> ChartFactory.createStackedBarChart(title, xAxisColumn, "Value", dataset, orientation, true, true, false);
            default -> ChartFactory.createBarChart(title, xAxisColumn, "Value", dataset, orientation, true, true, false);
        };
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