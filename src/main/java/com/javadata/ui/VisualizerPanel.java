package com.javadata.ui;

import com.javadata.data.DatasetManager;
import com.javadata.model.Dataset;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VisualizerPanel extends JPanel {
    private final JComboBox<String> datasetComboBox;
    private final JComboBox<String> visualizationTypeComboBox;
    private final JPanel chartPanelContainer;
    private final DatasetManager datasetManager;

    public VisualizerPanel() {
        setLayout(new BorderLayout());
        datasetManager = DatasetManager.getInstance();

        // Initialize components
        datasetComboBox = new JComboBox<>();
        visualizationTypeComboBox = new JComboBox<>(new String[]{"Bar Chart", "Line Chart", "Pie Chart"});
        JButton generateButton = new JButton("Generate Visualization");
        chartPanelContainer = new JPanel();

        // Add the components to the top panel
        JPanel controlsPanel = new JPanel(new FlowLayout());
        controlsPanel.add(new JLabel("Select Dataset:"));
        controlsPanel.add(datasetComboBox);
        controlsPanel.add(new JLabel("Select Visualization Type:"));
        controlsPanel.add(visualizationTypeComboBox);
        controlsPanel.add(generateButton);

        // Layout setup
        add(controlsPanel, BorderLayout.NORTH);
        add(chartPanelContainer, BorderLayout.CENTER);

        // Load initial datasets
        loadDatasets();

        // Add action listener for the generate button
        generateButton.addActionListener(e -> {
            String selectedDataset = (String) datasetComboBox.getSelectedItem();
            String selectedVisualizationType = (String) visualizationTypeComboBox.getSelectedItem();

            if (selectedDataset != null && selectedVisualizationType != null) {
                generateVisualization(selectedDataset, selectedVisualizationType);
            }
        });
    }

    private void loadDatasets() {
        datasetComboBox.removeAllItems();
        for (Dataset dataset : datasetManager.getAllDatasets()) {
            datasetComboBox.addItem(dataset.getName());
        }
    }

    private void generateVisualization(String datasetName, String visualizationType) {
        List<String[]> data = datasetManager.getDatasetContent(datasetName);
        if (data == null || data.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No data available for this dataset");
            return;
        }

        chartPanelContainer.removeAll();
        JFreeChart chart = null;

        try {
            switch (visualizationType) {
                case "Bar Chart":
                    chart = createBarChart(datasetName, data);
                    break;
                case "Line Chart":
                    chart = createLineChart(datasetName, data);
                    break;
                case "Pie Chart":
                    chart = createPieChart(datasetName, data);
                    break;
            }

            if (chart != null) {
                ChartPanel chartPanel = new ChartPanel(chart);
                chartPanel.setPreferredSize(new Dimension(800, 600));
                chartPanelContainer.setLayout(new BorderLayout());
                chartPanelContainer.add(chartPanel, BorderLayout.CENTER);
                chartPanelContainer.revalidate();
                chartPanelContainer.repaint();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error generating visualization: " + e.getMessage(),
                    "Visualization Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JFreeChart createBarChart(String datasetName, List<String[]> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Skip header row
        for (int i = 1; i < data.size(); i++) {
            String[] row = data.get(i);
            if (row.length >= 2) {
                try {
                    String category = row[0];
                    double value = Double.parseDouble(row[1]);
                    dataset.addValue(value, "Values", category);
                } catch (NumberFormatException ignored) {
                    // Skip invalid numeric values
                }
            }
        }

        return ChartFactory.createBarChart(
                datasetName,
                "Categories",
                "Values",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
    }

    private JFreeChart createLineChart(String datasetName, List<String[]> data) {
        XYSeries series = new XYSeries(datasetName);

        // Skip header row
        for (int i = 1; i < data.size(); i++) {
            String[] row = data.get(i);
            if (row.length >= 2) {
                try {
                    double x = Double.parseDouble(row[0]);
                    double y = Double.parseDouble(row[1]);
                    series.add(x, y);
                } catch (NumberFormatException ignored) {
                    // Skip invalid numeric values
                }
            }
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        return ChartFactory.createXYLineChart(
                datasetName,
                "X",
                "Y",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
    }

    private JFreeChart createPieChart(String datasetName, List<String[]> data) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        // Skip header row
        for (int i = 1; i < data.size(); i++) {
            String[] row = data.get(i);
            if (row.length >= 2) {
                try {
                    String category = row[0];
                    double value = Double.parseDouble(row[1]);
                    dataset.setValue(category, value);
                } catch (NumberFormatException ignored) {
                    // Skip invalid numeric values
                }
            }
        }

        return ChartFactory.createPieChart(
                datasetName,
                dataset,
                true,
                true,
                false
        );
    }
}