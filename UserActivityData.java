import javafx.beans.property.*;

public class UserActivityData {
    private final StringProperty username;
    private final DoubleProperty totalSales;
    private final IntegerProperty activeDeals;
    private final IntegerProperty customerCount;

    public UserActivityData(String username, double totalSales, int activeDeals, int customerCount) {
        this.username = new SimpleStringProperty(username);
        this.totalSales = new SimpleDoubleProperty(totalSales);
        this.activeDeals = new SimpleIntegerProperty(activeDeals);
        this.customerCount = new SimpleIntegerProperty(customerCount);
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public DoubleProperty totalSalesProperty() {
        return totalSales;
    }

    public IntegerProperty activeDealsProperty() {
        return activeDeals;
    }

    public IntegerProperty customerCountProperty() {
        return customerCount;
    }

    public String getUsername() {
        return username.get();
    }

    public double getTotalSales() {
        return totalSales.get();
    }

    public int getActiveDeals() {
        return activeDeals.get();
    }

    public int getCustomerCount() {
        return customerCount.get();
    }
}