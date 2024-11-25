package com.javadata.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.util.List;

public class ChartUtils {

    public static ChartPanel createBarChart(String title, String xAxisLabel, List<String> yAxisLabels, List<String[]> data) {
        CategoryDataset dataset = createDatasetForBarChart(yAxisLabels, data);
        JFreeChart chart = ChartFactory.createBarChart(
                title,
                xAxisLabel,
                "Values",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
        return new ChartPanel(chart);
    }

    public static ChartPanel createPieChart(String title, List<String> yAxisLabels, List<String[]> data) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (String label : yAxisLabels) {
            dataset.setValue(label, Double.parseDouble(data.get(1)[getColumnIndex(label, data)])); // Replace with actual data
        }
        JFreeChart chart = ChartFactory.createPieChart(
                title,
                dataset,
                true, true, false
        );
        return new ChartPanel(chart);
    }

    public static ChartPanel createLineChart(String title, String xAxisLabel, List<String> yAxisLabels, List<String[]> data) {
        XYSeriesCollection dataset = createDatasetForLineChart(yAxisLabels, data);
        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                xAxisLabel,
                "Values",
                dataset
        );
        return new ChartPanel(chart);
    }

    public static ChartPanel createAreaChart(String title, String xAxisLabel, List<String> yAxisLabels, List<String[]> data) {
        XYSeriesCollection dataset = createDatasetForAreaChart(yAxisLabels, data);
        JFreeChart chart = ChartFactory.createXYAreaChart(
                title,
                xAxisLabel,
                "Values",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
        return new ChartPanel(chart);
    }

    public static ChartPanel createScatterPlot(String title, String xAxisLabel, List<String> yAxisLabels, List<String[]> data) {
        XYSeriesCollection dataset = createDatasetForScatterPlot(yAxisLabels, data);
        JFreeChart chart = ChartFactory.createScatterPlot(
                title,
                xAxisLabel,
                "Values",
                dataset
        );
        return new ChartPanel(chart);
    }

    // Helper methods to create datasets
    private static CategoryDataset createDatasetForBarChart(List<String> yAxisLabels, List<String[]> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (String label : yAxisLabels) {
            for (int i = 1; i < data.size(); i++) { // Start from 1 to skip header
                dataset.addValue(Double.parseDouble(data.get(i)[getColumnIndex(label, data)]), label, data.get(i)[0]); // Assuming first column is category
            }
        }
        return dataset;
    }

    private static XYSeriesCollection createDatasetForLineChart(List<String> yAxisLabels, List<String[]> data) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (String label : yAxisLabels) {
            XYSeries series = new XYSeries(label);
            for (int i = 1; i < data.size(); i++) { // Start from 1 to skip header
                series.add(i, Double.parseDouble(data.get(i)[getColumnIndex(label, data)])); // Assuming first column is x-axis
            }
            dataset.addSeries(series);
        }
        return dataset;
    }

    private static XYSeriesCollection createDatasetForAreaChart(List<String> yAxisLabels, List<String[]> data) {
        return createDatasetForLineChart(yAxisLabels, data); // Reusing line chart dataset for area chart
    }

    private static XYSeriesCollection createDatasetForScatterPlot(List<String> yAxisLabels, List<String[]> data) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (String label : yAxisLabels) {
            XYSeries series = new XYSeries(label);
            for (int i = 1; i < data.size(); i++) { // Start from 1 to skip header
                series.add(Double.parseDouble(data.get(i)[0]), Double.parseDouble(data.get(i)[getColumnIndex(label, data)])); // Assuming first column is x-axis
            }
            dataset.addSeries(series);
        }
        return dataset;
    }

    private static int getColumnIndex(String columnName, List<String[]> data) {
        String[] header = data.get(0); // Assuming the first row contains the headers
        for (int i = 0; i < header.length; i++) {
            if (header[i].equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        return -1; // Not found
    }
}
