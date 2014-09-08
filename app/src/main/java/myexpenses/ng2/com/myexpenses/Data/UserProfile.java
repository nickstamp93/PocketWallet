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

    //constructor
    public UserProfile(String username, float savings, float balance) {
        this.username = username;
        this.savings = savings;
        this.balance = balance;
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


}
