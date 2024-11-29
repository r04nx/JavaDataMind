package com.javadata.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.DoubleSummaryStatistics;
import java.util.stream.Collectors;

public class TrendAnalyzer {
    public static Map<String, Double> calculateTrends(List<String[]> data, String column) {
        Map<String, Double> trends = new HashMap<>();
        int columnIndex = getColumnIndex(column, data);
        
        if (columnIndex == -1) return trends;
        
        List<Double> values = new ArrayList<>();
        for (int i = 1; i < data.size(); i++) {
            try {
                values.add(Double.parseDouble(data.get(i)[columnIndex]));
            } catch (NumberFormatException ignored) {}
        }
        
        if (values.isEmpty()) return trends;
        
        // Calculate basic trends
        DoubleSummaryStatistics stats = values.stream()
            .mapToDouble(Double::doubleValue)
            .summaryStatistics();
            
        trends.put("mean", stats.getAverage());
        trends.put("min", stats.getMin());
        trends.put("max", stats.getMax());
        trends.put("slope", calculateSlope(values));
        trends.put("variance", calculateVariance(values, stats.getAverage()));
        trends.put("trend_direction", trends.get("slope") > 0 ? 1.0 : -1.0);
        trends.put("range", stats.getMax() - stats.getMin());
        
        // Calculate quartiles
        List<Double> sortedValues = values.stream().sorted().collect(Collectors.toList());
        trends.put("median", calculateMedian(sortedValues));
        trends.put("q1", calculateQuartile(sortedValues, 0.25));
        trends.put("q3", calculateQuartile(sortedValues, 0.75));
        
        return trends;
    }
    
    private static double calculateSlope(List<Double> values) {
        int n = values.size();
        if (n < 2) return 0;
        
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < n; i++) {
            sumX += i;
            sumY += values.get(i);
            sumXY += i * values.get(i);
            sumX2 += i * i;
        }
        
        return (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
    }
    
    private static double calculateVariance(List<Double> values, double mean) {
        return values.stream()
            .mapToDouble(v -> Math.pow(v - mean, 2))
            .average()
            .orElse(0);
    }
    
    private static double calculateMedian(List<Double> sortedValues) {
        int size = sortedValues.size();
        if (size % 2 == 0) {
            return (sortedValues.get(size/2 - 1) + sortedValues.get(size/2)) / 2.0;
        } else {
            return sortedValues.get(size/2);
        }
    }
    
    private static double calculateQuartile(List<Double> sortedValues, double percentile) {
        int index = (int) Math.ceil(percentile * (sortedValues.size() - 1));
        return sortedValues.get(index);
    }
    
    private static int getColumnIndex(String columnName, List<String[]> data) {
        String[] headers = data.get(0);
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals(columnName)) {
                return i;
            }
        }
        return -1;
    }
} 