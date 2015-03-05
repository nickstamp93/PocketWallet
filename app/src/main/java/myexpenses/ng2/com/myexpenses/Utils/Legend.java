package myexpenses.ng2.com.myexpenses.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Nikos on 4/3/2015.
 */
public class Legend extends View {

    private String text = "default text";
    private int color = Color.RED;
    private int textColor = Color.BLACK;

    public Legend(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public Legend(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        int radius;
        radius = 20;
        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        canvas.drawPaint(paint);

        // Use Color.parseColor to define HTML colors
        paint.setColor(color);
        canvas.drawCircle(canvas.getWidth()/3 - 30, canvas.getHeight() / 2, radius, paint);

        paint.setColor(textColor);
        paint.setTextSize(40);
        canvas.drawText(text, canvas.getWidth()/3, canvas.getHeight() / 2 + radius / 2, paint);

    }

    public void setText(String text) {
        this.text = text;
    }

    public void setColor(int color){
        this.color = color;

    }
    public void setTextColor(int color){
        this.textColor = color;

    }

}
