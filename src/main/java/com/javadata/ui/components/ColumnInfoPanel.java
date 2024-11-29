package com.javadata.ui.components;

import com.javadata.util.DataTypeDetector;
import com.javadata.util.DataTypeDetector.DataType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ColumnInfoPanel extends JPanel {
    private final DefaultTableModel columnModel;
    private final JTable columnTable;
    
    public ColumnInfoPanel() {
        setLayout(new BorderLayout());
        
        // Create table model for column information
        String[] headers = {"Column Name", "Data Type", "Sample Values", "Statistics"};
        columnModel = new DefaultTableModel(headers, 0);
        columnTable = new JTable(columnModel);
        
        // Style the table
        columnTable.setBackground(new Color(45, 45, 45));
        columnTable.setForeground(new Color(200, 200, 200));
        columnTable.setGridColor(new Color(70, 70, 70));
        
        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(columnTable);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void updateColumnInfo(List<String[]> data, Map<String, Color> datasetColors) {
        columnModel.setRowCount(0);
        if (data == null || data.isEmpty()) return;
        
        String[] headers = data.get(0);
        for (int i = 0; i < headers.length; i++) {
            List<String> columnValues = new ArrayList<>();
            for (int j = 1; j < data.size(); j++) {
                columnValues.add(data.get(j)[i]);
            }
            
            DataType type = DataTypeDetector.detectColumnType(columnValues);
            String statistics = calculateStatistics(columnValues, type);
            String sampleValues = String.join(", ", 
                columnValues.stream().limit(3).collect(Collectors.toList()));
            
            columnModel.addRow(new Object[]{
                headers[i],
                type,
                sampleValues,
                statistics
            });
        }
    }
    
    private String calculateStatistics(List<String> values, DataType type) {
        StringBuilder stats = new StringBuilder();
        
        switch (type) {
            case NUMERIC_INTEGER, NUMERIC_DECIMAL -> {
                DoubleSummaryStatistics summary = values.stream()
                    .filter(v -> v != null && !v.trim().isEmpty())
                    .mapToDouble(Double::parseDouble)
                    .summaryStatistics();
                stats.append(String.format("Min: %.2f, Max: %.2f, Avg: %.2f", 
                    summary.getMin(), summary.getMax(), summary.getAverage()));
            }
            case CATEGORICAL -> {
                Map<String, Long> frequencies = values.stream()
                    .collect(Collectors.groupingBy(v -> v, Collectors.counting()));
                stats.append("Unique values: ").append(frequencies.size());
            }
            case DATE_TIME -> {
                stats.append("Date range available");
            }
            default -> stats.append("N/A");
        }
        
        return stats.toString();
    }
}