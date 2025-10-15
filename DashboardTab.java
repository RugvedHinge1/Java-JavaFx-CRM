import javafx.scene.layout.GridPane;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import java.util.*;
import java.io.File;

public class DashboardTab extends Tab {
    private PieChart salesPieChart;
    private BarChart<String, Number> dealsBarChart;
    private LineChart<String, Number> balanceLineChart;
    private ColorPicker chartColorPicker;
    
    public DashboardTab() {
        setText("Dashboard");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(20);
        grid.setVgap(20);
        grid.getStyleClass().add("dashboard-grid");
        // Create color picker for chart customization
        chartColorPicker = new ColorPicker(Color.web("#7986cb"));
        chartColorPicker.setOnAction(e -> updateChartColors());
        
        // Initialize charts
        initializeSalesPieChart();
        initializeDealsBarChart();
        initializeBalanceLineChart();
        
        // Add charts to grid
        VBox colorPickerBox = new VBox(10, new Label("Chart Colors"), chartColorPicker);
        colorPickerBox.getStyleClass().add("control-box");
        grid.add(colorPickerBox, 0, 0);
        grid.add(salesPieChart, 0, 1);
        grid.add(dealsBarChart, 1, 1);
        grid.add(balanceLineChart, 0, 2, 2, 1);
        
        // Apply chart theme
        grid.getStylesheets().add(new File("chart-theme.css").toURI().toString());
        
        setContent(grid);
        
        // Update charts with initial data
        updateCharts();
    }
    
    private void initializeSalesPieChart() {
        salesPieChart = new PieChart();
        salesPieChart.setTitle("Sales Distribution");
        salesPieChart.setPrefSize(800, 600);
        salesPieChart.setMinSize(600, 400);
        salesPieChart.setLegendVisible(true);
        salesPieChart.setLabelLineLength(10);
        salesPieChart.setLabelsVisible(true);
        salesPieChart.getStyleClass().add("custom-chart");
        salesPieChart.setAnimated(false);
    }
    
