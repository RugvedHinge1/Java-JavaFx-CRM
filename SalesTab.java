import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.util.*;

public class SalesTab extends Tab {
    private TableView<Sale> salesTable;
    private TextField productField;
    private TextField amountField;
    private TextField customerField;
    private DatePicker datePicker;
    
    public SalesTab() {
        setText("Sales");
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        
        // Create input fields
        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);
        
        datePicker = new DatePicker(LocalDate.now());
        productField = new TextField();
        amountField = new TextField();
        customerField = new TextField();
        
        inputGrid.addRow(0, new Label("Date:"), datePicker);
        inputGrid.addRow(1, new Label("Product:"), productField);
        inputGrid.addRow(2, new Label("Amount:"), amountField);
        inputGrid.addRow(3, new Label("Customer:"), customerField);
        
        Button addButton = new Button("Add Sale");
        addButton.setOnAction(e -> addSale());
        
        // Create table
        salesTable = new TableView<>();
        TableColumn<Sale, String> dateCol = new TableColumn<>("Date");
        TableColumn<Sale, String> productCol = new TableColumn<>("Product");
        TableColumn<Sale, Double> amountCol = new TableColumn<>("Amount");
        TableColumn<Sale, String> customerCol = new TableColumn<>("Customer");
        
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        productCol.setCellValueFactory(new PropertyValueFactory<>("product"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customer"));
        
        salesTable.getColumns().clear(); // Clear existing columns first
        salesTable.getColumns().addAll(dateCol, productCol, amountCol, customerCol);
        
        // Create edit and delete buttons
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        
        editButton.setOnAction(e -> editSale());
        deleteButton.setOnAction(e -> deleteSale());
        
        HBox buttonBox = new HBox(10, addButton, editButton, deleteButton);
        
        content.getChildren().addAll(inputGrid, buttonBox, salesTable);
        setContent(content);
        
        // Load initial data
        loadSalesData();
    }
    
    private void loadSalesData() {
        List<String[]> records = DataManager.readCSV("sales.csv");
        ObservableList<Sale> sales = FXCollections.observableArrayList();
        
        // Skip header row
        for (int i = 1; i < records.size(); i++) {
            String[] record = records.get(i);
            sales.add(new Sale(
                record[0],
                record[1],
                Double.parseDouble(record[2]),
                record[3]
            ));
        }
        
        salesTable.setItems(sales);
    }
    
    private void addSale() {
        try {
            String date = datePicker.getValue().toString();
            String product = productField.getText();
            double amount = Double.parseDouble(amountField.getText());
            String customer = customerField.getText();
            
            if (product.isEmpty() || customer.isEmpty()) {
                showAlert("Error", "All fields must be filled");
                return;
            }
            
            String[] record = {date, product, String.valueOf(amount), customer};
            DataManager.appendRecord("sales.csv", record);
            
            salesTable.getItems().add(new Sale(date, product, amount, customer));
            clearInputFields();
            
        } catch (NumberFormatException e) {
            showAlert("Error", "Amount must be a valid number");
        }
    }
    
    private void editSale() {
        Sale selectedSale = salesTable.getSelectionModel().getSelectedItem();
        if (selectedSale == null) {
            showAlert("Error", "Please select a sale to edit");
            return;
        }
        
        datePicker.setValue(LocalDate.parse(selectedSale.getDate()));
        productField.setText(selectedSale.getProduct());
        amountField.setText(String.valueOf(selectedSale.getAmount()));
        customerField.setText(selectedSale.getCustomer());
        
        // Remove the selected item and update CSV
        int index = salesTable.getSelectionModel().getSelectedIndex() + 1; // Add 1 to account for header
        salesTable.getItems().remove(selectedSale);
        DataManager.deleteRecord("sales.csv", index);
    }
    
    private void deleteSale() {
        Sale selectedSale = salesTable.getSelectionModel().getSelectedItem();
        if (selectedSale == null) {
            showAlert("Error", "Please select a sale to delete");
            return;
        }
        
        int index = salesTable.getSelectionModel().getSelectedIndex() + 1; // Add 1 to account for header
        salesTable.getItems().remove(selectedSale);
        DataManager.deleteRecord("sales.csv", index);
    }
    
    private void clearInputFields() {
        datePicker.setValue(LocalDate.now());
        productField.clear();
        amountField.clear();
        customerField.clear();
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public static class Sale {
        private String date;
        private String product;
        private double amount;
        private String customer;
        
        public Sale(String date, String product, double amount, String customer) {
            this.date = date;
            this.product = product;
            this.amount = amount;
            this.customer = customer;
        }
        
        public String getDate() { return date; }
        public String getProduct() { return product; }
        public double getAmount() { return amount; }
        public String getCustomer() { return customer; }
        
        public void setDate(String date) { this.date = date; }
        public void setProduct(String product) { this.product = product; }
        public void setAmount(double amount) { this.amount = amount; }
        public void setCustomer(String customer) { this.customer = customer; }
    }
}