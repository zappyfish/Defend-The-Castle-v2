package liamkengineering.defendthecastle;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class GameView extends SurfaceView {

    Shield myShield;
    Data myData;
    Paint mPaint;
    Drawable castle;
    Paint background;

    public GameView(Context context) {
        super(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public GameView(Context context, Activity a) { // use this constructor
        super(context);
        init(a);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0,0,getWidth(), getHeight(), background);
        canvas.drawCircle(getWidth()/2, getHeight()/2, 20, mPaint);
        if(myShield.visible) {
            drawShield(canvas);
        }
    }
    // setup
    public void init(Activity a) {
        setFocusable(true);
        setFocusableInTouchMode(true);
        myData = new Data(a); // see if a shield was selected. if so, set the bitmap appropriately
        SharedPreferences sharedShield = a.getApplicationContext()
                .getSharedPreferences("USE_SHIELD", 0);
        if(sharedShield.contains("shield")) {
            int shieldNum = sharedShield.getInt("shield", 0);
            Bitmap bmp = myData.getBMP(shieldNum);
            myShield = new Shield(20, 0, 0, bmp);
        }
        else {
            myShield = new Shield(20, 0, 0, null);
        }
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        castle = getResources().getDrawable(R.drawable.mycastle);
        background = new Paint();
        background.setColor(Color.WHITE);
    }
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        myShield.setX(e.getX());
        myShield.setY(e.getY());

        switch(e.getAction()) { // draw the shield in the new position if a move or touch down
            case MotionEvent.ACTION_DOWN:
                myShield.visible = true;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                myShield.visible = true;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                myShield.visible = false;
                invalidate();
                break;
        }
        return true;
    }

    public void drawShield(Canvas c) {
        Paint p = new Paint();
        p.setColor(myShield.color);
        c.drawCircle(myShield.getX(), myShield.getY(), myShield.getRad(), p);
        if(myShield.hasShield()) {
            c.drawBitmap(myShield.getBMP(), myShield.getX(), myShield.getY(), null);
        }
    }
}
