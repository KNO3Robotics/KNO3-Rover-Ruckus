package net.kno3.season.roverruckus.felix.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import net.kno3.robot.Robot;
import net.kno3.util.SimpleColor;

/**
 * Created by robotics on 10/28/2017.
 */

public class Felix extends Robot {
    public static final String DRIVE_FL_KEY = "frontleft";
    public static final String DRIVE_FR_KEY = "frontright";
    public static final String DRIVE_RL_KEY = "rearleft";
    public static final String DRIVE_RR_KEY = "rearright";

    public static final String HOPPER_FLIPPER_LEFT_KEY = "Hopper Flipper Left";
    public static final String HOPPER_FLIPPER_RIGHT_KEY = "Hopper Flipper Right";
    public static final String HOPPER_BAR_KEY = "Hopper Bar";
    public static final String HOPPER_LIFT_KEY = "Hopper Motor";

    public static final String HANG_MOTOR_KEY = "Hang Motor";
    public static final String HANG_SERVO_KEY = "Hang Servo";
    public static final String LAND_SERVO_KEY  = "Land";

    public static final String INTAKE_FLIPPER_KEY = "Intake Flipper";
    public static final String INTAKE_TRANSFER_KEY = "Intake Transfer";
    public static final String INTAKE_SLIDE_MOTOR_KEY = "Intake Slide Motor";
    public static final String TEAM_MARKER_DUMPER_KEY = "Team Marker Dumper";
    public static final String COLLECTOR_KEY = "Collector";

    public static final String IMU_KEY = "imu";



    public Felix(OpMode opMode) {
        super(opMode);

        putSubSystem("drive", new DriveSystem(this));
        putSubSystem("hopper", new HopperSystem(this));
        putSubSystem("intake", new IntakeSystem(this));
        putSubSystem("hang", new HangSystem(this));
        putSubSystem("led", new LEDSystem(this));

    }
}