package liamkengineering.defendthecastle;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainScreen extends AppCompatActivity {

    String[] topScoreAr = new String[Data.NUM_TOP_SCORES];
    TextView[] scoreViews = new TextView[5];
    Data d;

    int[] topScores = new int[5];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();

        Intent myIntent = getIntent();
        topScoreAr = myIntent.getStringArrayExtra("top scores");
        if(topScoreAr == null)
        {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        }
        Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start the game here
                Intent i = new Intent(MainScreen.this, GameActivity.class);
                startActivity(i);
            }
        });
        d = new Data(this);
        scoreViews[0] = (TextView)findViewById(R.id.top1);
        scoreViews[1] = (TextView) findViewById(R.id.top2);
        scoreViews[2] = (TextView) findViewById(R.id.top3);
        scoreViews[3] = (TextView) findViewById(R.id.top4);
        scoreViews[4] = (TextView) findViewById(R.id.top5);
        int score = d.getLocalHiscore();
        TextView localScore = (TextView) findViewById(R.id.local_hiscore);
        localScore.setText("Local Hiscore: " + score);

        if(topScoreAr!=null) {
            for(int i = 0; i<5; ++i) {
                if(topScoreAr[i]!=null) {
                    scoreViews[4-i].setText((5-i) + ": " + topScoreAr[i]);
                }
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, Settings.class));
                break;
            case R.id.design:
                startActivity(new Intent(this, DrawShield.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

}
