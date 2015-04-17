package myexpenses.ng2.com.WalletPocket.Model;

/**
 * Created by Nikos on 7/28/2014.
 * class : represents a user profile
 */
public class UserProfile {

    //username
    private String username;
    //user total savings
    private float savings;
    //user current balance
    private float balance;
    //preferred currency
    private String currency;

    private String grouping;

    //constructor
    public UserProfile(String username, float savings, float balance, String currency, String grouping) {
        this.username = username;
        this.savings = savings;
        this.balance = balance;
        this.currency = currency;
        this.grouping = grouping;
    }

    /**
     * below are the setters and getters for each attributes
     */

    public String getUsername() {
        return username;
    }

    public float getSavings() {
        return savings;
    }

    public void setSavings(float savings) {
        this.savings = savings;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public String getGrouping() {
        return grouping;
    }
}
