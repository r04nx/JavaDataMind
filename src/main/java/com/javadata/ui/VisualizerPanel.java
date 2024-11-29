package com.javadata.ui;

import com.javadata.data.DatasetManager;
import com.javadata.model.Dataset;
import com.javadata.ui.components.ColumnInfoPanel;
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
import org.jfree.data.general.DefaultPieDataset;
import com.javadata.analysis.TrendAnalyzer;
import com.javadata.util.DataTypeDetector;
import javax.swing.table.DefaultTableModel;
import com.javadata.util.DataTypeDetector.DataType;
import javax.swing.border.*;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import com.javadata.ui.theme.Theme;

public class VisualizerPanel extends JPanel {
    private static final Color PRIMARY_COLOR = new Color(60, 141, 188);
    private static final Color SECONDARY_COLOR = new Color(40, 96, 144);
    private static final Color ACCENT_COLOR = new Color(255, 152, 0);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 12);

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
    private ColumnInfoPanel columnInfoPanel;
    private JButton analyzeButton;
    private JPanel chartPanel;
    private final DataTypeDetector dataTypeDetector;

    public VisualizerPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
//        setBackground(Color.WHITE);
        
        datasetManager = DatasetManager.getInstance();
        dataTypeDetector = new DataTypeDetector();

        // Initialize all components first
        datasetComboBox = new JComboBox<>();
        visualizationTypeComboBox = new JComboBox<>(new String[]{
            "Bar Chart", "Line Chart", "Pie Chart", "Area Chart", "Scatter Plot", "Histogram",
            "Stacked Bar Chart", "Grouped Bar Chart", "Time Series"
        });
        
        columnList = new JList<>();
        columnList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        startDateField = new JTextField(10);
        endDateField = new JTextField(10);
        startDateField.setToolTipText("Enter start date (YYYY-MM-DD)");
        endDateField.setToolTipText("Enter end date (YYYY-MM-DD)");
        
        generateButton = new JButton("Generate Visualization");
        clearButton = new JButton("Clear Selections");
        exportButton = new JButton("Export Data");
        refreshButton = new JButton("Refresh Data");
        analyzeButton = new JButton("Analyze Trends");
        
        chartPanel = new JPanel(new BorderLayout());

        // Add components to control panel
        JPanel controlsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Now add the components
        addControlComponents(controlsPanel, gbc);
        add(controlsPanel, BorderLayout.NORTH);
        add(chartPanel, BorderLayout.CENTER);

        // Initialize column info panel
        columnInfoPanel = new ColumnInfoPanel();
        add(columnInfoPanel, BorderLayout.EAST);

        // Add listeners
        setupListeners();
        
        // Load initial data
        loadDatasets();

        // Style the components
        styleComponents();
    }

    private void styleComponents() {
        // Style buttons only
        Theme.styleButton(generateButton, Theme.PRIMARY_COLOR, Color.WHITE);
        Theme.styleButton(analyzeButton, Theme.SECONDARY_COLOR, Color.WHITE);
        Theme.styleButton(clearButton, Theme.NEUTRAL_COLOR, Color.WHITE);
        Theme.styleButton(exportButton, Theme.ACCENT_COLOR, Color.WHITE);
        Theme.styleButton(refreshButton, Theme.SUCCESS_COLOR, Color.WHITE);

        // Keep original simple styling for other components
        columnList.setFont(new Font("Segoe UI", Font.PLAIN, 12));
//        columnList.setBackground(Color.WHITE);
        columnList.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    }

    private void addControlComponents(JPanel controlsPanel, GridBagConstraints gbc) {
        // Initialize components if they haven't been initialized
        if (datasetComboBox == null || visualizationTypeComboBox == null || 
            startDateField == null || endDateField == null || columnList == null) {
            return;
        }

        // Add components to the controls panel with null checks
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel datasetLabel = new JLabel("Dataset:");
        controlsPanel.add(datasetLabel, gbc);
        
        gbc.gridx = 1;
        if (datasetComboBox != null) {
            controlsPanel.add(datasetComboBox, gbc);
        }

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel visualizationLabel = new JLabel("Visualization:");
        controlsPanel.add(visualizationLabel, gbc);
        
        gbc.gridx = 1;
        if (visualizationTypeComboBox != null) {
            controlsPanel.add(visualizationTypeComboBox, gbc);
        }

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel dateRangeLabel = new JLabel("Date Range:");
        controlsPanel.add(dateRangeLabel, gbc);
        
        if (startDateField != null && endDateField != null) {
            JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            datePanel.add(new JLabel("From:"));
            datePanel.add(startDateField);
            datePanel.add(new JLabel("To:"));
            datePanel.add(endDateField);
            gbc.gridx = 1;
            controlsPanel.add(datePanel, gbc);
        }

        if (columnList != null) {
            gbc.gridx = 0; gbc.gridy = 3;
            gbc.gridwidth = 2;
            JScrollPane columnScroll = new JScrollPane(columnList);
            columnScroll.setPreferredSize(new Dimension(300, 150));
            controlsPanel.add(columnScroll, gbc);
        }

        // Add buttons panel
        gbc.gridy = 4;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        if (generateButton != null) buttonPanel.add(generateButton);
        if (analyzeButton != null) buttonPanel.add(analyzeButton);
        if (clearButton != null) buttonPanel.add(clearButton);
        if (exportButton != null) buttonPanel.add(exportButton);
        if (refreshButton != null) buttonPanel.add(refreshButton);
        
        controlsPanel.add(buttonPanel, gbc);

        // Style the labels
        Arrays.asList(datasetLabel, visualizationLabel, dateRangeLabel).forEach(label -> {
            label.setFont(TITLE_FONT);
            label.setForeground(PRIMARY_COLOR);
        });

        // Add rounded corners and padding to the controls panel
        controlsPanel.setBorder(new CompoundBorder(
            new EmptyBorder(10, 10, 10, 10),
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1, true)
        ));
