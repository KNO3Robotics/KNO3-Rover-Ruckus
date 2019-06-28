package net.kno3.season.roverruckus.felix.program.roadrunnertests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.acmerobotics.roadrunner.profile.MotionProfile;
import com.acmerobotics.roadrunner.profile.MotionProfileGenerator;
import com.acmerobotics.roadrunner.profile.MotionState;
import com.acmerobotics.roadrunner.util.NanoClock;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.RobotLog;

import net.kno3.season.roverruckus.felix.program.auto.FelixAuto;
import net.kno3.season.roverruckus.felix.robot.acmedrive.DriveConstants;

import java.util.ArrayList;
import java.util.List;

/*
 * This routine is designed to tune the PID coefficients used by the REV Expansion Hubs for closed-
 * loop velocity control. Although it may seem unnecessary, tuning these coefficients is just as
 * important as the positional parameters. Like the other manual tuning routines, this op mode
 * relies heavily upon the dashboard. To access the dashboard, connect your computer to the RC's
 * WiFi network and navigate to https://192.168.49.1:8080/dash in your browser. Once you've
 * successfully connected, start the program, and your robot will begin moving forward and backward
 * according to a motion profile. Your job is to graph the velocity errors over time and adjust the
 * PID coefficients (it's highly suggested to leave F at its default value) like any normal PID
 * controller. Once you've found a satisfactory set of gains, add them to your drive class init.
 */
@Config
@Autonomous
public class DriveVelocityPIDTuner extends FelixAuto {
    public static PIDCoefficients MOTOR_PID = new PIDCoefficients();
    public static double DISTANCE = 72;

    /*
     * If true, the kV value is computed from the free speed determined by the manufacturer (likely
     * an overestimate of the actual value. If false, the value from DriveConstants.kV is used.
     */
    public static boolean USE_THEORETICAL_KV = true;
    private NanoClock clock;
    private FtcDashboard dashboard;
    private PIDCoefficients currentCoeffs;

    @Override
    public void postInit() {
        super.postInit();
        dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        currentCoeffs = drive.roadrunner.getPIDCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        pidCopy(currentCoeffs, MOTOR_PID);
        dashboard.updateConfig();

        RobotLog.i("Initial motor PID coefficients: " + MOTOR_PID);

        clock = NanoClock.system();

        telemetry.log().add("Ready!");
        telemetry.update();
        telemetry.clearAll();
    }

    @Override
    public void main() {
        if (isStopRequested()) return;

        MotionProfile activeProfile = new MotionProfile();
        boolean movingForwards = false;

        List<Double> lastWheelPositions = null;
        double lastTimestamp = 0;
        double profileStartTimestamp = clock.seconds();

        double maxVel = DriveConstants.rpmToVelocity(DriveConstants.getMaxRpm());
        double kV = USE_THEORETICAL_KV ? (1.0 / maxVel) : DriveConstants.kV;

        while (!isStopRequested()) {
            // update the coefficients if necessary
            //RobotLog.i("Loop Current Coeffs: " + currentCoeffs.kP);
            //RobotLog.i("Loop Motor PID: " + MOTOR_PID.kP);
            if (!pidEquals(currentCoeffs, MOTOR_PID)) {
                RobotLog.i("Updated motor PID coefficients: " + MOTOR_PID);
                pidCopy(MOTOR_PID, currentCoeffs); //currentCoeffs = pidCopy(MOTOR_PID);
                drive.roadrunner.setPIDCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, MOTOR_PID);
            }

            // calculate and set the motor power
            double profileTime = clock.seconds() - profileStartTimestamp;
            double dt = profileTime - lastTimestamp;
            lastTimestamp = profileTime;
            if (profileTime > activeProfile.duration()) {
                // generate a new profile
                movingForwards = !movingForwards;
                MotionState start = new MotionState(movingForwards ? 0 : DISTANCE, 0, 0, 0);
                MotionState goal = new MotionState(movingForwards ? DISTANCE : 0, 0, 0, 0);
                activeProfile = MotionProfileGenerator.generateSimpleMotionProfile(start, goal,
                        DriveConstants.BASE_CONSTRAINTS.maximumVelocity, DriveConstants.BASE_CONSTRAINTS.maximumAcceleration);
                profileStartTimestamp = clock.seconds();
            }
            MotionState motionState = activeProfile.get(profileTime);
            double targetPower = kV * motionState.getV();
            drive.roadrunner.setVelocity(new Pose2d(targetPower, 0, 0));

            List<Double> wheelPositions = drive.roadrunner.getWheelPositions();
            if (lastWheelPositions != null) {
                // compute velocities
                List<Double> syntheticVelocities = new ArrayList<>();
                for (int i = 0; i < wheelPositions.size(); i++) {
                    syntheticVelocities.add((wheelPositions.get(i) - lastWheelPositions.get(i)) / dt);
                }

                // update telemetry
                telemetry.addData("targetVelocity", motionState.getV());
                for (int i = 0; i < syntheticVelocities.size(); i++) {
                    telemetry.addData("velocity" + i, syntheticVelocities.get(i));
                    telemetry.addData("error" + i, motionState.getV() - syntheticVelocities.get(i));
                }
                telemetry.update();
            }
            lastWheelPositions = wheelPositions;
        }
    }

    // TODO: integrate these methods directly into the next Road Runner release
    private static boolean pidEquals(PIDCoefficients coeff1, PIDCoefficients coeff2) {
        return coeff1.kP == coeff2.kP && coeff1.kI == coeff2.kI && coeff1.kD == coeff2.kD;
    }

    private static void pidCopy(PIDCoefficients source, PIDCoefficients dest) {
        dest.kP = source.kP;
        dest.kI = source.kI;
        dest.kD = source.kD;
    }
}
