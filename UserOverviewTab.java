import javafx.scene.layout.GridPane;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import java.util.*;
import java.io.File;

public class UserOverviewTab extends Tab {
    private TableView<UserActivityData> activityTable;
    private PieChart userSalesPieChart;
    private BarChart<String, Number> userDealsBarChart;

    public UserOverviewTab() {
        setText("User Overview");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(20);
        grid.setVgap(20);
        grid.getStyleClass().add("dashboard-grid");

        initializeActivityTable();
        initializeCharts();

        grid.add(activityTable, 0, 0, 2, 1);
        grid.add(userSalesPieChart, 0, 1);
        grid.add(userDealsBarChart, 1, 1);

        setContent(grid);
        updateData();
    }

    private void initializeActivityTable() {
        activityTable = new TableView<>();
        activityTable.setPrefHeight(300);

        TableColumn<UserActivityData, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(data -> data.getValue().usernameProperty());

        TableColumn<UserActivityData, Number> salesCol = new TableColumn<>("Total Sales");
        salesCol.setCellValueFactory(data -> data.getValue().totalSalesProperty());

        TableColumn<UserActivityData, Number> dealsCol = new TableColumn<>("Active Deals");
        dealsCol.setCellValueFactory(data -> data.getValue().activeDealsProperty());

        TableColumn<UserActivityData, Number> customersCol = new TableColumn<>("Customers");
        customersCol.setCellValueFactory(data -> data.getValue().customerCountProperty());

        activityTable.getColumns().addAll(usernameCol, salesCol, dealsCol, customersCol);
    }

    private void initializeCharts() {
        // Initialize Sales Pie Chart
        userSalesPieChart = new PieChart();
        userSalesPieChart.setTitle("Sales by User");
        userSalesPieChart.setPrefSize(400, 300);

        // Initialize Deals Bar Chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        userDealsBarChart = new BarChart<>(xAxis, yAxis);
        userDealsBarChart.setTitle("Deals by User");
        userDealsBarChart.setPrefSize(400, 300);
        xAxis.setLabel("User");
        yAxis.setLabel("Number of Deals");
    }

    public void updateData() {
        if (!UserManager.isAdmin()) return;

        File dataDir = new File("data");
        File[] userDirs = dataDir.listFiles(File::isDirectory);
        ObservableList<UserActivityData> userData = FXCollections.observableArrayList();
        ObservableList<PieChart.Data> salesData = FXCollections.observableArrayList();
        XYChart.Series<String, Number> dealsSeries = new XYChart.Series<>();

        if (userDirs != null) {
            for (File userDir : userDirs) {
                String username = userDir.getName();
                if (!username.equals("admin")) {
                    double totalSales = calculateTotalSales(username);
                    int activeDeals = countActiveDeals(username);
                    int customerCount = countCustomers(username);

                    userData.add(new UserActivityData(username, totalSales, activeDeals, customerCount));
                    salesData.add(new PieChart.Data(username + " ($" + String.format("%.2f", totalSales) + ")", totalSales));
                    dealsSeries.getData().add(new XYChart.Data<>(username, activeDeals));
                }
            }
        }

        activityTable.setItems(userData);
        userSalesPieChart.setData(salesData);
        userDealsBarChart.getData().clear();
        userDealsBarChart.getData().add(dealsSeries);
    }

    private double calculateTotalSales(String username) {
        List<String[]> sales = DataManager.readCSV("data/" + username + "/user_" + username + "_sales.csv");
        double total = 0;
        for (int i = 1; i < sales.size(); i++) {
            try {
                total += Double.parseDouble(sales.get(i)[2]);
            } catch (NumberFormatException e) {
                // Skip invalid entries
            }
        }
        return total;
    }

    private int countActiveDeals(String username) {
        List<String[]> deals = DataManager.readCSV("data/" + username + "/user_" + username + "_deals.csv");
        int count = 0;
        for (int i = 1; i < deals.size(); i++) {
            if (deals.get(i)[3].equalsIgnoreCase("active")) {
                count++;
            }
        }
        return count;
    }

    private int countCustomers(String username) {
        List<String[]> customers = DataManager.readCSV("data/" + username + "/user_" + username + "_customers.csv");
        return Math.max(0, customers.size() - 1); // Subtract header row
    }
}