package liamkengineering.defendthecastle;

import android.graphics.Paint;

/**
 * Created by Liam on 3/9/2017.
 */

public class Projectile extends GameObject {

    private float speedX, speedY;
    public Paint pPaint;

    public Projectile(float rad, int col, float xstart, float ystart, float targetX, float targetY,
                      int tickstoCentre) {
        super(rad, col, xstart, ystart);
        getSpeeds(xstart, ystart, targetX, targetY, tickstoCentre);
        pPaint = new Paint();
        pPaint.setColor(col);
    }
    public void translate() {
        setX(getX()+speedX);
        setY(getY()+speedY);
    }

    public float getSpeedX() {
        return speedX;
    }

    public float getSpeedY() {
        return speedY;
    }

    private void getSpeeds(float xstart, float ystart, float targetX, float targetY,
                              int ticksToCentre) {
        float difX = targetX-xstart;
        float difY = targetY-ystart;
        float totDist = (float)Math.sqrt(Math.pow(difX,2)+Math.pow(difY,2));

        float distPerTick = totDist/((float)ticksToCentre);
        float xPerTick = difX*(distPerTick/totDist);
        float yPerTick = difY*(distPerTick/totDist);
        this.speedX = xPerTick;
        this.speedY = yPerTick;
    }
}
