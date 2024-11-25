package com.javadata.data;

import com.javadata.model.Dataset;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DatasetManager {
    private static DatasetManager instance;
    private final DatabaseManager dbManager;
    private final String DATA_DIR = "data";

    private static final String EDIT_EMOJI = "✏️";
    private static final String DELETE_EMOJI = "🗑️";
    private static final String SAVE_EMOJI = "💾";

    private DatasetManager() {
        dbManager = DatabaseManager.getInstance();
        createDataDirectory();
    }

    private void createDataDirectory() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
    }

    public static DatasetManager getInstance() {
        if (instance == null) {
            instance = new DatasetManager();
        }
        return instance;
    }

    public void addDataset(Dataset dataset, File file) throws SQLException {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("Invalid file");
        }
        
        // Validate file type
        String fileName = file.getName().toLowerCase();
        if (!fileName.endsWith(".csv")) {
            throw new IllegalArgumentException("Only CSV files are supported");
        }
        
        // Validate file size (e.g., max 10MB)
        if (file.length() > 10_000_000) {
            throw new IllegalArgumentException("File size exceeds maximum limit");
        }

        // Copy file with unique name
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String newFileName = dataset.getName() + "_" + timestamp + ".csv";
        Path destination = Paths.get(DATA_DIR, newFileName);
        
        try {
            Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to copy dataset file", e);
        }

        // Save to database
        String sql = "INSERT INTO datasets (name, type, color, uploaded_by, file_path) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, dataset.getName());
            pstmt.setString(2, dataset.getType());
            pstmt.setString(3, dataset.getColor());
            pstmt.setString(4, dataset.getUploadedBy());
            pstmt.setString(5, destination.toString());
            pstmt.executeUpdate();
        }
    }

    public List<Dataset> getAllDatasets() {
        List<Dataset> datasets = new ArrayList<>();
        String sql = "SELECT * FROM datasets";
        
        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Dataset dataset = new Dataset(
                    rs.getString("type"),
                    rs.getString("name"),
                    rs.getString("color"),
                    rs.getString("uploaded_by")
                );
                datasets.add(dataset);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datasets;
    }

    public List<String[]> getDatasetContent(String datasetName) {
        String sql = "SELECT file_path FROM datasets WHERE name = ?";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, datasetName);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String filePath = rs.getString("file_path");
                if (filePath != null) {
                    try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
                        return reader.readAll();
                    }
                }
            }
        } catch (SQLException | IOException | CsvException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Dataset getDatasetByName(String name) {
        String sql = "SELECT * FROM datasets WHERE name = ?";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Dataset(
                    rs.getString("type"),
                    rs.getString("name"),
                    rs.getString("color"),
                    rs.getString("uploaded_by")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateDataset(Dataset dataset) throws SQLException {
        String sql = "UPDATE datasets SET type = ?, color = ? WHERE name = ?";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, dataset.getType());
            pstmt.setString(2, dataset.getColor());
            pstmt.setString(3, dataset.getName());
            pstmt.executeUpdate();
        }
    }

    public void deleteDataset(String name) throws SQLException {
        // First get the file path
        String getPathSql = "SELECT file_path FROM datasets WHERE name = ?";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(getPathSql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String filePath = rs.getString("file_path");
                if (filePath != null) {
                    // Delete the physical file
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        }

        // Then delete from database
        String deleteSql = "DELETE FROM datasets WHERE name = ?";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(deleteSql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        }
    }

    public List<String> getDatasetColumns(String datasetName) {
        List<String[]> data = getDatasetContent(datasetName);
        List<String> columns = new ArrayList<>();
        
        if (data != null && !data.isEmpty()) {
            String[] header = data.get(0); // Assuming the first row contains the headers
            for (String column : header) {
                columns.add(column);
            }
        }
        
        return columns;
    }

    private boolean isValidDate(String value) {
        try {
            LocalDate.parse(value); // Adjust format if necessary
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    // Method to find the index of a column by its name
    public int selectedColumnIndex(String columnName, String datasetName) {
        List<String[]> data = this.getDatasetContent(datasetName);
        if (data != null && !data.isEmpty()) {
            String[] header = data.get(0); // Assuming the first row contains the headers
            for (int i = 0; i < header.length; i++) {
                if (header[i].equalsIgnoreCase(columnName)) {
                    return i; // Return the index if found
                }
            }
        }
        return -1; // Return -1 if the column is not found
    }

    // Modify the isDateOrTimestampColumn method to use the new validation
    public boolean isDateOrTimestampColumn(String datasetName, String column) {
        List<String[]> data = this.getDatasetContent(datasetName);
        if (data != null && !data.isEmpty()) {
            int columnIndex = selectedColumnIndex(column, datasetName); // Pass datasetName to the method
            for (int i = 1; i < data.size(); i++) {
                String[] row = data.get(i);
                if (row.length > columnIndex && columnIndex != -1) {
                    String value = row[columnIndex];
                    if (isValidDate(value)) {
                        return true; // Found a valid date or timestamp
                    }
                }
            }
        }
        return false; // No valid date or timestamp found
    }

    // New method to refresh datasets
    public void refreshDatasets() {
        // Logic to refresh datasets, e.g., reloading from the source or database
        // This could involve clearing cached data or re-fetching from the database
        // For example, you might want to reload the datasets from the database
        // or clear any cached datasets if applicable.

        // Clear any cached datasets if applicable
        // Assuming you have a cache or list to hold datasets, clear it
        // datasetsCache.clear(); // Uncomment if you have a cache

        // Fetch the latest datasets from the database
        List<Dataset> updatedDatasets = getAllDatasets();
        
        // Optionally, you can log or process the updated datasets
        System.out.println("Datasets refreshed. Total datasets: " + updatedDatasets.size());
    }
}