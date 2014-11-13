package myexpenses.ng2.com.myexpenses.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import myexpenses.ng2.com.myexpenses.R;

/**
 * Created by Nikos on 13/11/2014.
 */
public class PercentView extends View {

    public PercentView (Context context) {
        super(context);
        init();
    }
    public PercentView (Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public PercentView (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    private void init() {
        paintBalance = new Paint();
        paintBalance.setColor(getContext().getResources().getColor(R.color.lime));
        paintBalance.setAntiAlias(true);
        paintBalance.setStyle(Paint.Style.FILL);

        paintExpense = new Paint();
        paintExpense.setColor(getContext().getResources().getColor(R.color.red));
        paintExpense.setAntiAlias(true);
        paintExpense.setStyle(Paint.Style.FILL);



        bgpaint = new Paint();
        bgpaint.setColor(getContext().getResources().getColor(R.color.green));
        bgpaint.setAntiAlias(true);
        bgpaint.setStyle(Paint.Style.FILL);
        rect = new RectF();
    }
    Paint paintBalance;
    Paint paintExpense;
    Paint bgpaint;
    RectF rect;
    float percentageExpense = 0;
    float percentageBalance = 0;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //draw background circle anyway
        int left = 0;
        int width = getWidth();
        int top = 0;
        rect.set(left, top, left+width, top + width);
        canvas.drawArc(rect, -90, 360, true, bgpaint);
        if(percentageBalance!=0) {
            canvas.drawArc(rect, -90, (360*percentageBalance), true, paintBalance);
        }
        if(percentageExpense!=0) {
            canvas.drawArc(rect, -90, (360*percentageExpense), true, paintExpense);
        }
    }
    public void setPercentageBalance(float percentage) {
        this.percentageBalance = percentage / 100;
        invalidate();
    }
    public void setPercentageExpense(float percentage) {
        this.percentageExpense = percentage / 100;
        invalidate();
    }

}