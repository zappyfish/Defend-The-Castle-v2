package liamkengineering.defendthecastle;

import android.annotation.SuppressLint;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameActivity extends AppCompatActivity {
    int ind = 0;
    Handler projectileHandler = new Handler();
    Runnable projectileRun = new Runnable() {
        @Override
        public void run() {
            gv.updateProjectileAr();
            gv.invalidate();
            projectileHandler.postDelayed(projectileRun, 10);
        }
    };
    Runnable addProj = new Runnable() {
        @Override
        public void run() {
            if(gv.isProjNull(ind)) {
                // only add a projectile at the index if it's null. In this sense, the timing between
                // adding projectiles is pseudo-random because it depends on which one the user
                // most recently destroyed.
                gv.addProjectile(ind);
            }
            projectileHandler.postDelayed(addProj, 1000);
            ind = (++ind)%10;
            gv.decNumTicks();
        }
    };
    private GameView gv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        gv = new GameView(this);
        setContentView(gv);
        gv.requestFocus();
        projectileHandler.postDelayed(projectileRun, 1000);
        projectileHandler.postDelayed(addProj, 1000);

    }
    @Override
    protected void onResume() {
        super.onResume();
        gv.init(this);
    }


}
