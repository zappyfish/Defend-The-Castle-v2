package liamkengineering.defendthecastle;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Liam on 3/7/2017.
 */

public class Shield extends GameObject {

    private Bitmap shield_drawing;
    private boolean noshield = false;
    private int bp_width;
    public boolean visible = false;
    private float x_center, y_center;
    public Paint outline;
    private Bitmap originalBP;
    private boolean off_center;

    public Shield(float rad, float xstart, float ystart, Bitmap bp, boolean off_center) {
        super(rad, Color.WHITE, xstart, ystart); // all shields have white background
        this.originalBP = bp;
        this.bp_width = (int)((2*rad)/Math.sqrt(2)); // rad*2 = diameter
        // and diameter = sqrt(2)*side length of inscribed square
        if(bp == null) {
            noshield = true;
        }
        else {
            this.shield_drawing = bp;
            this.shield_drawing = Bitmap.createScaledBitmap(this.shield_drawing, this.bp_width, this.bp_width, true);
        }
        outline = new Paint();
        outline.setStrokeWidth(10);
        outline.setColor(Color.BLACK);
        outline.setStyle(Paint.Style.STROKE);
        this.off_center = off_center;

    }
    public boolean hasShield() {
        return !noshield;
    }
    public Bitmap getBMP() {
        return shield_drawing;
    }
    // this gets where the bitmap should actually be drawn by accounting for the fact that bitmap
    // is drawn not at the center, but rather at the top left
    public float getX_center() {
        if(this.shield_drawing!=null) return getX()-this.bp_width/2; // account for bitmap
        else return getX();
    }
    // same as getX_center(), but for y
    public float getY_center() {
        if(this.shield_drawing!=null) return getY()-this.bp_width/2; // account for bitmap
        else return getY();
    }
    public void changeSize(float newRad) {
        setRadius(newRad);
        if(this.shield_drawing!=null) {
            this.bp_width = (int)((2*newRad)/Math.sqrt(2)); // rad*2 = diameter
            // and diameter = sqrt(2)*side length of inscribed square
            this.shield_drawing = Bitmap.createScaledBitmap(this.originalBP, this.bp_width,
                    this.bp_width, true);
        }
    }
    public float getBPWidth() {
        return this.bp_width;
    }

    @Override
    public void setX(float x) {
        if(this.off_center) {
            super.setX(x);
        }
        else {
            super.setX(x+getRad());
        }
    }
    @Override
    public void setY(float y) {
        if(this.off_center) {
            super.setY(y);
        }
        else {
            super.setY(y-getRad());
        }
    }

    public void setXnoOffset(float x) {
        super.setX(x);
    }
    public void setYnoOffset(float y) {
        super.setY(y);
    }
}
