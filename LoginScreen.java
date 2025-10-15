import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.effect.DropShadow;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.stage.Screen;

public class LoginScreen extends StackPane {
    private TextField usernameField;
    private PasswordField passwordField;
    private TextField emailField;
    private Button loginButton;
    private Button signupButton;
    private Button switchButton;
    private Label messageLabel;
    private VBox loginForm;
    private boolean isLoginMode = true;
    private CheckBox rememberMeCheckbox;

    public LoginScreen() {
        // Create dark theme gradient background
        Stop[] stops = new Stop[] {
            new Stop(0, Color.web("#1a1a1a")),
            new Stop(1, Color.web("#2d2d2d"))
        };
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, null, stops);
        Rectangle background = new Rectangle();
        background.setFill(gradient);
        background.widthProperty().bind(this.widthProperty());
        background.heightProperty().bind(this.heightProperty());

        // Make form responsive
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        double formWidth = Math.min(400, screenWidth * 0.8);

        // Create form container
        loginForm = new VBox(20);
        loginForm.setAlignment(Pos.CENTER);
        loginForm.setMaxWidth(formWidth);
        loginForm.setPadding(new Insets(40));
        loginForm.setStyle("-fx-background-color: #333333; -fx-background-radius: 10;");

        // Add drop shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.25));
        shadow.setRadius(20);
        loginForm.setEffect(shadow);

        // Create title
        Label titleLabel = new Label("Welcome Back");
        titleLabel.setFont(Font.font("System", 24));
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

        // Create input fields
        usernameField = createStyledTextField("Username");
        passwordField = createStyledPasswordField();
        emailField = createStyledTextField("Email");
        emailField.setVisible(false);
        emailField.setManaged(false);

        // Create buttons
        loginButton = createStyledButton("Sign In");
        signupButton = createStyledButton("Sign Up");
        signupButton.setVisible(false);
        signupButton.setManaged(false);
        
        switchButton = new Button("Need an account? Sign Up");
        switchButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #4fc3f7; -fx-font-size: 12px;");
        switchButton.setOnAction(e -> toggleMode());

        // Create remember me checkbox
        rememberMeCheckbox = new CheckBox("Remember Me");
        rememberMeCheckbox.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        rememberMeCheckbox.setVisible(true);
        rememberMeCheckbox.setManaged(true);

        // Create message label for feedback
        messageLabel = new Label("");
        messageLabel.setStyle("-fx-text-fill: #ff5252;");

        // Add all elements to form
        loginForm.getChildren().addAll(titleLabel, usernameField, emailField, passwordField, rememberMeCheckbox, loginButton, signupButton, switchButton, messageLabel);

        // Load remembered credentials if they exist
        String rememberedUsername = UserPreferences.getRememberedUsername();
        String rememberedPassword = UserPreferences.getRememberedPassword();
        if (!rememberedUsername.isEmpty() && !rememberedPassword.isEmpty()) {
            usernameField.setText(rememberedUsername);
            passwordField.setText(rememberedPassword);
            rememberMeCheckbox.setSelected(true);
        }

        // Add background and form to stack pane
        this.getChildren().addAll(background, loginForm);
        StackPane.setAlignment(loginForm, Pos.CENTER);

