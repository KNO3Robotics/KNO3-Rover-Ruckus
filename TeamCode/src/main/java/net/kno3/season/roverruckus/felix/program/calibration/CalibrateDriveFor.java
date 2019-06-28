package net.kno3.season.roverruckus.felix.program.calibration;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import net.kno3.robot.Robot;
import net.kno3.robot.RobotSettings;
import net.kno3.season.roverruckus.felix.robot.DriveSystem;
import net.kno3.util.Threading;
import net.kno3.util.ValuesAdjuster;

import org.firstinspires.ftc.robotcontroller.internal.FtcOpModeRegister;
import org.firstinspires.ftc.robotcore.internal.opmode.OpModeManagerImpl;

/**
 * Created by robotics on 10/29/2017.
 */
@TeleOp(group = "calibration", name = "Calibrate DriveFor")
public class CalibrateDriveFor extends LinearOpMode {
    public double kDriveFor_p, kDriveFor_i, kDriveFor_d, kDriveFor_maxSpeed;;
    public double testDistance = 24;

    @Override
    public void runOpMode() throws InterruptedException {
        FtcOpModeRegister.opModeManager = (OpModeManagerImpl) internalOpModeServices;

        Robot robot = new Robot(this);
        DriveSystem drive = new DriveSystem(robot);
        drive.init();
        drive.modeSpeed();
        telemetry.addData("1", "Press start when imu is ready");
        telemetry.update();
        waitForStart();

        RobotSettings settings = new RobotSettings("Felix");

        kDriveFor_p = settings.getDouble("kDriveFor_p");
        kDriveFor_i = settings.getDouble("kDriveFor_i");
        kDriveFor_d = settings.getDouble("kDriveFor_d");
        kDriveFor_maxSpeed = settings.getDouble("kDriveFor_maxSpeed");

        ValuesAdjuster adjuster = new ValuesAdjuster(this, telemetry);
        adjuster.addValue("kDriveFor_maxSpeed", "Max Speed", 0, 10000);
        adjuster.addValue("kDriveFor_p", "P", 0, 10000);
        adjuster.addValue("kDriveFor_i", "I", 0, 10000);
        adjuster.addValue("kDriveFor_d", "D", 0, 10000);
        adjuster.addValue("testDistance", "Testing Distance (inches)", 0, 10000);

        while (opModeIsActive()) {
            while (opModeIsActive() && !gamepad1.a) {
                telemetry.addData("2", "Press A to test");
                telemetry.addData("3", "Press Start and back to change the increment");
                telemetry.addData("4", "Press dpad left and right to changed adjusted value");
                telemetry.addData("5", "Press the bumpers to adjust the value");
                adjuster.update(gamepad1);
                telemetry.update();
            }
            drive.kDriveFor_p = kDriveFor_p;
            drive.kDriveFor_i = kDriveFor_i;
            drive.kDriveFor_d = kDriveFor_d;
            drive.kDriveFor_maxSpeed = kDriveFor_maxSpeed;

            Thread.sleep(1000);

            drive.driveFor(testDistance);
            drive.stop();

            while (opModeIsActive()) {
                telemetry.addData("6", "Press Y to save and exit");
                telemetry.addData("7", "Press B to try again");
                telemetry.update();
                if(gamepad1.y) {
                    settings.setDouble("kDriveFor_maxSpeed", kDriveFor_maxSpeed);
                    settings.setDouble("kDriveFor_p", kDriveFor_p);
                    settings.setDouble("kDriveFor_i", kDriveFor_i);
                    settings.setDouble("kDriveFor_d", kDriveFor_d);
                    settings.save();
                    return;
                }
                if(gamepad1.b) {
                    break;
                }
            }
        }
    }
}