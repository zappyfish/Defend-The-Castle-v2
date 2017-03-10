package liamkengineering.defendthecastle;

/**
 * Created by Liam on 3/7/2017.
 */

public class GameObject {

    private float radius;
    public int color;
    private float x, y;

    public GameObject(float rad, int col, float xstart, float ystart) {
        this.radius = rad;
        this.color = col;
        this.x = xstart;
        this.y = ystart;
    }

    public static boolean contact(GameObject gmObj1, GameObject gmObj2) {
        float difX = gmObj1.x - gmObj2.x;
        float difY = gmObj1.y - gmObj2.y;
        float dist = (float)Math.sqrt(Math.pow(difX, 2) + Math.pow(difY, 2));
        return(Math.abs(dist) < (gmObj1.radius + gmObj2.radius));
    }
    public float getRad() {
        return radius;
    }
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public void setX(float x) {
        this.x = x;
    }
    public void setY(float y) {
        this.y = y;
    }
    public void setRadius(float newRad) {
        this.radius = newRad;
    }
}
