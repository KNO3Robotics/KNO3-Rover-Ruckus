package net.kno3.util;

import android.graphics.Bitmap;
import android.util.Log;

import com.qualcomm.robotcore.util.ReadWriteFile;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by jaxon on 12/12/2016.
 */

public class FileUtil {
    public static void writeJSON(String fileName, JSONObject json) {
        try {
            FileWriter file = new FileWriter("/data/data/" + FtcRobotControllerActivity.instance.getApplicationContext().getPackageName() + "/" + fileName);
            file.write(json.toString());
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject readJSON(String filename) {
        try {
            File f = new File("/data/data/" + FtcRobotControllerActivity.instance.getPackageName() + "/" + filename);
            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new JSONObject(new String(buffer));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeImage(String filename, Bitmap bitmap) {
        try {
            File file = AppUtil.getInstance().getSettingsFile(filename + ".png");
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                //out.flush();
            } catch (Exception e) {
                Log.e("vif", "write image ex", e);
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    Log.e("vif", "write image io ex", e);
                }
            }
        } catch (Exception ex) {
            Log.e("vif", "write image main", ex);
        }
    }
}