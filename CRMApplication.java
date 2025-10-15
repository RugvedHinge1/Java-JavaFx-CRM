import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

public class CRMApplication extends Application {
    private Stage primaryStage;
    private BorderPane mainLayout;
    private TabPane tabPane;
    private DashboardTab dashboardTab;
    private Button logoutButton;
    private Button themeButton;
    private boolean isDarkTheme = false;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Modern CRM System");
        
        // Initialize data files
        DataManager.initializeFiles();
        
        // Create initial empty scene to avoid null pointer
        Scene initialScene = new Scene(new BorderPane(), 1200, 800);
        primaryStage.setScene(initialScene);
        
        // Show login screen first
        showLoginScreen();
        
        // Set light theme as default
        initialScene.getStylesheets().add(getClass().getResource("light-theme.css").toExternalForm());
    }

    private void showLoginScreen() {
        LoginScreen loginScreen = new LoginScreen();
        Scene loginScene = new Scene(loginScreen, 1200, 800);
        String cssPath = getClass().getResource("styles.css").toExternalForm();
        loginScene.getStylesheets().add(cssPath);

        loginScreen.setOnLogin(() -> {
            // Initialize main layout and show main application
            initializeLayout();
            Scene mainScene = new Scene(mainLayout, 1200, 800);
            mainScene.getStylesheets().add(getClass().getResource("light-theme.css").toExternalForm());
            
            primaryStage.setScene(mainScene);
        });
        
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private void initializeLayout() {
        mainLayout = new BorderPane();
        tabPane = new TabPane();

        // Create top bar with theme and logout buttons
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(10));
        topBar.setSpacing(10);

        themeButton = new Button("Switch Theme");
        themeButton.setOnAction(e -> toggleTheme());

        logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> handleLogout());

        topBar.getChildren().addAll(themeButton, logoutButton);
        mainLayout.setTop(topBar);
        
        // Create tabs for different sections
        dashboardTab = new DashboardTab();
        SalesTab salesTab = new SalesTab();
        DealsTab dealsTab = new DealsTab();
        CustomersTab customersTab = new CustomersTab();
        TodoTab todoTab = new TodoTab();
        
        tabPane.getTabs().addAll(dashboardTab, salesTab, dealsTab, customersTab, todoTab);
        mainLayout.setCenter(tabPane);
        
        // Add tab change listener to update dashboard
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == dashboardTab) {
                dashboardTab.updateCharts();
            }
        });
    }

    private void handleLogout() {
        // Clear remembered credentials and current user data
        UserPreferences.clearCredentials();
        UserManager.clearCurrentUser();
        
        // Show login screen
        showLoginScreen();
    }

    private void toggleTheme() {
        isDarkTheme = !isDarkTheme;
        Scene currentScene = primaryStage.getScene();
        currentScene.getStylesheets().clear();
        String themePath = isDarkTheme ? "dark-theme.css" : "light-theme.css";
        currentScene.getStylesheets().add(getClass().getResource(themePath).toExternalForm());
        themeButton.setText(isDarkTheme ? "Light Theme" : "Dark Theme");
    }

    public static void main(String[] args) {
        launch(args);
    }
}