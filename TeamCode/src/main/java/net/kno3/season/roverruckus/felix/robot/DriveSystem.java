package net.kno3.season.roverruckus.felix.robot;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import net.kno3.robot.Robot;
import net.kno3.robot.SubSystem;
import net.kno3.season.roverruckus.felix.robot.acmedrive.SampleTankDriveREV;
import net.kno3.util.*;

import org.firstinspires.ftc.robotcontroller.internal.FtcOpModeRegister;

import java.util.function.Supplier;

public class DriveSystem extends SubSystem{
    public MotorPair left, right;
    public double kDriveFor_p, kDriveFor_i, kDriveFor_d, kDriveFor_maxSpeed;
    public double kTurnPID_p, kTurnPID_i, kTurnPID_d,kTurnPID_maxTurn;
    public IAdafruitIMU imu;
    public double imuZeroHeading;

    private boolean reverse = false, slow = true, slowReverse = false;


    public SampleTankDriveREV roadrunner;


    public DriveSystem(Robot robot) {
        super(robot);
        kDriveFor_maxSpeed = robot.settings.getDouble("kDriveFor_maxSpeed");
        kDriveFor_p = robot.settings.getDouble("kDriveFor_p");
        kDriveFor_i = robot.settings.getDouble("kDriveFor_i");
        kDriveFor_d = robot.settings.getDouble("kDriveFor_d");
        kTurnPID_p = robot.settings.getDouble("kTurnPID_p");
        kTurnPID_i = robot.settings.getDouble("kTurnPID_i");
        kTurnPID_d = robot.settings.getDouble("kTurnPID_d");
        kTurnPID_maxTurn = robot.settings.getDouble("kTurnPID_maxTurn");

    }

    private boolean startLockout = false;
    private boolean backLockout = false;
    private boolean bLockout = false;

    @Override
    public void init() {




        /*DcMotor frontLeft = hardwareMap().dcMotor.get(Felix.DRIVE_FL_KEY);
        DcMotor rearLeft = hardwareMap().dcMotor.get(Felix.DRIVE_RL_KEY);
        DcMotor frontRight = hardwareMap().dcMotor.get(Felix.DRIVE_FR_KEY);
        DcMotor rearRight = hardwareMap().dcMotor.get(Felix.DRIVE_RR_KEY);*/
        DcMotor frontLeft = hardwareMap().get(DcMotorEx.class, Felix.DRIVE_FL_KEY);
        DcMotor rearLeft = hardwareMap().get(DcMotorEx.class, Felix.DRIVE_RL_KEY);
        DcMotor frontRight = hardwareMap().get(DcMotorEx.class, Felix.DRIVE_FR_KEY);
        DcMotor rearRight = hardwareMap().get(DcMotorEx.class, Felix.DRIVE_RR_KEY);

        rearLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);


        this.left = new MotorPair(frontLeft, rearLeft);
        this.right = new MotorPair(frontRight, rearRight);

        modeSpeed();
        brakeMode();


        Threading.startThread(() -> {
            try {
                Log.i("IMUt", "starting imu stuff");

                imu = new SafeAdafruitIMU(hardwareMap(), Felix.IMU_KEY, true);
                imu.update();
                imu.zeroHeading();

                telemetry().addData("IMU initialized", () -> true);
                telemetry().update();
                Log.i("IMUt", "imu initialization done");


                while (FtcOpModeRegister.opModeManager.getActiveOpMode() == robot.opMode) {
                    imu.update();
                    Threading.delay(0.03);
                }
            } catch (Exception ex) {
                Log.e("IMUFAIL", "IMU FAILED", ex);
                telemetry().addData("IMU FAILED0", () -> "IMU FAILED RESTART ROBOT");
                telemetry().addData("IMU FAILED1", () -> "IMU FAILED RESTART ROBOT");
                telemetry().addData("IMU FAILED2", () -> "IMU FAILED RESTART ROBOT");
                telemetry().addData("IMU FAILED3", () -> "IMU FAILED RESTART ROBOT");
                telemetry().addData("IMU FAILED4", () -> "IMU FAILED RESTART ROBOT");
                telemetry().addData("IMU FAILED5", () -> "IMU FAILED RESTART ROBOT");
                telemetry().addData("IMU FAILED6", () -> "IMU FAILED RESTART ROBOT");
                telemetry().addData("IMU FAILED7", () -> "IMU FAILED RESTART ROBOT");
                telemetry().addData("IMU FAILED8", () -> "IMU FAILED RESTART ROBOT");
                telemetry().addData("IMU FAILED9", () -> "IMU FAILED RESTART ROBOT");
            }
        });

