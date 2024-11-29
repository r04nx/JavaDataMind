package com.javadata.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DataTypeDetector {
    private static final int SAMPLE_SIZE = 100;
    
    public enum DataType {
        NUMERIC_INTEGER,
        NUMERIC_DECIMAL,
        DATE_TIME,
        TEXT,
        BOOLEAN,
        CATEGORICAL,
        UNKNOWN
    }
    
    public static DataType detectColumnType(List<String> columnValues) {
        if (columnValues == null || columnValues.isEmpty()) {
            return DataType.UNKNOWN;
        }
        
        // Take a sample of values for performance
        List<String> sampleValues = getSampleValues(columnValues);
        
        // Check for data types in order of specificity
        if (isNumericInteger(sampleValues)) return DataType.NUMERIC_INTEGER;
        if (isNumericDecimal(sampleValues)) return DataType.NUMERIC_DECIMAL;
        if (isDateTime(sampleValues)) return DataType.DATE_TIME;
        if (isBoolean(sampleValues)) return DataType.BOOLEAN;
        if (isCategorical(sampleValues)) return DataType.CATEGORICAL;
        
        return DataType.TEXT;
    }
    
    private static List<String> getSampleValues(List<String> values) {
        if (values.size() <= SAMPLE_SIZE) return values;
        
        List<String> sample = new ArrayList<>();
        int step = values.size() / SAMPLE_SIZE;
        for (int i = 0; i < values.size(); i += step) {
            String value = values.get(i);
            if (value != null && !value.trim().isEmpty()) {
                sample.add(value.trim());
            }
        }
        return sample;
    }
    
    private static boolean isNumericInteger(List<String> values) {
        return values.stream()
            .filter(v -> v != null && !v.trim().isEmpty())
            .allMatch(v -> v.matches("-?\\d+"));
    }
    
    private static boolean isNumericDecimal(List<String> values) {
        return values.stream()
            .filter(v -> v != null && !v.trim().isEmpty())
            .allMatch(v -> v.matches("-?\\d*\\.?\\d+"));
    }
    
    private static boolean isDateTime(List<String> values) {
        String[] datePatterns = {
            "yyyy-MM-dd", "MM/dd/yyyy", "dd-MM-yyyy",
            "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy HH:mm:ss"
        };
        
        return values.stream()
            .filter(v -> v != null && !v.trim().isEmpty())
            .allMatch(v -> {
                for (String pattern : datePatterns) {
                    try {
                        new SimpleDateFormat(pattern).parse(v);
                        return true;
                    } catch (ParseException ignored) {}
                }
                return false;
            });
    }
    
    private static boolean isBoolean(List<String> values) {
        Set<String> booleanValues = Set.of(
            "true", "false", "yes", "no", "1", "0", "y", "n"
        );
        return values.stream()
            .filter(v -> v != null && !v.trim().isEmpty())
            .allMatch(v -> booleanValues.contains(v.toLowerCase()));
    }
    
    private static boolean isCategorical(List<String> values) {
        Set<String> uniqueValues = new HashSet<>(values);
        return uniqueValues.size() <= Math.min(values.size() * 0.1, 50);
    }
}