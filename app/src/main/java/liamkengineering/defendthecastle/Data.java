package liamkengineering.defendthecastle;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
    //Activity a; // for debugging purposes only

    public Data(Activity a) {
        //this.a = a; // for debugging
        shields = a.getApplicationContext().getSharedPreferences(PREFS_NAME,0);
        if(shields.contains(NUMSHIELDS))
        numBMPs = shields.getInt(NUMSHIELDS, 0);
        else {
            numBMPs = 0;
        }
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
}
