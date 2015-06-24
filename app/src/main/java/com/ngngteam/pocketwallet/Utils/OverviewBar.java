package com.ngngteam.pocketwallet.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.ngngteam.pocketwallet.R;

/**
 * Created by Nikos on 19/6/2015.
 */
public class OverviewBar extends View {

    private Paint textPaint;
    private Paint boxPaintExpense;
    private Paint boxPaingIncome;
    private Paint textPaintExpense;
    private Paint textPaingIncome;
    private double expense;
    private double income;
    private float scaleFactor;

    private Context context;

    public OverviewBar(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public OverviewBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public OverviewBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    void init() {

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        scaleFactor = metrics.density;

        textPaint = new Paint();
        boxPaintExpense = new Paint();
        boxPaingIncome = new Paint();
        textPaintExpense = new Paint();
        textPaingIncome = new Paint();
        textPaint.setColor(getResources().getColor(R.color.bpRed));
        textPaint.setTextSize(14 * scaleFactor);
        boxPaintExpense.setColor(getResources().getColor(R.color.bpRed));
        boxPaingIncome.setColor(getResources().getColor(R.color.YellowGreen));
        textPaintExpense.setColor(getResources().getColor(R.color.bpRed));
        textPaingIncome.setColor(getResources().getColor(R.color.YellowGreen));
        textPaintExpense.setTextSize(14 * scaleFactor);
        textPaingIncome.setTextSize(14 * scaleFactor);

    }

    // Earnings and expense are calculated values based on user inputs.
    // The Main app Activity calculates these and calls the below methods
    // when the user inputs a new value

    public void setExpense(double value) {
        expense = value;
        invalidate();
    }

    public void setIncome(double value) {
        income = value;
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int fullWidth = getWidth();
        int fullHeight = getHeight();
        int padding = (int) (10 * scaleFactor);
        int maxBarHeight = fullHeight - 5 * padding;
        float incomeBarHeight;
        float expenseBarHeight;

        if (expense > income) {
            expenseBarHeight = (float) maxBarHeight;
            incomeBarHeight = (float) (income / expense * maxBarHeight);
        } else {
            incomeBarHeight = (float) maxBarHeight;
            expenseBarHeight = (float) (expense / income * maxBarHeight);
        }

        float middle = (float) (fullWidth * 0.5);
        float quarter = (float) (fullWidth * 0.25);
        float threequarter = (float) (fullWidth * 0.75);

        int incomeBarBottom = fullHeight - padding * 3;
        float incomeBarTop = incomeBarBottom - incomeBarHeight;
        float incomeValuePos = incomeBarTop - padding;
        canvas.drawRect(padding * 2, incomeBarTop, middle - padding, incomeBarBottom, boxPaingIncome);
        canvas.drawText(context.getString(R.string.action_income), quarter - padding, fullHeight - padding, textPaingIncome);
        canvas.drawText(income + " " +
                PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_key_currency), "€")
                , quarter - padding, incomeValuePos, textPaingIncome);

        int expenseBarBottom = fullHeight - padding * 3;
        float expenseBarTop = expenseBarBottom - expenseBarHeight;
        float expenseValuePos = expenseBarTop - padding;
        canvas.drawRect(middle + padding, expenseBarTop, fullWidth - padding * 2, expenseBarBottom, boxPaintExpense);
        canvas.drawText(context.getString(R.string.action_expense), threequarter - padding * 3, fullHeight - padding, textPaintExpense);
        canvas.drawText(expense + " " +
                PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_key_currency), "€")
                , threequarter - padding * 2, expenseValuePos, textPaintExpense);
    }

}
