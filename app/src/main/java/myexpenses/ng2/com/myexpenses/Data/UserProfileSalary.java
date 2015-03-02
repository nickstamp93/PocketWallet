package myexpenses.ng2.com.myexpenses.Data;

import android.content.Context;
import android.util.Log;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import myexpenses.ng2.com.myexpenses.Utils.SharedPrefsManager;

/**
 * Created by Nikos on 7/29/2014.
 * class : represents a user profile with a standard salary
 */
public class UserProfileSalary extends UserProfile {

    //salary frequency
    private String salFreq;
    //salary
    private float salary;
    //if user can manually add an income
    //days to next payment
    private int daysToNextPayment;
    //next payment date
    private Date nextPaymentDate;
    private Context context;


    //constructor
    public UserProfileSalary(Context context,String username, float savings, float balance, float salary, String salFreq, String npd , String currency) {
        super(username, savings, balance , currency , username);

        this.salary = salary;
        this.salFreq = salFreq;

        this.context = context;

        //set the next payment day
        setNextPaymentDate(npd);
    }

    //calculate days for the next payment date
    public int getDaysToNextPayment() {

        Calendar c = Calendar.getInstance();

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date today = format.parse(format.format(c.getTime()));

            int diffDays = (int) ((nextPaymentDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
            daysToNextPayment = diffDays;
            return diffDays;

        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    //add one salary period to the date parameter that is passed as an argument
    public void simulateOneSalaryPeriod(Date date) {
        //add one week or one month to the current next payment date
        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        c.setTime(date);
        if (salFreq.equals("weekly")) {
            c.add(Calendar.DATE, 7);
        } else {
            c.add(Calendar.MONTH, 1);
        }
        Date newDate = c.getTime();
        String strNewDate = format.format(newDate);
        try {
            nextPaymentDate = format.parse(strNewDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //if the previous salar period is over transact any amount left in the balance to the savings total
        if (getBalance() > 0) {
            makeBalanceToSavingsTransaction();
        }
        //and add a salary to the balance
        addSalaryToBalance(date);
        return;

    }

    //update the money status of the user(if a payment has occured in between)
    public void updateMoneyStatus() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        String todayStr = format.format(c.getTime());
        try {
            Date today = format.parse(todayStr);
            while (!today.before(nextPaymentDate)) {
                simulateOneSalaryPeriod(nextPaymentDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SharedPrefsManager manager = new SharedPrefsManager(context);
        String newNPD = format.format(nextPaymentDate);
        manager.startEditing();
//        manager.setPrefsNpd(newNPD);
        manager.setPrefsBalance(getBalance());
        manager.setPrefsSavings(getSavings());
        manager.commit();
        show();

    }

    //transfer any money left in the balance to the savings total
    public void makeBalanceToSavingsTransaction() {
        setSavings(getSavings() + getBalance());
        setBalance(0);
    }

    //add a salary to the balance
    public void addSalaryToBalance(Date date) {
        MoneyDatabase mdb=new MoneyDatabase(context);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String strDate=format.format(date);
        IncomeItem income=new IncomeItem((double)salary,strDate,"Reqular");
        Log.i("nikos","Salary:"+income.getAmount() + " Date:"+income.getDate());
        try {
            mdb.openDatabase();
            mdb.InsertIncome(income);
            mdb.close();
            setBalance(getBalance() + salary);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //for debugging
    public void show() {
        Log.i("nikos", "Username:" + getUsername());
        Log.i("nikos", "Savings:" + getSavings());
        Log.i("nikos", "Balance:" + getBalance());
        Log.i("nikos", "Salary:" + getSalary());
        Log.i("nikos", "Frequency:" + getSalFreq());
        Log.i("nikos", "NPD:" + getNextPaymentDate());
    }

    /**
     * below are setters and getters for each attribute
     */

    public String getSalFreq() {
        return salFreq;
    }

    public void setSalFreq(String salFreq) {
        this.salFreq = salFreq;
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public Date getNextPaymentDate() {
        return nextPaymentDate;
    }

    public void setNextPaymentDate(Date nextPaymentDate) {
        this.nextPaymentDate = nextPaymentDate;
    }

    public void setNextPaymentDate(String npd) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            nextPaymentDate = format.parse(npd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
