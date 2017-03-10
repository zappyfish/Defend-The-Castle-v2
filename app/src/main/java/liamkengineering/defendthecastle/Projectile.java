package liamkengineering.defendthecastle;

/**
 * Created by Liam on 3/9/2017.
 */

public class Projectile extends GameObject {

    private float speedX, speedY;

    public Projectile(float rad, int col, float xstart, float ystart) {
        super(rad, col, xstart, ystart);
    }
    private void translate() {
        setX(getX()+speedX);
        setY(getY()+speedY);
    }

    public float getSpeedX() {
        return speedX;
    }

    public float getSpeedY() {
        return speedY;
    }
}
