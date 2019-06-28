package net.kno3.season.roverruckus.felix.program.calibration;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import net.kno3.season.roverruckus.felix.robot.Felix;
import net.kno3.util.AdafruitIMU;
import net.kno3.util.IAdafruitIMU;
import net.kno3.util.MotorPair;
import net.kno3.util.SynchronousPID;
import net.kno3.util.Threading;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.function.Supplier;

/**
 * Created by robotics on 10/29/2017.
 */

public class TestingSolusDT {
    public double kDriveFor_p, kDriveFor_i, kDriveFor_d, kDriveFor_maxSpeed;

    public MotorPair left, right;

    private DcMotor frontLeft, frontRight, rearLeft, rearRight;

    private IAdafruitIMU imu;
    private double imuZeroHeading;


    Telemetry telemetry;

    public TestingSolusDT(HardwareMap hardwareMap, Telemetry telemetry) {
        DcMotor frontLeft = hardwareMap.dcMotor.get(Felix.DRIVE_FL_KEY);
        DcMotor rearLeft = hardwareMap.dcMotor.get(Felix.DRIVE_RL_KEY);
        DcMotor frontRight = hardwareMap.dcMotor.get(Felix.DRIVE_FR_KEY);
        DcMotor rearRight = hardwareMap.dcMotor.get(Felix.DRIVE_RR_KEY);

        rearRight.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        this.telemetry = telemetry;

        this.left = new MotorPair(frontLeft, rearLeft);
        this.right = new MotorPair(frontRight, rearRight);

        modeSpeed();
        brakeMode();

        Threading.startThread(() -> {
            imu = new AdafruitIMU(hardwareMap, "imu", true);
            telemetry.addData("IMU initialized", true);
            telemetry.addData("heading", () -> imu.getHeading());
            telemetry.update();
        });

    }

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

    public double getHeading() {
        return imu.getHeading() - imuZeroHeading;
    }

    public void zeroHeading() {
        this.imuZeroHeading = imu.getHeading();
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
            return Math.abs(this.left.getEncoder() - targetPulses) < 25 || Math.abs(this.right.getEncoder() - targetPulses) < 25;
        });
    }

    public void driveForAlt(double maxSpeed, double p, double i, double d, double inches){
        left.resetEncoder();
        right.resetEncoder();

        double targetPulses = inchesToEncoderPulses(inches);
        int accuracyPulses = 25;
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
        while(true) {

            try { Thread.sleep(5); }
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
    public void driveFor(double maxSpeed, double p, double inches, boolean pass) {
        left.resetEncoder();
        right.resetEncoder();
        double targetPulses = inchesToEncoderPulses(inches);
        SynchronousPID leftPID = new SynchronousPID(p, 0, 0);
        leftPID.setOutputRange(-maxSpeed, maxSpeed);
        SynchronousPID rightPID = new SynchronousPID(p, 0, 0);
        rightPID.setOutputRange(-maxSpeed, maxSpeed);
        leftPID.setSetpoint(targetPulses);
        rightPID.setSetpoint(targetPulses);

        Threading.waitFor(() -> {
            Threading.delay(0.05);
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

    public double inchesToEncoderPulses(double inches) {
        return (537.6 * inches)/(4.0*Math.PI);
    }

}

