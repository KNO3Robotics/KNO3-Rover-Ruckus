package net.kno3.season.roverruckus.felix.program.calibration;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import net.kno3.robot.RobotSettings;
import net.kno3.season.roverruckus.felix.robot.Felix;
import net.kno3.util.Threading;


@TeleOp(group = "calibration", name = "Calibrate Intake Flipper")
public class CalibrateIntakeFlipper extends LinearOpMode{
    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("1", "Press start");
        telemetry.update();
        waitForStart();

        RobotSettings settings = new RobotSettings("Felix");

        //ServoDefaults.resetAllServos(hardwareMap, settings);

        while(opModeIsActive()) {
            Servo intakeFlip = hardwareMap.servo.get(Felix.INTAKE_FLIPPER_KEY);

            double upSetpoint = settings.getDouble("intake_flipper_up");
                while(opModeIsActive() && !gamepad1.a) {
                if(Math.abs(gamepad1.left_stick_x) > 0.05) {
                    upSetpoint += gamepad1.left_stick_x * Math.abs(gamepad1.left_stick_x) / 3000;
                }
                if(Math.abs(gamepad1.right_stick_x) > 0.05) {
                    upSetpoint += gamepad1.right_stick_x * Math.abs(gamepad1.right_stick_x) / 15000;
                }
                intakeFlip.setPosition(upSetpoint);
                telemetry.addData("up Setpoint: ", upSetpoint);
                telemetry.addData("2", "Press A to continue");
                telemetry.update();
            }

            telemetry.addData("2", "Release A");
            telemetry.update();
            while(opModeIsActive() && gamepad1.a);

            double downSetpoint = settings.getDouble("intake_flipper_down");
            while(opModeIsActive() && !gamepad1.a) {
                if(Math.abs(gamepad1.left_stick_x) > 0.05) {
                    downSetpoint += gamepad1.left_stick_x * Math.abs(gamepad1.left_stick_x) / 3000;
                }
                if(Math.abs(gamepad1.right_stick_x) > 0.05) {
                    downSetpoint += gamepad1.right_stick_x * Math.abs(gamepad1.right_stick_x) / 15000;
                }
                intakeFlip.setPosition(downSetpoint);
                telemetry.addData("Down Setpoint: ", downSetpoint);
                telemetry.addData("2", "Press A to continue");
                telemetry.update();
            }


            telemetry.addData("2", "Release A");
            telemetry.update();
            while(opModeIsActive() && gamepad1.a);

            double idleSetpoint = settings.getDouble("intake_flipper_idle");
            while(opModeIsActive() && !gamepad1.a) {
                if(Math.abs(gamepad1.left_stick_x) > 0.05) {
                    idleSetpoint += gamepad1.left_stick_x * Math.abs(gamepad1.left_stick_x) / 3000;
                }
                if(Math.abs(gamepad1.right_stick_x) > 0.05) {
                    idleSetpoint += gamepad1.right_stick_x * Math.abs(gamepad1.right_stick_x) / 15000;
                }
                intakeFlip.setPosition(idleSetpoint);
                telemetry.addData("idle Setpoint: ", idleSetpoint);
                telemetry.addData("2", "Press A to continue");
                telemetry.update();
            }


            telemetry.addData("2", "Release A");
            telemetry.update();
            while(opModeIsActive() && gamepad1.a);


            telemetry.addData("3", "press B to test");
            telemetry.update();
            while(opModeIsActive() && !gamepad1.b);

            intakeFlip.setPosition(upSetpoint);
            Threading.delay(4);
            intakeFlip.setPosition(downSetpoint);
            Threading.delay(4);
            intakeFlip.setPosition(idleSetpoint);
            Threading.delay(4);

            settings.setDouble("intake_flipper_up", upSetpoint);
            settings.setDouble("intake_flipper_down", downSetpoint);
            settings.setDouble("intake_flipper_idle", idleSetpoint);
            settings.save();

            telemetry.addData("5", "Saved! Press Y to try again.");
            telemetry.update();
            while(opModeIsActive() && !gamepad1.y);
        }
    }
}
