package net.kno3.season.roverruckus.felix.robot.acmedrive;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.trajectory.constraints.DriveConstraints;
import com.qualcomm.hardware.motors.NeveRest20Gearmotor;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

/*
 * Constants shared between multiple drive types.
 */
@Config
public class DriveConstants {

    /*
     * TODO: Tune or adjust the following constants to fit your robot. Note that the non-final
     * fields may also be edited through the dashboard (connect to the robot's WiFi network and
     * navigate to https://192.168.49.1:8080/dash). Make sure to save the values here after you
     * adjust them in the dashboard; **config variable changes don't persist between app restarts**.
     */

    private static final MotorConfigurationType MOTOR_CONFIG =
            MotorConfigurationType.getMotorType(NeveRest20Gearmotor.class);
    private static final double TICKS_PER_REV = MOTOR_CONFIG.getTicksPerRev();

    public static double WHEEL_RADIUS = 2; // in
    public static double GEAR_RATIO = 1; // output (wheel) speed / input (motor) speed
    public static double TRACK_WIDTH = 14.75; // in

    public static DriveConstraints BASE_CONSTRAINTS = new DriveConstraints(30.0, 30.0, Math.PI / 2, Math.PI / 2);

    public static double kV = .0171;
    public static double kA = 0.00116;
    public static double kStatic = 0.045;


    public static double encoderTicksToInches(int ticks) {
        return (WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks / TICKS_PER_REV); /// .885;
    }

    public static double rpmToVelocity(double rpm) {
        return (rpm * GEAR_RATIO * 2 * Math.PI * WHEEL_RADIUS / 60.0 ); //* .885;
    }

    public static double getMaxRpm() {
        return MOTOR_CONFIG.getMaxRPM();
    }
}
