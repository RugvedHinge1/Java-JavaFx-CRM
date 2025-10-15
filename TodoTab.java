import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.util.*;

public class TodoTab extends Tab {
    private TableView<Task> todoTable;
    private TextField taskField;
    private ComboBox<String> priorityComboBox;
    private ComboBox<String> statusComboBox;
    private DatePicker datePicker;
    
    public TodoTab() {
        setText("To-Do List");
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        
        // Create input fields
        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);
        
        datePicker = new DatePicker(LocalDate.now());
        taskField = new TextField();
        
        priorityComboBox = new ComboBox<>();
        priorityComboBox.getItems().addAll("High", "Medium", "Low");
        priorityComboBox.setValue("Medium");
        
        statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Not Started", "In Progress", "Completed");
        statusComboBox.setValue("Not Started");
        
        inputGrid.addRow(0, new Label("Date:"), datePicker);
        inputGrid.addRow(1, new Label("Task:"), taskField);
        inputGrid.addRow(2, new Label("Priority:"), priorityComboBox);
        inputGrid.addRow(3, new Label("Status:"), statusComboBox);
        
        Button addButton = new Button("Add Task");
        addButton.setOnAction(e -> addTask());
        
        // Create table
        todoTable = new TableView<>();
        TableColumn<Task, String> dateCol = new TableColumn<>("Date");
        TableColumn<Task, String> taskCol = new TableColumn<>("Task");
        TableColumn<Task, String> priorityCol = new TableColumn<>("Priority");
        TableColumn<Task, String> statusCol = new TableColumn<>("Status");
        
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        taskCol.setCellValueFactory(new PropertyValueFactory<>("task"));
        priorityCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        todoTable.getColumns().addAll(dateCol, taskCol, priorityCol, statusCol);
        
        // Create edit and delete buttons
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        Button completeButton = new Button("Mark as Completed");
        
        editButton.setOnAction(e -> editTask());
        deleteButton.setOnAction(e -> deleteTask());
        completeButton.setOnAction(e -> markAsCompleted());
        
        HBox buttonBox = new HBox(10, addButton, editButton, deleteButton, completeButton);
        
        content.getChildren().addAll(inputGrid, buttonBox, todoTable);
        setContent(content);
        
        // Load initial data
        loadTodoData();
    }
    
    private void loadTodoData() {
        List<String[]> records = DataManager.readCSV("todo.csv");
        ObservableList<Task> tasks = FXCollections.observableArrayList();
        
        // Skip header row
        for (int i = 1; i < records.size(); i++) {
            String[] record = records.get(i);
            tasks.add(new Task(
                record[0],
                record[1],
                record[2],
                record[3]
            ));
        }
        
        todoTable.setItems(tasks);
    }
    
    private void addTask() {
        String date = datePicker.getValue().toString();
        String task = taskField.getText();
        String priority = priorityComboBox.getValue();
        String status = statusComboBox.getValue();
        
        if (task.isEmpty()) {
            showAlert("Error", "Task description must be filled");
            return;
        }
        
        String[] record = {date, task, priority, status};
        DataManager.appendRecord("todo.csv", record);
        
        ObservableList<Task> items = todoTable.getItems();
        items.add(new Task(date, task, priority, status));
        clearInputFields();
    }
    
    private void editTask() {
        Task selectedTask = todoTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert("Error", "Please select a task to edit");
            return;
        }
        
        datePicker.setValue(LocalDate.parse(selectedTask.getDate()));
        taskField.setText(selectedTask.getTask());
        priorityComboBox.setValue(selectedTask.getPriority());
        statusComboBox.setValue(selectedTask.getStatus());
        
        // Remove the selected item and update CSV
        int index = todoTable.getSelectionModel().getSelectedIndex() + 1; // Add 1 to account for header
        ObservableList<Task> items = todoTable.getItems();
        items.remove(selectedTask);
        DataManager.deleteRecord("todo.csv", index);
    }
    
    private void deleteTask() {
        Task selectedTask = todoTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert("Error", "Please select a task to delete");
            return;
        }
        
        int index = todoTable.getSelectionModel().getSelectedIndex() + 1; // Add 1 to account for header
        ObservableList<Task> items = todoTable.getItems();
        items.remove(selectedTask);
        DataManager.deleteRecord("todo.csv", index);
    }
    
    private void markAsCompleted() {
        Task selectedTask = todoTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert("Error", "Please select a task to mark as completed");
            return;
        }
        
        selectedTask.setStatus("Completed");
        int index = todoTable.getSelectionModel().getSelectedIndex() + 1; // Add 1 to account for header
        
        String[] record = {
            selectedTask.getDate(),
            selectedTask.getTask(),
            selectedTask.getPriority(),
            selectedTask.getStatus()
        };
        
        DataManager.updateRecord("todo.csv", index, record);
        ObservableList<Task> items = todoTable.getItems();
        items.set(index - 1, selectedTask);
    }
    
    private void clearInputFields() {
        datePicker.setValue(LocalDate.now());
        taskField.clear();
        priorityComboBox.setValue("Medium");
        statusComboBox.setValue("Not Started");
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public static class Task {
        private String date;
        private String task;
        private String priority;
        private String status;
        
        public Task(String date, String task, String priority, String status) {
            this.date = date;
            this.task = task;
            this.priority = priority;
            this.status = status;
        }
        
        public String getDate() { return date; }
        public String getTask() { return task; }
        public String getPriority() { return priority; }
        public String getStatus() { return status; }
        
        public void setDate(String date) { this.date = date; }
        public void setTask(String task) { this.task = task; }
        public void setPriority(String priority) { this.priority = priority; }
        public void setStatus(String status) { this.status = status; }
    }
}