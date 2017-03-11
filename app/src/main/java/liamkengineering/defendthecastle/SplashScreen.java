package liamkengineering.defendthecastle;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreen extends AppCompatActivity {

    Data d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);
        d = new Data(this);
        new StartUp().execute(d);
    }

    @Override
    protected void onResume() {
        super.onResume();
        d = new Data(this);
        new StartUp().execute(d);
    }


    private class StartUp extends AsyncTask<Data, Void, String> {
        Data retData;
        @Override
        protected String doInBackground(Data...params) {
            retData = params[0];
            Intent i = new Intent(SplashScreen.this, MainScreen.class);
            retData.retrieveTopFive(i, SplashScreen.this);
            if(retData.topScores == null) return null;
            else return "success";
        }

        @Override
        protected void onPostExecute(String result) {
            /*Intent i = new Intent(SplashScreen.this, MainScreen.class);
            i.putExtra("top scores", retData.topScores);
            startActivity(i);
            /*
            Handler h = new Handler();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashScreen.this, MainScreen.class);
                    i.putExtra("top scores", retData.topScores);
                    startActivity(i);
                }
            };
            h.postDelayed(r, 500);
            */
        }
    }


}
