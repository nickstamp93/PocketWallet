package com.ngngteam.pocketwallet.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.text.TextPaint;
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
    private Paint boxPaintIncome;
    private Paint textPaintExpense;
    private Paint textPaintIncome;
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

        textPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        boxPaintExpense = new Paint();
        boxPaintIncome = new Paint();
        textPaintExpense = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        textPaintIncome = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        textPaint.setColor(getResources().getColor(R.color.bpRed));
        textPaint.setTextSize(14 * scaleFactor);
        boxPaintExpense.setColor(getResources().getColor(R.color.bpRed));
        boxPaintIncome.setColor(getResources().getColor(R.color.YellowGreen));
        textPaintExpense.setColor(getResources().getColor(R.color.bpRed));
        textPaintIncome.setColor(getResources().getColor(R.color.YellowGreen));
        textPaintExpense.setTextSize(14 * scaleFactor);
        textPaintIncome.setTextSize(14 * scaleFactor);

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
        int padding = (int) (15 * scaleFactor);
        int fullWidth = getWidth();
        int fullHeight = getHeight();
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
        canvas.drawRect(padding * 2, incomeBarTop, middle - padding, incomeBarBottom, boxPaintIncome);
        canvas.drawText(context.getString(R.string.action_income), quarter - padding, fullHeight - padding, textPaintIncome);
        canvas.drawText(income + " " +
                PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_key_currency), "€")
                , quarter - padding, incomeValuePos, textPaintIncome);

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
