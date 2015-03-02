package myexpenses.ng2.com.myexpenses.Data;

/**
 * Created by Nikos on 7/28/2014.
 * class : represents a user profile without a standard salary
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
    public UserProfile(String username, float savings, float balance , String currency , String grouping) {
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

    public void setUsername(String username) {
        this.username = username;
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

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getGrouping() {
        return grouping;
    }

    public void setGrouping(String grouping) {
        this.grouping = grouping;
    }
}
