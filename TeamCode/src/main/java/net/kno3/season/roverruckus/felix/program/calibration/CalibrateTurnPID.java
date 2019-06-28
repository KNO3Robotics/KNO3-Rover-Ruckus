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

@TeleOp(group = "calibration", name = "Calibrate TurnPID")
public class CalibrateTurnPID extends LinearOpMode {
    public double kTurnPID_maxTurn, kTurnPID_p, kTurnPID_i, kTurnPID_d;
    public double testAngle = 90;

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

        kTurnPID_maxTurn = settings.getDouble("kTurnPID_maxTurn");
        kTurnPID_p = settings.getDouble("kTurnPID_p");
        kTurnPID_i = settings.getDouble("kTurnPID_i");
        kTurnPID_d = settings.getDouble("kTurnPID_d");

        ValuesAdjuster adjuster = new ValuesAdjuster(this, telemetry);
        adjuster.addValue("kTurnPID_maxTurn", "Max Turn Speed", 0, 10000);
        adjuster.addValue("kTurnPID_p", "KP", 0, 10000);
        adjuster.addValue("kTurnPID_i", "KI", 0, 10000);
        adjuster.addValue("kTurnPID_d", "KD", 0, 10000);
        adjuster.addValue("testAngle", "Testing Angle", 0, 360);

        while (opModeIsActive()) {
            while (opModeIsActive() && !gamepad1.a) {
                telemetry.addData("2", "Press A to test");
                telemetry.addData("3", "Press Start and back to change the increment");
                telemetry.addData("4", "Press dpad left and right to changed adjusted value");
                telemetry.addData("5", "Press the bumpers to adjust the value");
                adjuster.update(gamepad1);
                telemetry.update();
            }
            drive.kTurnPID_maxTurn = kTurnPID_maxTurn;
            drive.kTurnPID_p = kTurnPID_p;
            drive.kTurnPID_i = kTurnPID_i;
            drive.kTurnPID_d = kTurnPID_d;
            Thread.sleep(1000);

            drive.turnPID(testAngle);
            drive.stop();

            while (opModeIsActive()) {
                telemetry.addData("6", "Press Y to save and exit");
                telemetry.addData("7", "Press B to try again");
                telemetry.update();
                if(gamepad1.y) {
                    settings.setDouble("kTurnPID_maxTurn", kTurnPID_maxTurn);
                    settings.setDouble("kTurnPID_p", kTurnPID_p);
                    settings.setDouble("kTurnPID_i", kTurnPID_i);
                    settings.setDouble("kTurnPID_d", kTurnPID_d);
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