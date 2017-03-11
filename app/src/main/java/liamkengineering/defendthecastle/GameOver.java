package liamkengineering.defendthecastle;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GameOver extends AppCompatActivity {

    Data d;
    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent myIntent = getIntent();
        d = new Data(this);
        score = myIntent.getIntExtra("score", 0);
        d.saveLocalHiscore(score);
        TextView gameScore = (TextView) findViewById(R.id.display_score);
        gameScore.setText("You destroyed " + score + " projectiles! Great job.");

        Button playAgain = (Button) findViewById(R.id.play_again);
        playAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GameOver.this, GameActivity.class);
                startActivity(i);
            }
        });

        Button mainMenu = (Button) findViewById(R.id.return_home);
        mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GameOver.this, SplashScreen.class);
                startActivity(i);
            }
        });
        Button submitScore = (Button) findViewById(R.id.submit);
        submitScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(GameOver.this);
                View promptView = layoutInflater.inflate(R.layout.prompt, null);
                AlertDialog.Builder alert = new AlertDialog.Builder(GameOver.this);
                alert.setView(promptView);
                final EditText input = (EditText) promptView.findViewById(R.id.userInput);
                alert.setCancelable(false).setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // submit here
                        String usr = input.getText().toString();
                        d.saveScore(score, usr);
                        Intent intent = new Intent(GameOver.this, SplashScreen.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog alertD = alert.create();
                alertD.show();
            }
        });

    }

}
