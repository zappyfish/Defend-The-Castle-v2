package liamkengineering.defendthecastle;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import eltos.simpledialogfragment.SimpleDialog;
import eltos.simpledialogfragment.color.SimpleColorDialog;

public class DrawShield extends AppCompatActivity implements SimpleDialog.OnDialogResultListener {
    private static final String COLOR_DIALOG = "dialogTagColor";
    private DrawView dv; // where the shield is drawn
    Data d; // for storing bitmaps

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_shield);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton brush = (FloatingActionButton) findViewById(R.id.brush);
        brush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleColorDialog.build().title("Pick a Color").colorPreset(Color.BLACK)
                        .allowCustom(true).show(DrawShield.this, COLOR_DIALOG);
            }
        });
        Button reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dv.reset();
            }
        });
        Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bp = getBitmapFromView(dv);
                if(d.saveBMP(bp)) {
                    Toast t = Toast.makeText(DrawShield.this, "saved!", Toast.LENGTH_SHORT);
                    t.show();
                }
                else {
                    Toast t = Toast.makeText(DrawShield.this, "problem saving", Toast.LENGTH_SHORT);
                    t.show();
                }

            }
        });
        dv =  (DrawView) findViewById(R.id.dv);
        d = new Data(this);

    }
    // use this method to get a bitmap of our drawing. we'll then save it locally so that
    // users can select their shield later
    public Bitmap getBitmapFromView(View view) {
        view.setBackground(getResources().getDrawable(R.drawable.takephoto));
        view.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);

        view.draw(canvas);
        view.setBackground(getResources().getDrawable(R.drawable.back));
        return returnedBitmap;
    }

    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {
        if(which == BUTTON_POSITIVE) {
            if(dialogTag.equals(COLOR_DIALOG)) {
                dv.setPaintColor(extras.getInt(SimpleColorDialog.COLOR));
                return true;
            }
        }
        return false;
    }


}
