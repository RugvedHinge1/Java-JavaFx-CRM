import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.time.LocalDate;

public class DataManager {
    private static final String USERS_FILE = "data/users.csv";
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    
    private static String getDataFilePath(String fileType) {
        String basePath = "data/" + UserManager.getCurrentUser() + "/";
        return basePath + UserManager.getDataFilePath(fileType);
    }

    public static void initializeFiles() {
        // Create data directory if it doesn't exist
        new File("data").mkdirs();
        
        createFileIfNotExists(USERS_FILE);
        if (UserManager.getCurrentUser() != null) {
            String userDir = "data/" + UserManager.getCurrentUser();
            new File(userDir).mkdirs();
            
            createFileIfNotExists(getDataFilePath("sales"));
            createFileIfNotExists(getDataFilePath("deals"));
            createFileIfNotExists(getDataFilePath("customers"));
            createFileIfNotExists(getDataFilePath("balance"));
            createFileIfNotExists(getDataFilePath("todo"));
        }
    }

    private static void createFileIfNotExists(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
                // Write headers based on file type
                FileWriter writer = new FileWriter(file);
                if (fileName.equals(USERS_FILE)) {
                    writer.write("Username,Password,Email,Role\n");
                } else if (fileName.contains("_sales.")) {
                    writer.write("Date,Product,Amount,Customer\n");
                } else if (fileName.contains("_deals.")) {
                    writer.write("Date,Description,Value,Status\n");
                } else if (fileName.contains("_customers.")) {
                    writer.write("Name,Email,Phone,Address\n");
                } else if (fileName.contains("_balance.")) {
                    writer.write("Date,Description,Credit,Debit,Balance\n");
                } else if (fileName.contains("_todo.")) {
                    writer.write("Date,Task,Priority,Status\n");
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<String[]> readCSV(String fileName) {
        List<String[]> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                records.add(line.split(","));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

    public static void writeCSV(String fileName, List<String[]> records) {
        try (FileWriter writer = new FileWriter(fileName)) {
            for (String[] record : records) {
                writer.write(String.join(",", record) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void appendRecord(String fileName, String[] record) {
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write(String.join(",", record) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateRecord(String fileName, int recordIndex, String[] newRecord) {
        List<String[]> records = readCSV(fileName);
        if (recordIndex >= 0 && recordIndex < records.size()) {
            records.set(recordIndex, newRecord);
            writeCSV(fileName, records);
        }
    }

    public static void deleteRecord(String fileName, int recordIndex) {
        List<String[]> records = readCSV(fileName);
        if (recordIndex >= 0 && recordIndex < records.size()) {
            records.remove(recordIndex);
            writeCSV(fileName, records);
        }
    }

    public static boolean validateUser(String username, String password) {
        boolean isValid = validateUserCredentials(username, password);
        if (isValid) {
            UserManager.setCurrentUser(username);
            UserManager.setUserRole(getUserRole(username));
            initializeFiles();
        }
        return isValid;
    }

    private static boolean validateUserCredentials(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            return false;
        }

        // Check for admin credentials first
        if (username.trim().equals(ADMIN_USERNAME) && password.trim().equals(ADMIN_PASSWORD)) {
            return true;
        }

        List<String[]> users = readCSV(USERS_FILE);
        if (users.isEmpty()) {
            return false;
        }

        try {
            for (int i = 1; i < users.size(); i++) { // Skip header
                String[] user = users.get(i);
                if (user.length >= 2 && user[0].trim().equals(username.trim()) && 
                    user[1].trim().equals(password.trim())) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static String getUserRole(String username) {
        if (username.equals(ADMIN_USERNAME)) {
            return "admin";
        }
        
        List<String[]> users = readCSV(USERS_FILE);
        for (int i = 1; i < users.size(); i++) {
            String[] user = users.get(i);
            if (user.length >= 4 && user[0].equals(username)) {
                return user[3];
            }
        }
        return "worker";
    }

    public static boolean registerUser(String username, String password, String email) {
        // Prevent registration with admin username
        if (username.equals(ADMIN_USERNAME)) {
            return false;
        }

        // Check if username already exists
        List<String[]> users = readCSV(USERS_FILE);
        for (int i = 1; i < users.size(); i++) {
            if (users.get(i)[0].equals(username)) {
                return false; // Username already exists
            }
        }
        
        // Add new user with worker role
        String[] newUser = {username, password, email, "worker"};
        appendRecord(USERS_FILE, newUser);
        return true;
    }

    public static void clearAllUserData() {
        File usersFile = new File(USERS_FILE);
        if (usersFile.exists()) {
            List<String[]> users = new ArrayList<>();
            users.add(new String[]{"Username", "Password", "Email", "Role"});
            writeCSV(USERS_FILE, users);
        }
    }
}