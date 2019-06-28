package net.kno3.robot;

import com.qualcomm.robotcore.util.ReadWriteFile;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Created by robotics on 10/28/2017.
 */

public class RobotSettings {
    private String filename;
    private JSONObject dataSto;

    public RobotSettings(String name) {
        this.filename = "RobotSettings" + name + ".json";
        try {
            this.dataSto = (JSONObject) new JSONParser().parse(
                    ReadWriteFile.readFile(
                            AppUtil.getInstance().getSettingsFile(filename)
                    )
            );
        } catch(Exception ex) {
            ex.printStackTrace();
            this.dataSto = new JSONObject();
        }
    }

    public void save() {
        ReadWriteFile.writeFile(AppUtil.getInstance().getSettingsFile(filename), dataSto.toString());
    }

    public double getDouble(String key) {
        try {
            return (Double) this.dataSto.get(key);
        } catch (Exception ex) {
            return 0;
        }
    }

    public void setDouble(String key, double val) {
        try {
            this.dataSto.put(key, val);
        } catch (Exception ex) {}
    }

    public int getInt(String key) {
        try {
            return (Integer) this.dataSto.get(key);
        } catch (Exception ex) {
            return 0;
        }
    }

    public void setInt(String key, int val) {
        try {
            this.dataSto.put(key, val);
        } catch (Exception ex) {}
    }
}
