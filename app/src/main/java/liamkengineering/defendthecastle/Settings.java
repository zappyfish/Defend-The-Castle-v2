package liamkengineering.defendthecastle;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    private int selectionID; // id of the displayed shield
    private TableLayout shields;
    private Data d;
    private boolean displayDelete = false; // when a shield is selected, this becomes true so that
    // the delete button becomes available

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        d = new Data(this);
        int num_rows = d.numBMPs/3 + (d.numBMPs % 3 > 0 ? 1: 0); // 3 shields displayed per row
        TableRow[] rows = new TableRow[num_rows];
        for(int i = 0; i<num_rows; ++i) { // set up the rows
            rows[i] = new TableRow(this);
            rows[i].setOrientation(LinearLayout.HORIZONTAL);
            rows[i].setLayoutParams(new LinearLayout.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        }
        Display display = getWindowManager().getDefaultDisplay();
        for(int i = 0; i < d.numBMPs; ++i) { // create each of the images and add them to
            // the rows
            ImageView img = new ImageView(this);
            Bitmap bmp = d.getBMP(i);
            if(bmp == null) {
                Toast.makeText(Settings.this, "uh oh! something went wrong" +
                        "loading your shields", Toast.LENGTH_SHORT).show();
            }
            else {
                bmp = Bitmap.createScaledBitmap(bmp, display.getWidth()/3, display.getWidth()/3, true);
                img.setImageBitmap(bmp);
                img.setId(i); // need to set the id for selection
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setSelection(view.getId());
                    }
                });
                rows[i / 3].addView(img);
            }
        }
        shields = (TableLayout) findViewById(R.id.shield_select); // our lin layout that holds
        // all of the rows. add the rows to it to display them
        for(int i = 0; i<num_rows; ++i) {
            shields.addView(rows[i]);
        }
        // add onclicklistener for delete button here
        Button delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteShield(selectionID);
            }
        });
        Button select = (Button) findViewById(R.id.use_shield);
        final Activity a = this;
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedShield = a.getApplicationContext()
                        .getSharedPreferences("USE_SHIELD", 0);
                SharedPreferences.Editor editor = sharedShield.edit();
                editor.putInt("shield", selectionID);
                editor.commit(); // put the selection ID in the shared preferences so we know which
                // one we are using. Then, go back to the main screen
                Intent i = new Intent(Settings.this, MainScreen.class);
                startActivity(i);
            }
        });
    }

    public void setSelection(int id) {
        ImageView sel = (ImageView) findViewById(R.id.selection);
        sel.setImageBitmap(d.getBMP(id));
        sel.setBackground(getResources().getDrawable(R.drawable.back)); // give it a nice frame
        if(!displayDelete) { // once a shield is selected, set the visiblity for use and delete
            displayDelete = true;
            findViewById(R.id.delete).setVisibility(View.VISIBLE);
            findViewById(R.id.use_shield).setVisibility(View.VISIBLE);
        }
        selectionID = id; // used for deleting/selecting the correct shield
    }

    public void deleteShield(int id) {
        final int deleteID = id;
        DialogInterface.OnClickListener dialog = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch(i) {
                    case DialogInterface.BUTTON_POSITIVE:
                        d.deleteBMP(deleteID);
                        finish();
                        startActivity(getIntent());
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to delete?");
        builder.setPositiveButton("yes", dialog).setNegativeButton("no", dialog).show();

    }


    // Runescape is best game ever
}