        // Add fade-in animation
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), loginForm);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle("-fx-background-color: #424242; -fx-background-radius: 5; " +
                      "-fx-border-color: #616161; -fx-border-radius: 5; " +
                      "-fx-padding: 10; -fx-font-size: 14px; -fx-text-fill: white;");
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle("-fx-background-color: #424242; -fx-background-radius: 5; " +
                              "-fx-border-color: #4fc3f7; -fx-border-radius: 5; " +
                              "-fx-padding: 10; -fx-font-size: 14px; -fx-text-fill: white;");
            } else {
                field.setStyle("-fx-background-color: #424242; -fx-background-radius: 5; " +
                              "-fx-border-color: #616161; -fx-border-radius: 5; " +
                              "-fx-padding: 10; -fx-font-size: 14px; -fx-text-fill: white;");
            }
        });
        return field;
    }

    private PasswordField createStyledPasswordField() {
        PasswordField field = new PasswordField();
        field.setPromptText("Password");
        field.setStyle("-fx-background-color: #424242; -fx-background-radius: 5; " +
                      "-fx-border-color: #616161; -fx-border-radius: 5; " +
                      "-fx-padding: 10; -fx-font-size: 14px; -fx-text-fill: white;");
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle("-fx-background-color: #424242; -fx-background-radius: 5; " +
                              "-fx-border-color: #4fc3f7; -fx-border-radius: 5; " +
                              "-fx-padding: 10; -fx-font-size: 14px; -fx-text-fill: white;");
            } else {
                field.setStyle("-fx-background-color: #424242; -fx-background-radius: 5; " +
                              "-fx-border-color: #616161; -fx-border-radius: 5; " +
                              "-fx-padding: 10; -fx-font-size: 14px; -fx-text-fill: white;");
            }
        });
        return field;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #4fc3f7; -fx-text-fill: white; " +
                       "-fx-background-radius: 5; -fx-font-size: 14px; -fx-padding: 10 20;");
        button.setPrefWidth(200);
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #03a9f4; -fx-text-fill: white; " +
                                                    "-fx-background-radius: 5; -fx-font-size: 14px; -fx-padding: 10 20;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #4fc3f7; -fx-text-fill: white; " +
                                                   "-fx-background-radius: 5; -fx-font-size: 14px; -fx-padding: 10 20;"));
        return button;
    }

    private void toggleMode() {
        isLoginMode = !isLoginMode;
        emailField.setVisible(!isLoginMode);
        emailField.setManaged(!isLoginMode);
        loginButton.setVisible(isLoginMode);
        loginButton.setManaged(isLoginMode);
        signupButton.setVisible(!isLoginMode);
        signupButton.setManaged(!isLoginMode);
        rememberMeCheckbox.setVisible(isLoginMode);
        rememberMeCheckbox.setManaged(isLoginMode);
        switchButton.setText(isLoginMode ? "Need an account? Sign Up" : "Already have an account? Sign In");
        messageLabel.setText("");
    }

    public void setOnLogin(Runnable onLogin) {
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Please fill in all fields");
                return;
            }

            // Disable login button while processing
            loginButton.setDisable(true);
            messageLabel.setText("Logging in...");

            // Run validation in background thread
            Thread validateThread = new Thread(() -> {
                boolean isValid = DataManager.validateUser(username, password);
                
                // Update UI on JavaFX Application Thread
                javafx.application.Platform.runLater(() -> {
                    if (isValid) {
                        messageLabel.setText("");
                        UserManager.setCurrentUser(username);
                        if (rememberMeCheckbox.isSelected()) {
                            UserPreferences.saveCredentials(username, password);
                        } else {
                            UserPreferences.clearCredentials();
                        }
                        onLogin.run();
                    } else {
                        messageLabel.setText("Invalid username or password");
                        loginButton.setDisable(false);
                    }
                });
            });
            validateThread.setDaemon(true);
            validateThread.start();
        });

        signupButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String email = emailField.getText().trim();

            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                messageLabel.setText("Please fill in all fields");
                return;
            }

            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                messageLabel.setText("Please enter a valid email address");
                return;
            }

            // Disable signup button while processing
            signupButton.setDisable(true);
            messageLabel.setText("Creating account...");
            messageLabel.setStyle("-fx-text-fill: white;");

            // Run registration in background thread
            Thread registerThread = new Thread(() -> {
                boolean isRegistered = DataManager.registerUser(username, password, email);
                
                // Update UI on JavaFX Application Thread
                javafx.application.Platform.runLater(() -> {
                    if (isRegistered) {
                        messageLabel.setText("Registration successful! Please sign in");
                        messageLabel.setStyle("-fx-text-fill: #69f0ae;");
                        toggleMode();
                    } else {
                        messageLabel.setText("Username already exists");
                        messageLabel.setStyle("-fx-text-fill: #ff5252;");
                    }
                    signupButton.setDisable(false);
                });
            });
            registerThread.setDaemon(true);
            registerThread.start();
        });
    }
}