    private void initializeDealsBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        dealsBarChart = new BarChart<>(xAxis, yAxis);
        dealsBarChart.setTitle("Deals Overview");
        dealsBarChart.setPrefSize(800, 600);
        dealsBarChart.setMinSize(600, 400);
        dealsBarChart.setAnimated(false);
        dealsBarChart.setBarGap(8);
        dealsBarChart.setCategoryGap(20);
        dealsBarChart.getStyleClass().add("custom-chart");
        xAxis.setLabel("Status");
        yAxis.setLabel("Value");
    }
    
    private void initializeBalanceLineChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        balanceLineChart = new LineChart<>(xAxis, yAxis);
        balanceLineChart.setTitle("Account Balance Trend");
        balanceLineChart.setPrefSize(1000, 600);
        balanceLineChart.setMinSize(800, 400);
        balanceLineChart.setCreateSymbols(true);
        balanceLineChart.setAnimated(false);
        balanceLineChart.setAlternativeRowFillVisible(false);
        balanceLineChart.setAlternativeColumnFillVisible(false);
        balanceLineChart.getStyleClass().add("custom-chart");
        xAxis.setLabel("Date");
        yAxis.setLabel("Balance");
    }
    
    public void updateCharts() {
        updateSalesPieChart();
        updateDealsBarChart();
        updateBalanceLineChart();
    }
    
    private void updateSalesPieChart() {
        Map<String, Double> salesByProduct = new HashMap<>();
        
        try {
            String filePath;
            if (UserManager.isAdmin()) {
                filePath = "sales.csv";
            } else {
                String currentUser = UserManager.getCurrentUser();
                filePath = "data" + File.separator + currentUser + File.separator + "user_" + currentUser + "_sales.csv";
            }
            
            File salesFile = new File(filePath);
            if (salesFile.exists()) {
                List<String[]> salesData = DataManager.readCSV(filePath);
                
                // Skip header row
                for (int i = 1; i < salesData.size(); i++) {
                    String[] record = salesData.get(i);
                    if (record.length >= 3) {
                        String product = record[1];
                        try {
                            double amount = Double.parseDouble(record[2]);
                            salesByProduct.merge(product, amount, Double::sum);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid amount format in sales data: " + record[2]);
                        }
                    }
                }
            } else {
                System.err.println("Sales data file not found: " + filePath);
            }
        } catch (Exception e) {
            System.err.println("Error loading sales data: " + e.getMessage());
            e.printStackTrace();
        }
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        salesByProduct.forEach((product, amount) ->
            pieChartData.add(new PieChart.Data(product + " ($" + amount + ")", amount)));
        
        salesPieChart.setData(pieChartData);
        salesPieChart.setVisible(true);
        salesPieChart.setAnimated(true);
    }
    
    private void updateDealsBarChart() {
        Map<String, Double> dealsByStatus = new HashMap<>();
        
        try {
            String filePath = "deals.csv";
            File dealsFile = new File(filePath);
            if (dealsFile.exists()) {
                List<String[]> dealsData = DataManager.readCSV(filePath);
                
                // Skip header row
                for (int i = 1; i < dealsData.size(); i++) {
                    String[] record = dealsData.get(i);
                    if (record.length >= 4) {
                        String status = record[3];
                        try {
                            double value = Double.parseDouble(record[2]);
                            dealsByStatus.merge(status, value, Double::sum);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid value format in deals data: " + record[2]);
                        }
                    }
                }
            } else {
                // For workers, show only their data
                String currentUser = UserManager.getCurrentUser();
                String userFilePath = "data" + File.separator + currentUser + File.separator + "user_" + currentUser + "_deals.csv";
                File userDealsFile = new File(userFilePath);
                if (userDealsFile.exists()) {
                    List<String[]> dealsData = DataManager.readCSV(userFilePath);
                    
                    // Skip header row
                    for (int i = 1; i < dealsData.size(); i++) {
                        String[] record = dealsData.get(i);
                        if (record.length >= 4) {
                            String status = record[3];
                            try {
                                double value = Double.parseDouble(record[2]);
                                dealsByStatus.merge(status, value, Double::sum);
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid value format in deals data: " + record[2]);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading deals data: " + e.getMessage());
        }
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Deal Value");
        dealsByStatus.forEach((status, value) ->
            series.getData().add(new XYChart.Data<>(status, value)));
        
        dealsBarChart.getData().clear();
        dealsBarChart.getData().add(series);
        dealsBarChart.setVisible(true);
        dealsBarChart.setAnimated(true);
    }
    
    private void updateBalanceLineChart() {
        Map<String, Double> balanceByDate = new TreeMap<>(); // TreeMap to maintain date order
        
        try {
            if (UserManager.isAdmin()) {
                // For admin, aggregate data from all user directories
                File dataDir = new File("data");
                if (dataDir.exists() && dataDir.isDirectory()) {
                    for (File userDir : dataDir.listFiles()) {
                        if (userDir.isDirectory() && !userDir.getName().equals("admin")) {
                            String filePath = "data" + File.separator + userDir.getName() + File.separator + "user_" + userDir.getName() + "_balance.csv";
                            File balanceFile = new File(filePath);
                            if (balanceFile.exists()) {
                                List<String[]> balanceData = DataManager.readCSV(filePath);
                                
                                // Skip header row
                                for (int i = 1; i < balanceData.size(); i++) {
                                    String[] record = balanceData.get(i);
                                    if (record.length >= 5) {
                                        String date = record[0];
                                        try {
                                            double balance = Double.parseDouble(record[4]);
                                            balanceByDate.merge(date, balance, Double::sum);
                                        } catch (NumberFormatException e) {
                                            System.err.println("Invalid balance format in balance data: " + record[4]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // For workers, show only their data
                String currentUser = UserManager.getCurrentUser();
                String filePath = "data" + File.separator + currentUser + File.separator + "user_" + currentUser + "_balance.csv";
                File balanceFile = new File(filePath);
                if (balanceFile.exists()) {
                    List<String[]> balanceData = DataManager.readCSV(filePath);
                    
                    // Skip header row
                    for (int i = 1; i < balanceData.size(); i++) {
                        String[] record = balanceData.get(i);
                        if (record.length >= 5) {
                            String date = record[0];
                            try {
                                double balance = Double.parseDouble(record[4]);
                                balanceByDate.put(date, balance);
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid balance format in balance data: " + record[4]);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading balance data: " + e.getMessage());
        }
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Balance");
        
        // Add data points in date order
        balanceByDate.forEach((date, balance) ->
            series.getData().add(new XYChart.Data<>(date, balance)));

        
        balanceLineChart.getData().clear();
        balanceLineChart.getData().add(series);
        balanceLineChart.setVisible(true);
        balanceLineChart.setAnimated(true);
    }
    
    private void updateChartColors() {
        Color color = chartColorPicker.getValue();
        String colorStyle = "-fx-pie-color: " + toRGBCode(color) + ";";
            
        salesPieChart.getData().forEach(data ->
            data.getNode().setStyle(colorStyle));
            
        dealsBarChart.getData().forEach(series ->
            series.getData().forEach(data ->
                data.getNode().setStyle(
                    "-fx-bar-fill: " + toRGBCode(color) + ";"
                )
            ));
            
        balanceLineChart.getData().forEach(series ->
            series.getNode().setStyle(
                "-fx-stroke: " + toRGBCode(color) + ";"
            ));
    }
    
    private String toRGBCode(Color color) {
        return String.format("#%02X%02X%02X",
            (int)(color.getRed() * 255),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255));
    }
}