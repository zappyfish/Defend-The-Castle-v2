package liamkengineering.defendthecastle;

/**
 * Created by Liam on 3/15/2017.
 */

public class SinusoidProjectile extends Projectile {
    //
    private float amp = 0;
    private float[][] CoBMatrix = new float[2][2];
    private float distCovered;
    private float stepDist; // distance moved in the x1 basis direction each translation
    private float xstart, ystart;
    private int xDirec, yDirec;
    private float period;

    public SinusoidProjectile(float rad, int col, float xstart, float ystart, float targetX, float targetY,
                              int tickstoCentre, float amp, float period) {
        super(rad, col, xstart, ystart, targetX, targetY, tickstoCentre);
        setUpCoB(CoBMatrix, getSpeedX(), getSpeedY());
        distCovered = 0;
        stepDist = (float)Math.sqrt(Math.pow(getSpeedX(),2)+Math.pow(getSpeedY(),2));
        this.xstart = xstart;
        this.ystart = ystart;
        this.xDirec = (int)(Math.abs(this.getSpeedX())/this.getSpeedX());
        this.yDirec = (int)(Math.abs(this.getSpeedY())/this.getSpeedY());
        this.amp = amp;
        this.period = period;
    }

    // this method is how sinusoidal projectiles translate. it determines the current ampltiude
    // and distance of the sine wave and then uses CoB to translate into euclidean coordinates
    @Override
    public void translate() {
        //
        float myAmp = this.amp*(float)Math.sin(distCovered*Math.PI/period);
        float[] nonEuclidVector = {distCovered, myAmp};
        float[] euclidVector = MatrixMath.multMatrixAndVector(CoBMatrix, nonEuclidVector, 2);
        setY(ystart + euclidVector[1]);
        setX(xstart + euclidVector[0]);
        distCovered += stepDist;
    }

    private void setUpCoB(float[][] mat, float x, float y) {
        float normalizationFactor = (float)Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
        mat[0][0] = x/normalizationFactor;
        mat[0][1] = -y/normalizationFactor;
        mat[1][0] = y/normalizationFactor;
        mat[1][1] = x/normalizationFactor;
    }


}
