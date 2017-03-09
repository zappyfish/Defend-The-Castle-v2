package liamkengineering.defendthecastle;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by Liam on 3/7/2017.
 */

public class Shield extends GameObject {

    private Bitmap shield_drawing;
    private boolean noshield = false;
    private int bp_width;
    public boolean visible = false;

    public Shield(float rad, float xstart, float ystart, Bitmap bp) {
        super(rad, Color.WHITE, xstart, ystart); // all shields have white background
        if(bp == null) {
            noshield = true;
        }
        else {
            this.shield_drawing = bp;
            this.bp_width = (int)((2*rad)/Math.sqrt(2)); // rad*2 = diameter
            // and diameter = sqrt(2)*side length of inscribed square
        }
    }
    public boolean hasShield() {
        return !noshield;
    }
    public Bitmap getBMP() {
        return shield_drawing;
    }

}
