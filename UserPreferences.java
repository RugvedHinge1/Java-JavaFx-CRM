import java.io.*;
import java.util.Properties;

public class UserPreferences {
    private static final String PREFERENCES_FILE = "user_preferences.properties";
    private static Properties preferences;
    private static boolean initialized = false;

    private static void initializeIfNeeded() {
        if (!initialized) {
            preferences = new Properties();
            File preferencesFile = new File(PREFERENCES_FILE);
            if (preferencesFile.exists()) {
                try (FileInputStream fis = new FileInputStream(preferencesFile)) {
                    preferences.load(fis);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            initialized = true;
        }
    }

    public static void saveCredentials(String username, String password) {
        initializeIfNeeded();
        preferences.setProperty("remembered_username", username);
        preferences.setProperty("remembered_password", password);
        savePreferences();
    }

    public static void clearCredentials() {
        initializeIfNeeded();
        preferences.remove("remembered_username");
        preferences.remove("remembered_password");
        savePreferences();
    }

    public static String getRememberedUsername() {
        initializeIfNeeded();
        return preferences.getProperty("remembered_username", "");
    }

    public static String getRememberedPassword() {
        initializeIfNeeded();
        return preferences.getProperty("remembered_password", "");
    }

    private static void savePreferences() {
        try (FileOutputStream fos = new FileOutputStream(PREFERENCES_FILE)) {
            preferences.store(fos, "User Preferences");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}