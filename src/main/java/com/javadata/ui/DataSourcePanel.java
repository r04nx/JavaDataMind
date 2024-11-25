package com.javadata.ui;

import com.javadata.data.DatasetManager;
import com.javadata.model.Dataset;
import com.javadata.model.UserProfile;
import com.javadata.ui.components.PlaceholderPasswordField;
import com.javadata.ui.components.PlaceholderTextField;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.PlotOrientation;

public class DataSourcePanel extends JPanel {
    private final JTable datasetsTable;
    private final DefaultTableModel tableModel;
    private final DatasetManager datasetManager;
    private final UserProfile currentUser;
    private static final Color BACKGROUND_COLOR = new Color(45, 45, 45);
    private static final Color FOREGROUND_COLOR = new Color(200, 200, 200);
    private static final Color BUTTON_COLOR = new Color(70, 120, 180);
    private static final String EDIT_EMOJI = "✏️";
    private static final String DELETE_EMOJI = "Delete";
    private static final String ADD_EMOJI = "➕";
    private static final String REFRESH_EMOJI = "🔄";
    private static final String SAVE_EMOJI = "💾";
    private static final String FILE_EMOJI = "📁";
    private JTextField searchField;
    private JComboBox<String> datasetComboBox;
    private String xAxisColumn;
    private JComboBox<String> visualizationTypeComboBox;

