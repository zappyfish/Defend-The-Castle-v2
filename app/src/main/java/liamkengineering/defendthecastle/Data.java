package liamkengineering.defendthecastle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static java.lang.System.out;
import static liamkengineering.defendthecastle.R.id.settings;

/**
 * Created by Liam on 3/6/2017.
 * This class is for saving and retrieving bitmap data
 */

public class Data extends Activity {

    public int numBMPs; // keep track of number of BMPs
    private final String PREFS_NAME = "SHIELD";
    private final String NUMSHIELDS = "NUMSHIELDS";
    private final String FILENAME = "/shields/";
    SharedPreferences shields;
    Activity a; // for debugging purposes only
    private final String LOCAL_HISCORE = "LOCAL_HISCORE";
    private final String LOCAL = "LOCAL";
    SharedPreferences localHiscore;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase;
    private final String SCORE_KEY = "TOP_SCORES";
    public String[] topScores;
    public static final int NUM_TOP_SCORES = 5;
    private final String CENTER_FINGER = "CENTERORNOT";
    private final String STATE_FINGER = "center";
    SharedPreferences center;


    public Data(Activity a) {
        this.a = a; // for debugging
        shields = a.getApplicationContext().getSharedPreferences(PREFS_NAME,0);
        if(shields.contains(NUMSHIELDS))
        numBMPs = shields.getInt(NUMSHIELDS, 0);
        else {
            numBMPs = 0;
        }
        localHiscore = a.getApplicationContext().getSharedPreferences(LOCAL_HISCORE, 0);
        mDatabase = database.getReference(SCORE_KEY);
        topScores = new String[NUM_TOP_SCORES];
        center = a.getApplicationContext().getSharedPreferences(CENTER_FINGER, 0);
    }
    // this method saves a bitmap of the shield to the SD card. it returns true for success,
    // false for failure
    public boolean saveBMP(Bitmap bp) {
        if(bp == null) return false;
        String path = Environment.getExternalStorageDirectory().toString();
        File file = new File(path, FILENAME);
        // ex: path/shields/0.png
        try {
            File f = new File(file, numBMPs + ".png");
            FileOutputStream out = new FileOutputStream(f);
            bp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        }catch(FileNotFoundException e) {
            file.mkdirs();
            try {
                File f = new File(file, numBMPs + ".png");
                FileOutputStream out = new FileOutputStream(f);
                bp.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.close();
            } catch(IOException excp) {
                return false;
            }
        }catch(IOException e) {
            return false;
        }
        ++numBMPs;
        SharedPreferences.Editor editor = shields.edit();
        editor.putInt(NUMSHIELDS, numBMPs);
        editor.commit();
        return true;
    }
    // returns the shield of the specified number
    public Bitmap getBMP(int num) {
        if(num<0 || num>=numBMPs) {
            return null;
        }
        File dir = Environment.getExternalStorageDirectory();
        File file = new File(dir, FILENAME + num + ".png");
        Bitmap bp = BitmapFactory.decodeFile(file.getAbsolutePath());
        return bp;
    }
    // this methord deletes a bitmap (used for deleting shields) and renames the last shield
    // to the deleted shield so that they can still be displayed properly in settings
    public boolean deleteBMP(int num) {
        if(num < 0 || num>=numBMPs) {
            return false;
        }
        File dir = Environment.getExternalStorageDirectory();
        File file = new File(dir, FILENAME + num + ".png");
        boolean success = file.delete(); // try to delete it here
        if(!success) { // there was some problem deleting
            return false;
        }
        --numBMPs;
        SharedPreferences.Editor editor = shields.edit();
        editor.putInt(NUMSHIELDS, numBMPs);
        editor.commit();
        if(num == numBMPs) return true; // we don't need to rename anything if we deleted the last
        // file
        File rename = new File(dir, FILENAME + numBMPs + ".png");
        rename.renameTo(file); // rename the last file to the deleted file
        return true;
    }

    public boolean saveLocalHiscore(int score) {
        int current_hiscore = localHiscore.getInt(LOCAL, 0);
        if(score>current_hiscore) {
            SharedPreferences.Editor editor = localHiscore.edit();
            editor.putInt(LOCAL, score);
            editor.commit();
            return true;
        }
        return false;
    }
    public int getLocalHiscore() {
        return localHiscore.getInt(LOCAL, 0);
    }

    public boolean saveScore(int score, String username) {
        if(username == null) {
            return false;
        }

        Map<String, Object> entry = new HashMap<String, Object>();
        entry.put("name", username);
        entry.put("score", score);
        mDatabase.push().setValue(entry);
        return true;

    }

    public int getCenterFinger() {
        return center.getInt(STATE_FINGER, 0);
    }

    public void setCenterFinger(int set) {
        SharedPreferences.Editor editor = center.edit();
        editor.putInt(STATE_FINGER, set);
        editor.commit();
    }

    public void retrieveTopFive(Intent i, Activity a) {
        final PassData pd = new PassData(i, a);
        Query queryRef = mDatabase.orderByChild("score").limitToLast(NUM_TOP_SCORES);
        queryRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int counter = 0;
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    long score = (long)postSnapshot.child("score").getValue();
                    String user = (String)postSnapshot.child("name").getValue();
                    topScores[counter] = user+ ", " + score + " pts";
                    ++counter;
                }
                pd.start(topScores, "top scores");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                topScores = null; // some sort of error
                pd.start(null, "top scores");
            }
        });

    }

    public class PassData {

        Intent intent;
        Activity activity;

        public PassData(Intent intent, Activity a) {
            this.intent = intent;
            this.activity = a;
        }

        public void start(String[] data, String key) {
            intent.putExtra(key, data);
            activity.startActivity(intent);
        }
    }

}
