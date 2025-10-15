import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.util.*;

public class DealsTab extends Tab {
    private TableView<Deal> dealsTable;
    private TextField descriptionField;
    private TextField valueField;
    private ComboBox<String> statusComboBox;
    private DatePicker datePicker;
    
    public DealsTab() {
        setText("Deals");
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        
        // Create input fields
        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);
        
        datePicker = new DatePicker(LocalDate.now());
        descriptionField = new TextField();
        valueField = new TextField();
        statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("New", "In Progress", "Closed", "Lost");
        statusComboBox.setValue("New");
        
        inputGrid.addRow(0, new Label("Date:"), datePicker);
        inputGrid.addRow(1, new Label("Description:"), descriptionField);
        inputGrid.addRow(2, new Label("Value:"), valueField);
        inputGrid.addRow(3, new Label("Status:"), statusComboBox);
        
        Button addButton = new Button("Add Deal");
        addButton.setOnAction(e -> addDeal());
        
        // Create table
        dealsTable = new TableView<>();
        TableColumn<Deal, String> dateCol = new TableColumn<>("Date");
        TableColumn<Deal, String> descriptionCol = new TableColumn<>("Description");
        TableColumn<Deal, Double> valueCol = new TableColumn<>("Value");
        TableColumn<Deal, String> statusCol = new TableColumn<>("Status");
        
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        dealsTable.getColumns().addAll(dateCol, descriptionCol, valueCol, statusCol);
        
        // Create edit and delete buttons
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        
        editButton.setOnAction(e -> editDeal());
        deleteButton.setOnAction(e -> deleteDeal());
        
        HBox buttonBox = new HBox(10, addButton, editButton, deleteButton);
        
        content.getChildren().addAll(inputGrid, buttonBox, dealsTable);
        setContent(content);
        
        // Load initial data
        loadDealsData();
    }
    
    private void loadDealsData() {
        List<String[]> records = DataManager.readCSV("deals.csv");
        ObservableList<Deal> deals = FXCollections.observableArrayList();
        
        // Skip header row
        for (int i = 1; i < records.size(); i++) {
            String[] record = records.get(i);
            deals.add(new Deal(
                record[0],
                record[1],
                Double.parseDouble(record[2]),
                record[3]
            ));
        }
        
        dealsTable.setItems(deals);
    }
    
    private void addDeal() {
        try {
            String date = datePicker.getValue().toString();
            String description = descriptionField.getText();
            double value = Double.parseDouble(valueField.getText());
            String status = statusComboBox.getValue();
            
            if (description.isEmpty()) {
                showAlert("Error", "Description must be filled");
                return;
            }
            
            String[] record = {date, description, String.valueOf(value), status};
            DataManager.appendRecord("deals.csv", record);
            
            dealsTable.getItems().add(new Deal(date, description, value, status));
            clearInputFields();
            
        } catch (NumberFormatException e) {
            showAlert("Error", "Value must be a valid number");
        }
    }
    
    private void editDeal() {
        Deal selectedDeal = dealsTable.getSelectionModel().getSelectedItem();
        if (selectedDeal == null) {
            showAlert("Error", "Please select a deal to edit");
            return;
        }
        
        datePicker.setValue(LocalDate.parse(selectedDeal.getDate()));
        descriptionField.setText(selectedDeal.getDescription());
        valueField.setText(String.valueOf(selectedDeal.getValue()));
        statusComboBox.setValue(selectedDeal.getStatus());
        
        // Remove the selected item and update CSV
        int index = dealsTable.getSelectionModel().getSelectedIndex() + 1; // Add 1 to account for header
        dealsTable.getItems().remove(selectedDeal);
        DataManager.deleteRecord("deals.csv", index);
    }
    
    private void deleteDeal() {
        Deal selectedDeal = dealsTable.getSelectionModel().getSelectedItem();
        if (selectedDeal == null) {
            showAlert("Error", "Please select a deal to delete");
            return;
        }
        
        int index = dealsTable.getSelectionModel().getSelectedIndex() + 1; // Add 1 to account for header
        dealsTable.getItems().remove(selectedDeal);
        DataManager.deleteRecord("deals.csv", index);
    }
    
    private void clearInputFields() {
        datePicker.setValue(LocalDate.now());
        descriptionField.clear();
        valueField.clear();
        statusComboBox.setValue("New");
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public static class Deal {
        private String date;
        private String description;
        private double value;
        private String status;
        
        public Deal(String date, String description, double value, String status) {
            this.date = date;
            this.description = description;
            this.value = value;
            this.status = status;
        }
        
        public String getDate() { return date; }
        public String getDescription() { return description; }
        public double getValue() { return value; }
        public String getStatus() { return status; }
        
        public void setDate(String date) { this.date = date; }
        public void setDescription(String description) { this.description = description; }
        public void setValue(double value) { this.value = value; }
        public void setStatus(String status) { this.status = status; }
    }
}