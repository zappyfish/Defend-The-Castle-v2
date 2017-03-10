package liamkengineering.defendthecastle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

/**
 * TODO: document your custom view class.
 */
public class GameView extends View{

    private Shield myShield; // shield object for the view
    private Data myData; // for getting bitmaps
    private Paint mPaint; // for circle surrounding the castle in the center
    private Paint red, green, white; // different paints
    private Drawable castle;
    private Paint background;
    GameActivity gameActivity;
    private float shieldRad;
    private boolean shieldChange = false; // indicates whether the shield has been resized recently
    private float centreX, centreY, centreRad; // centre of the screen + radius of castle in the center
    private int health; // castle health
    private final int NUM_PROJ = 10;
    private Projectile[] projectileAr = new Projectile[NUM_PROJ]; // max of 10 at any given time
    private int NUM_TICKS = 200;
    Random rand = new Random();
    public int activeProjectiles = 0;

    private final int MAX_HEALTH = 8;
    private GameObject centerObject;
    private int numDestroyed;
    Paint scorePaint;
    private int MIN_TICKS = 100; // the minimum number of ticks required to reach the center

    Handler gameOverHandler = new Handler();
    Runnable gameOverRun = new Runnable() {
        @Override
        public void run() {
            gameOver();
        }
    };

    private boolean gameOver = false;
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
        canvas.drawRect(0, 0, getWidth(), getHeight(), background);
        drawCastle(canvas, centreX, centreY, centreRad, mPaint, health, red, green, white);
        checkForContact(); // check for contact b/w shield and projectiles before drawing
        checkForDamage(); // check to see if projectiles reached the center
        for (int i = 0; i < NUM_PROJ; ++i) {
            drawProjectile(canvas, projectileAr[i]);
        }
        if (myShield.visible) {
            drawShield(canvas);
        }
        displayPoints(canvas);
        if(gameOver) {
            gameOverHandler.postDelayed(gameOverRun, 500);
            gameOver = false;
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
        health = MAX_HEALTH;
        numDestroyed = 0;
        scorePaint = new Paint();
        scorePaint.setColor(Color.BLACK);
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
        centerObject = new GameObject(centreRad, 0, w/2, h/2);
        scorePaint.setTextSize(getWidth()/10);
        castle.setBounds((int)(centreX-centreRad/2), (int)(centreY-centreRad/2),
                (int)(centreX+centreRad/2), (int)(centreY+centreRad/2));
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
        float bottom = (y-rad)+ ((MAX_HEALTH-(float)health)/MAX_HEALTH)*(2*rad); // bottom of cropping rectangle
        // we start from the top, and then find the amount of damag done (i.e. maxhealt-current health)
        // and multiply this by the diameter of the circle (i.e. the height of the rectangle) to get the
        // proportional height of the rectangle
        c.drawRect(x-rad, y-rad, x+rad, bottom, white);
        c.drawCircle(x, y, rad, circlePaint);
        if(c!=null)castle.draw(c);
    }
    private void drawProjectile(Canvas c, Projectile p) {
        if(p!=null) {
            c.drawCircle(p.getX(), p.getY(), p.getRad(), p.pPaint);
        }
    }
    public void updateProjectileAr() {
        for(int i = 0; i< NUM_PROJ; ++i) {
            if(projectileAr[i]!=null) {
                projectileAr[i].translate();
            }
        }
    }
    public void addProjectile(int index) {
        if(index >= 0 && index<NUM_PROJ) {
            projectileAr[index] = newProjectile();
            ++activeProjectiles;
        }
    }
    // this method creates and returns a new projectile. It randomly selects a size and then
    // randomly selects a starting location for the projectile.
    private Projectile newProjectile() {
        float rad = ((rand.nextFloat()/2)+(float)0.5)*(getWidth()/20);
        boolean x_or_y = rand.nextBoolean(); // whether the new projectile starts at the left/right
        //edges of the screen or the top/bottom edges
        float x, y;
        if(x_or_y) {
            x = (rand.nextBoolean() ? 0: getWidth()); // either the left side or the right side
            y = getHeight()*rand.nextFloat();
        }
        else {
            y = (rand.nextBoolean() ? 0: getHeight()); // top or bottom
            x = getWidth()*rand.nextFloat();
        }
        Projectile p = new Projectile(rad, Color.BLACK, x, y, centreX, centreY, NUM_TICKS);
        return p;
    }
    // loop through all of the projectiles checking for contact b/w the projectile and the shield.
    // if there is contact, then "delete" that projectile and reduce the number of active projectiles
    private void checkForContact() {
        if(myShield.visible) { // only makes sense to check if the shield is actually visible
            for (int i = 0; i < NUM_PROJ; ++i) {
                if (projectileAr[i] != null) {
                    if (GameObject.contact(myShield, projectileAr[i])) {
                        projectileAr[i] = null;
                        --activeProjectiles;
                        ++numDestroyed;
                    }
                }
            }
        }
    }
    public boolean isProjNull(int ind) {
        return projectileAr[ind] == null;
    }
    private void checkForDamage() {
        for(int i = 0; i<NUM_PROJ; ++i) {
            if(projectileAr[i]!=null) {
                if(GameObject.contact(centerObject, projectileAr[i])) {
                    projectileAr[i] = null;
                    --health;
                    if(health == 0) {
                        gameOver = true;
                        break;
                    }
                }
            }
        }
    }
    private void gameOver() {
        Intent i = new Intent(gameActivity, GameOver.class);
        i.putExtra("score", numDestroyed);
        gameActivity.startActivity(i);
    }

    public void decNumTicks() {
        if(NUM_TICKS>MIN_TICKS) {
            --NUM_TICKS;
        }
    }
    public void displayPoints(Canvas c) {
        float x = scorePaint.measureText("Score: " + numDestroyed); // get the width of the text
        x = getWidth()/2 - x/2; // shift to the left by the width of the text/2 to center
        c.drawText("Score: " + numDestroyed, x, getHeight()/8, scorePaint);
    }
}
