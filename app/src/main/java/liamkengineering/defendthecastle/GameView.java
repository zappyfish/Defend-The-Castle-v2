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
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

/**
 * TODO: document your custom view class.
 */
public class GameView extends View{

    private Shield myShield;
    private Data myData;
    private Paint mPaint;
    private Paint red, green, white;
    private Drawable castle;
    private Paint background;
    GameActivity gameActivity;
    private float shieldRad;
    private boolean shieldChange = false;
    private float centreX, centreY, centreRad;
    private int health;

    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0,0,getWidth(), getHeight(), background);
        drawCastle(canvas, centreX, centreY, centreRad, mPaint, health, red, green, white);
        if(myShield.visible) {
            drawShield(canvas);
        }

    }
    // setup
    public void init(Context c) {
        Activity a = (Activity) c;
        setFocusable(true);
        setFocusableInTouchMode(true);
        myData = new Data(a); // see if a shield was selected. if so, set the bitmap appropriately
        SharedPreferences sharedShield = a.getApplicationContext()
                .getSharedPreferences("USE_SHIELD", 0);
        if(sharedShield.contains("shield")) {
            int shieldNum = sharedShield.getInt("shield", 0);
            Bitmap bmp = myData.getBMP(shieldNum);
            myShield = new Shield(1, 0, 0, bmp); // init rad to 1. it'll change upon call
            // to onWindowFocusChanged
        }
        else {
            myShield = new Shield(1, 0, 0, null);
        }
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(20);
        castle = getResources().getDrawable(R.drawable.mycastle);
        background = new Paint();
        background.setColor(Color.WHITE);
        red = new Paint();
        red.setColor(Color.RED);
        green = new Paint();
        green.setColor(Color.GREEN);
        white = new Paint();
        white.setColor(Color.WHITE);
        gameActivity = (GameActivity) a;
        health = 5; // castle can be hit 5 times before you lose
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        int w = getWidth();
        int h = getHeight();
        centreX = w/2;
        centreY = h/2;
        centreRad = w/10;
        float shieldSize = (float)(Math.sqrt(Math.pow(w, 2)+ Math.pow(h, 2))/20);
        shieldRad = shieldSize;
        shieldChange = true;
    }
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        myShield.setX(e.getX()+myShield.getRad());
        myShield.setY(e.getY()-myShield.getRad());

        switch(e.getAction()) { // draw the shield in the new position if a move or touch down
            case MotionEvent.ACTION_DOWN:
                myShield.visible = true;
                checkInCentre(myShield, centreX, centreY, centreRad);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                myShield.visible = true;
                checkInCentre(myShield, centreX, centreY, centreRad);
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
        if(shieldChange && myShield!= null) {
            myShield.changeSize(shieldRad);
            shieldChange = false;
        }
        c.drawCircle(myShield.getX(), myShield.getY(), myShield.getRad(), myShield.outline);
        if(myShield.hasShield()) {
            c.drawBitmap(myShield.getBMP(), myShield.getX_center(), myShield.getY_center(), null);
        }
    }

    private void checkInCentre(Shield s, float centreX, float centreY, float centreRad) {
        if(s==null) return;

        float difX = s.getX()-centreX;
        float difY = s.getY()-centreY;
        float totDist = (float)Math.sqrt(Math.pow(difX,2) + Math.pow(difY, 2));
        if(totDist<=(centreRad+s.getRad())) { // if it's in the centre, we want it to be hugging the outside of
            // the centre. To do this, we use some simple geometry i.e. similar triangles
            float distFromCentre = s.getRad() + centreRad;
            float newX = (centreX)+((difX/totDist)*distFromCentre);
            float newY = (centreY)+((difY/totDist)*distFromCentre);
            s.setX(newX);
            s.setY(newY);
        }

    }
    private void drawCastle(Canvas c, float x, float y, float rad, Paint circlePaint, int health,
                            Paint red, Paint green, Paint white) {
        // first draw health circle, then draw cropping rectangle, then draw circle outline
        if(health>2) {
            c.drawCircle(x, y, rad, green);
        }
        else {
            c.drawCircle(x, y, rad, red);
        }
        float bottom = (y-rad)+ ((5-(float)health)/5)*(2*rad);
        c.drawRect(x-rad, y-rad, x+rad, bottom, white);
        c.drawCircle(x, y, rad, circlePaint);
    }
}
