package myexpenses.ng2.com.WalletPocket.Extra;

/**
 * Created by Nikos on 7/3/2015.
 */
public class MagnificentChartItem {

// #MARK - Constants

    public int color;
    public double value;
    public String title;

// #MARK - Constructors

    public MagnificentChartItem(String title, double value, int color){
        this.color = color;
        this.value = value;
        this.title = title;
    }

}