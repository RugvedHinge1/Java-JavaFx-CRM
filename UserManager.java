import java.util.HashMap;
import java.io.File;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class UserManager {
    private static String currentUser = null;
    private static String currentRole = null;
    private static final HashMap<String, String> userDataPaths = new HashMap<>();
    private static final String USER_SETTINGS_FILE = "user_settings.properties";
    private static Properties userSettings;
    
    public static void setCurrentUser(String username) {
        currentUser = username;
        initializeUserDataPaths(username);
        loadUserSettings(username);
    }

    public static void setUserRole(String role) {
        currentRole = role;
    }

    public static String getCurrentRole() {
        return currentRole;
    }

    public static boolean isAdmin() {
        return "admin".equals(currentRole);
    }
    
    public static String getCurrentUser() {
        return currentUser;
    }
    
    private static void initializeUserDataPaths(String username) {
        if (isAdmin()) {
            // Admin can access all user data
            File dataDir = new File("data");
            File[] userDirs = dataDir.listFiles(File::isDirectory);
            if (userDirs != null) {
                for (File userDir : userDirs) {
                    String user = userDir.getName();
                    if (!user.equals("admin")) {
                        String userPrefix = "user_" + user + "_";
                        userDataPaths.put(user + "_sales", userPrefix + "sales.csv");
                        userDataPaths.put(user + "_deals", userPrefix + "deals.csv");
                        userDataPaths.put(user + "_customers", userPrefix + "customers.csv");
                        userDataPaths.put(user + "_balance", userPrefix + "balance.csv");
                        userDataPaths.put(user + "_todo", userPrefix + "todo.csv");
                    }
                }
            }
        } else {
            // Regular worker access
            String userPrefix = "user_" + username + "_";
            userDataPaths.put("sales", userPrefix + "sales.csv");
            userDataPaths.put("deals", userPrefix + "deals.csv");
            userDataPaths.put("customers", userPrefix + "customers.csv");
            userDataPaths.put("balance", userPrefix + "balance.csv");
            userDataPaths.put("todo", userPrefix + "todo.csv");
            
            // Create user directory if it doesn't exist
            new File("data/" + username).mkdirs();
            
            // Move files to user directory if they exist in root
            for (String filePath : userDataPaths.values()) {
                File oldFile = new File(filePath);
                File newFile = new File("data/" + username + "/" + oldFile.getName());
                if (oldFile.exists() && !newFile.exists()) {
                    oldFile.renameTo(newFile);
                }
            }
        }
    }
    
    public static String getDataFilePath(String fileType) {
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in");
        }
        return userDataPaths.getOrDefault(fileType, null);
    }
    
    private static void loadUserSettings(String username) {
        userSettings = new Properties();
        File settingsFile = new File("data/" + username + "/" + USER_SETTINGS_FILE);
        
        if (settingsFile.exists()) {
            try (FileInputStream in = new FileInputStream(settingsFile)) {
                userSettings.load(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void saveUserSettings(String key, String value) {
        if (currentUser != null && userSettings != null) {
            userSettings.setProperty(key, value);
            try (FileOutputStream out = new FileOutputStream("data/" + currentUser + "/" + USER_SETTINGS_FILE)) {
                userSettings.store(out, "User Settings");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static String getUserSetting(String key) {
        return userSettings != null ? userSettings.getProperty(key) : null;
    }
    
    public static void clearCurrentUser() {
        currentUser = null;
        currentRole = null;
        userDataPaths.clear();
        userSettings = null;
    }
}