    public DataSourcePanel(UserProfile user) {
        this.currentUser = user;
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        datasetManager = DatasetManager.getInstance();

        // Create table model and table with custom renderer
        String[] columns = {"Name", "Type", "Color", "Uploaded By", "Upload Date", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 2) return Color.class;
                return super.getColumnClass(column);
            }
        };
        
        datasetsTable = new JTable(tableModel);
        datasetsTable.setBackground(BACKGROUND_COLOR);
        datasetsTable.setForeground(FOREGROUND_COLOR);
        datasetsTable.setGridColor(new Color(70, 70, 70));
        datasetsTable.getTableHeader().setBackground(new Color(60, 63, 65));
        datasetsTable.getTableHeader().setForeground(FOREGROUND_COLOR);
        datasetsTable.setRowHeight(30);
        
        // Custom renderer for the color column
        datasetsTable.getColumnModel().getColumn(2).setCellRenderer(new ColorCellRenderer());

        // Buttons panel with gradient background
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(50, 50, 50), 
                    0, getHeight(), new Color(40, 40, 40));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        JButton addDatasetBtn = createStyledButton(ADD_EMOJI + " Add Dataset", null);
        JButton refreshBtn = createStyledButton(REFRESH_EMOJI + " Refresh", null);
        buttonPanel.add(addDatasetBtn);
        buttonPanel.add(refreshBtn);

        searchField = new JTextField(20);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = searchField.getText().toLowerCase();
                filterDatasets(searchText);
            }
        });
        
        buttonPanel.add(searchField); // Add search field to button panel

        // Add components to panel
        add(buttonPanel, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(datasetsTable);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        add(scrollPane, BorderLayout.CENTER);

        // Add dataset button action
        addDatasetBtn.addActionListener(e -> showAddDatasetDialog());
        refreshBtn.addActionListener(e -> refreshDatasetsList());

        // Initial load
        refreshDatasetsList();

        // Add action column renderer
        TableColumn actionColumn = datasetsTable.getColumnModel().getColumn(5);
        actionColumn.setCellRenderer(new ActionButtonRenderer());
        actionColumn.setCellEditor(new ActionButtonEditor());
        actionColumn.setPreferredWidth(80);

        visualizationTypeComboBox = new JComboBox<>(new String[]{
            "Bar Chart", "Line Chart", "Area Chart", "Stacked Bar Chart"
        });
    }

    private JButton createStyledButton(String text, String iconName) {
        JButton button = new JButton(text);
        button.setFont(button.getFont().deriveFont(14f));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(BUTTON_COLOR.brighter());
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(BUTTON_COLOR);
            }
        });
        
        return button;
    }

    private void showAddDatasetDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Dataset", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Create styled components
        PlaceholderTextField nameField = new PlaceholderTextField("Dataset name");
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"CSV", "Database"});
        JButton colorPickerBtn = new JButton("Pick Color");
        JTextField uploaderField = new JTextField(currentUser.getName());
        uploaderField.setEditable(false);

        styleTextField(nameField);
        styleComboBox(typeCombo);
        styleButton(colorPickerBtn);
        styleTextField(uploaderField);

        // File selection
        JTextField filePathField = new JTextField();
        filePathField.setEditable(false);
        styleTextField(filePathField);
        JButton selectFileBtn = createStyledButton(FILE_EMOJI + " Select File", null);

        final File[] selectedFile = {null};
        final Color[] selectedColor = {Color.WHITE};

        selectFileBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                selectedFile[0] = fileChooser.getSelectedFile();
                filePathField.setText(selectedFile[0].getName());
                
                // Auto-fill name from file
                String fileName = Path.of(selectedFile[0].getName()).getFileName().toString();
                if (fileName.contains(".")) {
                    fileName = fileName.substring(0, fileName.lastIndexOf('.'));
                }
                nameField.setText(fileName);
            }
        });

        colorPickerBtn.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(dialog, "Choose Color", selectedColor[0]);
            if (newColor != null) {
                selectedColor[0] = newColor;
                colorPickerBtn.setBackground(newColor);
            }
        });

        // Add components to form
        addFormRow(form, "Name:", nameField, gbc, 0);
        addFormRow(form, "Type:", typeCombo, gbc, 1);
        addFormRow(form, "Color:", colorPickerBtn, gbc, 2);
        addFormRow(form, "Uploaded By:", uploaderField, gbc, 3);
        addFormRow(form, "File:", filePathField, gbc, 4);
        
        JPanel fileButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fileButtonPanel.setBackground(BACKGROUND_COLOR);
        fileButtonPanel.add(selectFileBtn);
        gbc.gridy = 5;
        gbc.gridx = 1;
        form.add(fileButtonPanel, gbc);

        // Save button
        JButton saveBtn = createStyledButton(SAVE_EMOJI + " Save", null);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(saveBtn);

        saveBtn.addActionListener(e -> {
            try {
                // Convert color to hex format properly
                String colorHex = String.format("#%02x%02x%02x", 
                    selectedColor[0].getRed(),
                    selectedColor[0].getGreen(),
                    selectedColor[0].getBlue());
                    
                Dataset dataset = new Dataset(
                    typeCombo.getSelectedItem().toString(),
                    nameField.getText(),
                    colorHex,  // Using properly formatted hex color
                    currentUser.getName()
                );
                datasetManager.addDataset(dataset, selectedFile[0]);
                refreshDatasetsList();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error adding dataset: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void refreshDatasetsList() {
        tableModel.setRowCount(0);
        for (Dataset dataset : datasetManager.getAllDatasets()) {
            Color color;
            try {
                // Try to decode hex color
                color = Color.decode(dataset.getColor());
            } catch (NumberFormatException e) {
                // If not a hex code, use a default color
                color = new Color(70, 120, 180); // Default blue color
            }
            
            tableModel.addRow(new Object[]{
                dataset.getName(),
                dataset.getType(),
                color,
                dataset.getUploadedBy(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                ""  // Empty string for action column
            });
        }
    }

    private void filterDatasets(String searchText) {
        tableModel.setRowCount(0);
        for (Dataset dataset : datasetManager.getAllDatasets()) {
            if (dataset.getName().toLowerCase().contains(searchText)) {
                // Add dataset to table model
                Color color = Color.decode(dataset.getColor());
                tableModel.addRow(new Object[]{
                    dataset.getName(),
                    dataset.getType(),
                    color,
                    dataset.getUploadedBy(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    ""
                });
            }
        }
    }

    // Custom renderer for color column
    private static class ColorCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof Color) {
                JPanel panel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        g.setColor((Color) value);
                        g.fillOval(5, 5, 20, 20); // Draw a circle
                    }
                };
                panel.setOpaque(false);
                return panel;
            }
            return super.getTableCellRendererComponent(table, value, isSelected, 
                hasFocus, row, column);
        }
    }

    private void styleTextField(JTextField field) {
        field.setBackground(new Color(60, 63, 65));
        field.setForeground(FOREGROUND_COLOR);
        field.setCaretColor(FOREGROUND_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setBackground(new Color(60, 63, 65));
        comboBox.setForeground(FOREGROUND_COLOR);
        comboBox.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
    }

    private void styleButton(JButton button) {
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    private void addFormRow(JPanel panel, String labelText, JComponent component, 
                          GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(labelText);
        label.setForeground(FOREGROUND_COLOR);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.1;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.9;
        panel.add(component, gbc);
    }

    private class ActionButtonRenderer extends DefaultTableCellRenderer {
        private final JPanel panel;
        private final JButton deleteButton;
        private int currentRow;

        public ActionButtonRenderer() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setOpaque(true);
            
            deleteButton = new JButton(DELETE_EMOJI);
            deleteButton.setFont(deleteButton.getFont().deriveFont(16f));
            deleteButton.setToolTipText("Delete");
            deleteButton.setBorderPainted(false);
            deleteButton.setContentAreaFilled(false);
            deleteButton.setFocusPainted(false);
            deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            deleteButton.addActionListener(e -> deleteDataset(currentRow));

            // Add hover effect
            deleteButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    deleteButton.setForeground(Color.RED);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    deleteButton.setForeground(Color.BLACK);
                }
            });

            panel.add(deleteButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            currentRow = row;
            panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return panel;
        }
    }

    private class ActionButtonEditor extends DefaultCellEditor {
        private final JPanel panel;
        private final JButton deleteButton;
        private int currentRow;

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        private void deleteAndStopEditing() {
            deleteDataset(currentRow);
            fireEditingStopped();
        }

        public ActionButtonEditor() {
            super(new JCheckBox());
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            
            deleteButton = new JButton(DELETE_EMOJI);
            deleteButton.setFont(deleteButton.getFont().deriveFont(16f));
            deleteButton.setToolTipText("Delete");
            deleteButton.setBorderPainted(false);
            deleteButton.setContentAreaFilled(false);
            deleteButton.setFocusPainted(false);
            deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            deleteButton.addActionListener(e -> deleteAndStopEditing());

            panel.add(deleteButton);
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    private void deleteDataset(int row) {
        String datasetName = (String) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete dataset '" + datasetName + "'?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                datasetManager.deleteDataset(datasetName);
                refreshDatasetsList();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error deleting dataset: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void showColumnInfo(int row) {
        String datasetName = (String) tableModel.getValueAt(row, 0);
        List<String[]> data = datasetManager.getDatasetContent(datasetName);
        
        if (data != null && !data.isEmpty()) {
            StringBuilder info = new StringBuilder("Columns:\n");
            String[] headers = data.get(0);
            for (String header : headers) {
                info.append(header).append("\n");
            }
            JOptionPane.showMessageDialog(this, info.toString(), "Column Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showColumnSelectionDialog(List<String> columns) {
        String[] columnArray = columns.toArray(new String[0]);
        JList<String> columnList = new JList<>(columnArray);
        columnList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        int result = JOptionPane.showConfirmDialog(this, new JScrollPane(columnList), "Select Columns for Visualization", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            List<String> selectedColumns = columnList.getSelectedValuesList();
            visualizeColumns(selectedColumns);
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
            if (columnIndex == -1) {
                showError("Column '" + column + "' not found.");
                return; // Exit if the column is not found
            }
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

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
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
        return -1;
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
}