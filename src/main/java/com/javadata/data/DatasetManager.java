package com.javadata.data;

import com.javadata.model.Dataset;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatasetManager {
    private static DatasetManager instance;
    private final Map<String, Dataset> datasets;
    private final Map<String, List<String[]>> dataCache;

    private DatasetManager() {
        datasets = new HashMap<>();
        dataCache = new HashMap<>();
    }

    public static DatasetManager getInstance() {
        if (instance == null) {
            instance = new DatasetManager();
        }
        return instance;
    }

    public void addDataset(Dataset dataset, File dataFile) throws IOException, CsvException {
        datasets.put(dataset.getName(), dataset);
        
        if (dataFile != null && dataFile.exists()) {
            try (CSVReader reader = new CSVReader(new FileReader(dataFile))) {
                List<String[]> data = reader.readAll();
                dataCache.put(dataset.getName(), data);
            }
        }
    }

    public List<Dataset> getAllDatasets() {
        return new ArrayList<>(datasets.values());
    }

    public List<String[]> getDatasetContent(String datasetName) {
        return dataCache.get(datasetName);
    }
}