        roadrunner = new SampleTankDriveREV(this);
    }

    @Override
    public void handle() {

        double speed = (gamepad1().right_trigger - gamepad1().left_trigger);
        speed *= Math.abs(speed);
        double dir = gamepad1().left_stick_x;

        dir *= .7;

        double left = ((1 - Math.abs(dir)) * speed + (1 - Math.abs(speed)) * dir + dir + speed) / 2;
        double right = ((1 - Math.abs(dir)) * speed - (1 - Math.abs(speed)) * dir - dir + speed) / 2;


        set(left, right);

    }

    @Override
    public void stop() {
        left.stop();
        right.stop();
    }

    public void floatMode() {
        this.left.floatMode();
        this.right.floatMode();
    }

    public void brakeMode() {
        this.left.brakeMode();
        this.right.brakeMode();
    }

    public void modeSpeed() {
        this.left.runMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.right.runMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void modeVoltage() {
        this.left.runMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.right.runMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void setLeft(double power) {
        this.left.setPower(power);
    }

    public void setRight(double power) {
        this.right.setPower(power);
    }

    public void set(double left, double right) {
        setLeft(left);
        setRight(right);
    }

    public void drive(double speed) {
        set(speed, speed);
    }

    public void driveUntil(double speed, Supplier<Boolean> until) {
        drive(speed);
        Threading.waitFor(until);
        stop();
    }

    public void driveForNoPID(double maxSpeed, double inches) {
        left.resetEncoder();
        right.resetEncoder();
        double targetPulses = inchesToEncoderPulses(inches);
        set(maxSpeed, maxSpeed);

        Threading.waitFor(() -> {
            if(!Threading.isOpModeActive()) {
                return true;
            }
            telemetry().addData("left", left.getEncoder());
            telemetry().addData("Right", right.getEncoder());
            telemetry().addData("target", targetPulses);
            telemetry().update();
            return Math.abs(this.left.getEncoder() - targetPulses) < 25 || Math.abs(this.right.getEncoder() - targetPulses) < 25;
        });
        stop();
    }

    public void driveFor(double maxSpeed, double p, double i, double d, double inches, boolean pass) {
        left.resetEncoder();
        right.resetEncoder();
        double targetPulses = inchesToEncoderPulses(inches);
        SynchronousPID leftPID = new SynchronousPID(p, i, d);
        leftPID.setOutputRange(-maxSpeed, maxSpeed);
        SynchronousPID rightPID = new SynchronousPID(p, i, d);
        rightPID.setOutputRange(-maxSpeed, maxSpeed);
        leftPID.setSetpoint(targetPulses);
        rightPID.setSetpoint(targetPulses);

        Threading.waitFor(() -> {
            if(!Threading.isOpModeActive()) {
                return true;
            }
            int leftEncoder = this.left.getEncoder();
            int rightEncoder = this.right.getEncoder();
            double left = leftPID.calculate(leftEncoder);
            double right = rightPID.calculate(rightEncoder);
            set(left, right);
            if(pass) {
                if(targetPulses > 0) {
                    return leftEncoder > targetPulses || rightEncoder > targetPulses;
                } else {
                    return leftEncoder < targetPulses || rightEncoder < targetPulses;
                }
            } else {
                return Math.abs(leftEncoder - targetPulses) < 25 || Math.abs(rightEncoder - targetPulses) < 25;
            }
        });
        stop();
    }

    public void driveFor(double inches) {
        driveFor(kDriveFor_maxSpeed, kDriveFor_p, kDriveFor_i, kDriveFor_d, inches, false);
    }


    public void driveForAlt(double maxSpeed, double p, double i, double d, double inches){
        left.resetEncoder();
        right.resetEncoder();

        double targetPulses = inchesToEncoderPulses(inches);
        int accuracyPulses = 45;
        SynchronousPID leftPID = new SynchronousPID(p, i, d);
        leftPID.setOutputRange(-maxSpeed, maxSpeed);
        SynchronousPID rightPID = new SynchronousPID(p, i, d);
        rightPID.setOutputRange(-maxSpeed, maxSpeed);
        leftPID.setSetpoint(targetPulses);
        rightPID.setSetpoint(targetPulses);

        /*
         * Last system time at which the PID loop was definitely NOT stable at the solution.
         */
        long lastBad = System.currentTimeMillis();
        long startTime = System.currentTimeMillis();


        /*
         * Loop until either:
         *   (1) Autonomous is over.
         *   (2) The PID loop becomes stable at the solution for at least 25 ms.
         */
        while(Threading.isOpModeActive()) {//Threading.isOpModeActive()) {

            try { Thread.sleep(1); }
            catch (InterruptedException ex) { }

            /*
             * Stop if supplier returns true
             */

            //add supplier?

            /*
             * Check if autonomous is over.
             */
            //if(!Threading.isOpModeActive()) {
            //  return;
            //}

            /*
             * Check if loop is timed out
             */

            //if((System.currentTimeMillis() - startTime) > (long)(timeOut*1000)) {
            //    return;
            //}

            /*
             * Update the PID.
             */
            int leftEncoder = this.left.getEncoder();
            int rightEncoder = this.right.getEncoder();
            double left = leftPID.calculate(leftEncoder);
            double right = rightPID.calculate(rightEncoder);


            /*
             * Ensure that we never try to send the motor a speed so low it won't do anything.
             */

            //unnecessary i think

            /*
             * If the PID loop is NOT stable at the solution (error is too large) then update
             * the time.
             */
            if(Math.abs(leftEncoder - targetPulses) > accuracyPulses || Math.abs(rightEncoder-targetPulses) > accuracyPulses) lastBad = System.currentTimeMillis();


            /*
             * If the PID loop IS stable for the past 25 ms then we're done.
             */
            if (System.currentTimeMillis() - lastBad > 25) return;


            /*
             * Looks like we're not stable yet. Update the motors.
             */

            set(left, right);
        }

    }

    public void driveForAlt(double inches) {
        driveForAlt(kDriveFor_maxSpeed, kDriveFor_p, kDriveFor_i, kDriveFor_d, inches);
    }

    public void turnPID(double maxTurn, double kp, double ki, double kd, double angle, double turnTolerance, double delay) {
        SynchronousPID turnPID = new SynchronousPID(kp, ki, kd);
        turnPID.setOutputRange(-maxTurn, maxTurn);
        turnPID.setSetpoint(angle);
        turnPID.setDeadband(turnTolerance);

        Threading.waitFor(() -> {
            if(!Threading.isOpModeActive()) {
                return true;
            }
            Threading.delay(delay);
            double heading = imu.getHeading();
            double turnFactor = turnPID.calculateGivenError(AngleUtil.normalize180(angle - heading));
            if(turnFactor > -.07 && turnFactor < 0) {
                turnFactor = -.07;
            }
            if(turnFactor < .07 && turnFactor > 0) {
                turnFactor = .07;
            }
            set(turnFactor, -turnFactor);
            telemetry().addData("heading", heading);
            telemetry().addData("turn error", turnPID.getError());
            telemetry().addData("turnFactor", turnFactor);
            telemetry().update();
            return Math.abs(turnPID.getError()) < turnTolerance;
        });

        stop();
    }

    public void turnPID(double angle) {
        turnPID(kTurnPID_maxTurn, kTurnPID_p, kTurnPID_i, kTurnPID_d, angle, 3, 0.02);
    }

    public void turnPIDbad(double angle) {
        turnPID(.9, .007, .000003, .00001, angle, 2, .02);
    }

    public void turnPIDfast(double angle) {
        turnPID(0.4, 0.013, 0, 0.07, angle, 5, 0.05);
    }

    public void turnPIDsuperfast(double angle) {
        turnPID(1, 0.03, 0, 0.07, angle, 5, 0.05);
    }


    public double inchesToEncoderPulses(double inches) {
        return (537.6 * inches)/(4.0*Math.PI);
    }


}
