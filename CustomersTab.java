import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.*;

public class CustomersTab extends Tab {
    private TableView<Customer> customersTable;
    private TextField nameField;
    private TextField emailField;
    private TextField phoneField;
    private TextField addressField;
    
    public CustomersTab() {
        setText("Customers");
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        
        // Create input fields
        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);
        
        nameField = new TextField();
        emailField = new TextField();
        phoneField = new TextField();
        addressField = new TextField();
        
        inputGrid.addRow(0, new Label("Name:"), nameField);
        inputGrid.addRow(1, new Label("Email:"), emailField);
        inputGrid.addRow(2, new Label("Phone:"), phoneField);
        inputGrid.addRow(3, new Label("Address:"), addressField);
        
        Button addButton = new Button("Add Customer");
        addButton.setOnAction(e -> addCustomer());
        
        // Create table
        customersTable = new TableView<>();
        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        TableColumn<Customer, String> emailCol = new TableColumn<>("Email");
        TableColumn<Customer, String> phoneCol = new TableColumn<>("Phone");
        TableColumn<Customer, String> addressCol = new TableColumn<>("Address");
        
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        
        customersTable.getColumns().addAll(nameCol, emailCol, phoneCol, addressCol);
        
        // Create edit and delete buttons
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        
        editButton.setOnAction(e -> editCustomer());
        deleteButton.setOnAction(e -> deleteCustomer());
        
        HBox buttonBox = new HBox(10, addButton, editButton, deleteButton);
        
        content.getChildren().addAll(inputGrid, buttonBox, customersTable);
        setContent(content);
        
        // Load initial data
        loadCustomersData();
    }
    
    private void loadCustomersData() {
        List<String[]> records = DataManager.readCSV("customers.csv");
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        
        // Skip header row
        for (int i = 1; i < records.size(); i++) {
            String[] record = records.get(i);
            customers.add(new Customer(
                record[0],
                record[1],
                record[2],
                record[3]
            ));
        }
        
        customersTable.setItems(customers);
    }
    
    private void addCustomer() {
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            showAlert("Error", "All fields must be filled");
            return;
        }
        
        String[] record = {name, email, phone, address};
        DataManager.appendRecord("customers.csv", record);
        
        customersTable.getItems().add(new Customer(name, email, phone, address));
        clearInputFields();
    }
    
    private void editCustomer() {
        Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            showAlert("Error", "Please select a customer to edit");
            return;
        }
        
        nameField.setText(selectedCustomer.getName());
        emailField.setText(selectedCustomer.getEmail());
        phoneField.setText(selectedCustomer.getPhone());
        addressField.setText(selectedCustomer.getAddress());
        
        // Remove the selected item and update CSV
        int index = customersTable.getSelectionModel().getSelectedIndex() + 1; // Add 1 to account for header
        customersTable.getItems().remove(selectedCustomer);
        DataManager.deleteRecord("customers.csv", index);
    }
    
    private void deleteCustomer() {
        Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            showAlert("Error", "Please select a customer to delete");
            return;
        }
        
        int index = customersTable.getSelectionModel().getSelectedIndex() + 1; // Add 1 to account for header
        customersTable.getItems().remove(selectedCustomer);
        DataManager.deleteRecord("customers.csv", index);
    }
    
    private void clearInputFields() {
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        addressField.clear();
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public static class Customer {
        private String name;
        private String email;
        private String phone;
        private String address;
        
        public Customer(String name, String email, String phone, String address) {
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.address = address;
        }
        
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public String getAddress() { return address; }
        
        public void setName(String name) { this.name = name; }
        public void setEmail(String email) { this.email = email; }
        public void setPhone(String phone) { this.phone = phone; }
        public void setAddress(String address) { this.address = address; }
    }
}