//        controlsPanel.setBackground(Color.WHITE);
    }

    private void setupListeners() {
        generateButton.addActionListener(e -> {
            if (validateInputs()) {
                List<String> selectedColumns = columnList.getSelectedValuesList();
                visualizeColumns(selectedColumns);
            }
        });

        clearButton.addActionListener(e -> clearSelections());
        exportButton.addActionListener(e -> exportCurrentView());
        refreshButton.addActionListener(e -> refreshData());
        analyzeButton.addActionListener(e -> analyzeTrends());

        datasetComboBox.addActionListener(e -> {
            loadColumns();
            updateDateFieldsForDataset();
            updateColumnInfo();
        });
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
        
        String chartType = (String) visualizationTypeComboBox.getSelectedItem();
        
        // For pie chart, we only need one numeric column
        if (chartType.equals("Pie Chart")) {
            handlePieChart(data, selectedColumns, columnTypes);
            return;
        }
        
        // For other charts, proceed with X and Y axis selection
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
        
        List<String> yAxisColumns = selectedColumns.stream()
            .filter(col -> !col.equals(xAxisColumn))
            .collect(Collectors.toList());
        
        if (yAxisColumns.isEmpty()) {
            showError("Please select at least one column for Y-axis");
            return;
        }

        // Create appropriate chart based on type and data
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
        String chartType = (String) visualizationTypeComboBox.getSelectedItem();
        
        // For line charts, we should preserve individual data points
        boolean isLineChart = chartType.equals("Line Chart");
        
        for (String yColumn : yColumns) {
            int yColIndex = getColumnIndex(yColumn, data);
            
            if (isLineChart) {
                // For line charts, maintain the original order and individual points
                for (int i = 1; i < data.size(); i++) {
                    String[] row = data.get(i);
                    try {
                        String category = row[xColIndex];
                        double value = Double.parseDouble(row[yColIndex]);
                        dataset.addValue(value, yColumn, category);
                    } catch (NumberFormatException ignored) {}
                }
            } else {
                // For bar charts and others, aggregate the data
                Map<String, List<Double>> categoryValues = new HashMap<>();
                for (int i = 1; i < data.size(); i++) {
                    String[] row = data.get(i);
                    String category = row[xColIndex];
                    try {
                        double value = Double.parseDouble(row[yColIndex]);
                        categoryValues.computeIfAbsent(category, k -> new ArrayList<>()).add(value);
                    } catch (NumberFormatException ignored) {}
                }
                
                // Calculate aggregates (sum for bar charts)
                for (Map.Entry<String, List<Double>> entry : categoryValues.entrySet()) {
                    double sum = entry.getValue().stream()
                        .mapToDouble(Double::doubleValue)
                        .sum();
                    dataset.addValue(sum, yColumn, entry.getKey());
                }
            }
        }
        
        JFreeChart chart = switch (chartType) {
            case "Bar Chart" -> ChartFactory.createBarChart(
                "Category Analysis",
                xColumn,
                "Total Values",  // Changed to reflect that bars show totals
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
            );
            case "Line Chart" -> {
                JFreeChart lineChart = ChartFactory.createLineChart(
                    "Trend Analysis",  // Changed to better reflect line chart purpose
                    xColumn,
                    "Values",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true, true, false
                );
                // Customize line chart for better visibility
                lineChart.getCategoryPlot().setDomainGridlinesVisible(true);
                lineChart.getCategoryPlot().setRangeGridlinesVisible(true);
                yield lineChart;
            }
            // ... other cases remain the same ...
            default -> ChartFactory.createBarChart(
                "Category Analysis",
                xColumn,
                "Total Values",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
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

    private void handlePieChart(List<String[]> data, List<String> selectedColumns, Map<String, String> columnTypes) {
        // Filter numeric columns for pie chart values
        List<String> numericColumns = selectedColumns.stream()
            .filter(col -> columnTypes.get(col).equals("Numeric"))
            .collect(Collectors.toList());
        
        if (numericColumns.isEmpty()) {
            showError("Please select at least one numeric column for pie chart");
            return;
        }
        
        // Let user select the numeric column for values
        String valueColumn = (String) JOptionPane.showInputDialog(
            this,
            "Select column for values:",
            "Value Column Selection",
            JOptionPane.QUESTION_MESSAGE,
            null,
            numericColumns.toArray(),
            numericColumns.get(0)
        );
        
        if (valueColumn == null) return;
        
        // Let user select the category column (non-numeric)
        List<String> categoryColumns = selectedColumns.stream()
            .filter(col -> !numericColumns.contains(col))
            .collect(Collectors.toList());
        
        if (categoryColumns.isEmpty()) {
            showError("Please select a categorical column for pie chart labels");
            return;
        }
        
        String categoryColumn = (String) JOptionPane.showInputDialog(
            this,
            "Select column for categories:",
            "Category Column Selection",
            JOptionPane.QUESTION_MESSAGE,
            null,
            categoryColumns.toArray(),
            categoryColumns.get(0)
        );
        
        if (categoryColumn == null) return;
        
        createPieChart(data, categoryColumn, valueColumn);
    }

    private void createPieChart(List<String[]> data, String categoryColumn, String valueColumn) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        int catIndex = getColumnIndex(categoryColumn, data);
        int valIndex = getColumnIndex(valueColumn, data);
        
        // Group and sum values by category
        Map<String, Double> categoryTotals = new HashMap<>();
        for (int i = 1; i < data.size(); i++) {
            String[] row = data.get(i);
            String category = row[catIndex];
            try {
                double value = Double.parseDouble(row[valIndex]);
                categoryTotals.merge(category, value, Double::sum);
            } catch (NumberFormatException ignored) {}
        }
        
        // Add data to dataset
        categoryTotals.forEach((category, value) -> dataset.setValue(category, value));
        
        JFreeChart chart = ChartFactory.createPieChart(
            "Distribution of " + valueColumn + " by " + categoryColumn,
            dataset,
            true,  // legend
            true,  // tooltips
            false  // urls
        );
        
        // Customize the chart appearance
        chart.setBackgroundPaint(Color.white);
        chart.getPlot().setBackgroundPaint(Color.white);
        
        displayChartInNewWindow(chart);
    }

    private void initializeColumnInfo() {
        columnInfoPanel = new ColumnInfoPanel();
        add(columnInfoPanel, BorderLayout.EAST);
    }

    private void updateDatasetVisuals() {
        String datasetName = (String) datasetComboBox.getSelectedItem();
        if (datasetName == null) return;
        
        Dataset dataset = datasetManager.getDataset(datasetName);
        List<String[]> data = datasetManager.getDatasetContent(datasetName);
        
        // Update column info panel
        columnInfoPanel.updateColumnInfo(data, Collections.singletonMap(
            datasetName, Color.decode(dataset.getColor())
        ));
        
        // Update dataset color indicator
        datasetComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                panel.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
                
                // Color indicator
                JPanel colorBox = new JPanel();
                colorBox.setPreferredSize(new Dimension(16, 16));
                colorBox.setBackground(Color.decode(dataset.getColor()));
                panel.add(colorBox);
                
                // Dataset name
                JLabel label = new JLabel(value.toString());
                label.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
                panel.add(label);
                
                return panel;
            }
        });
    }

    private void displayTrendAnalysis(List<String[]> data, String column) {
        Map<String, Double> trends = TrendAnalyzer.calculateTrends(data, column);
        if (trends.isEmpty()) {
            showError("No numeric data available for trend analysis");
            return;
        }
        
        StringBuilder report = new StringBuilder();
        report.append("Trend Analysis for ").append(column).append("\n\n");
        report.append(String.format("Mean: %.2f\n", trends.get("mean")));
        report.append(String.format("Median: %.2f\n", trends.get("median")));
        report.append(String.format("Range: %.2f\n", trends.get("range")));
        report.append(String.format("Q1: %.2f\n", trends.get("q1")));
        report.append(String.format("Q3: %.2f\n", trends.get("q3")));
        report.append(String.format("Trend Direction: %s\n", 
            trends.get("trend_direction") > 0 ? "Upward" : "Downward"));
        report.append(String.format("Slope: %.4f\n", trends.get("slope")));
        report.append(String.format("Variance: %.2f\n", trends.get("variance")));
        
        JTextArea textArea = new JTextArea(report.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Trend Analysis", JOptionPane.INFORMATION_MESSAGE);
    }

    private void analyzeTrends() {
        List<String> selectedColumns = columnList.getSelectedValuesList();
        if (selectedColumns.isEmpty()) {
            showError("Please select columns to analyze");
            return;
        }

        String datasetName = (String) datasetComboBox.getSelectedItem();
        List<String[]> data = datasetManager.getDatasetContent(datasetName);

        // Create a tabbed pane for multiple analyses
        JTabbedPane tabbedPane = new JTabbedPane();

        for (String column : selectedColumns) {
            List<String> columnValues = getColumnValues(data, column);
            DataType type = DataTypeDetector.detectColumnType(columnValues);
            
            if (type == DataType.NUMERIC_INTEGER || type == DataType.NUMERIC_DECIMAL) {
                Map<String, Double> trends = TrendAnalyzer.calculateTrends(data, column);
                JPanel analysisPanel = createTrendAnalysisPanel(column, trends);
                tabbedPane.addTab(column, analysisPanel);
            }
        }

        if (tabbedPane.getTabCount() > 0) {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                "Trend Analysis", true);
            dialog.setContentPane(tabbedPane);
            dialog.setSize(600, 400);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        } else {
            showError("No numeric columns selected for analysis");
        }
    }

    private JPanel createTrendAnalysisPanel(String column, Map<String, Double> trends) {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create statistics table
        String[][] data = {
            {"Mean", String.format("%.2f", trends.get("mean"))},
            {"Median", String.format("%.2f", trends.get("median"))},
            {"Range", String.format("%.2f", trends.get("range"))},
            {"Q1", String.format("%.2f", trends.get("q1"))},
            {"Q3", String.format("%.2f", trends.get("q3"))},
            {"Trend Direction", trends.get("trend_direction") > 0 ? "Upward" : "Downward"},
            {"Slope", String.format("%.4f", trends.get("slope"))},
            {"Variance", String.format("%.2f", trends.get("variance"))}
        };
        
        // Create a table model
        DefaultTableModel tableModel = new DefaultTableModel(data, new String[]{"Statistic", "Value"});
        
        // Create a JTable with the table model
        JTable table = new JTable(tableModel);
        
        // Add the table to the panel
        panel.add(table.getTableHeader(), BorderLayout.NORTH);
        panel.add(table, BorderLayout.CENTER);
        
        return panel;
    }

    private List<String> getColumnValues(List<String[]> data, String column) {
        int colIndex = getColumnIndex(column, data);
        List<String> values = new ArrayList<>();
        if (colIndex == -1) return values;
        
        // Skip header row (index 0)
        for (int i = 1; i < data.size(); i++) {
            String[] row = data.get(i);
            if (row.length > colIndex) {
                values.add(row[colIndex]);
            }
        }
        return values;
    }

    private void updateColumnInfo() {
        String datasetName = (String) datasetComboBox.getSelectedItem();
        if (datasetName == null) return;
        
        Dataset dataset = datasetManager.getDataset(datasetName);
        List<String[]> data = datasetManager.getDatasetContent(datasetName);
        
        if (dataset == null || data == null) {
            // Clear the column info panel if no valid dataset
            columnInfoPanel.updateColumnInfo(null, Collections.emptyMap());
            return;
        }
        
        // Get the color, defaulting to a standard color if null
        String colorStr = dataset.getColor();
        Color datasetColor;
        try {
            datasetColor = colorStr != null ? Color.decode(colorStr) : new Color(70, 120, 180);
        } catch (NumberFormatException e) {
            datasetColor = new Color(70, 120, 180); // Default blue color
        }
        
        // Update column info panel with the data and color
        columnInfoPanel.updateColumnInfo(data, Collections.singletonMap(
            datasetName, datasetColor
        ));
    }

    // Custom renderer for combo boxes
    private class CustomComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
            
            label.setBorder(BorderFactory.createEmptyBorder(5, 7, 5, 7));
            
            if (isSelected) {
                label.setBackground(PRIMARY_COLOR);
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(Color.WHITE);
                label.setForeground(PRIMARY_COLOR);
            }
            
            return label;
        }
    }

    // Override paintComponent to add gradient background
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
//
//        GradientPaint gp = new GradientPaint(0, 0,
//            new Color(255, 255, 255, 240),
//            0, getHeight(),
//            new Color(240, 245, 250, 240));
//
//        g2d.setPaint(gp);
//        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}