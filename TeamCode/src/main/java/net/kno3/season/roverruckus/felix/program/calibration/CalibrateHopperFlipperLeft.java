package net.kno3.season.roverruckus.felix.program.calibration;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import net.kno3.robot.RobotSettings;
import net.kno3.season.roverruckus.felix.robot.Felix;
import net.kno3.util.Threading;


@TeleOp(group = "calibration", name = "Calibrate Hopper Flipper Left")
public class CalibrateHopperFlipperLeft extends LinearOpMode{
    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("1", "Press start");
        telemetry.update();
        waitForStart();

        RobotSettings settings = new RobotSettings("Felix");

        //ServoDefaults.resetAllServos(hardwareMap, settings);

        while(opModeIsActive()) {
            Servo hopperFlipper = hardwareMap.servo.get(Felix.HOPPER_FLIPPER_LEFT_KEY);
            double idleSetpoint = settings.getDouble("hopper_flipper_left_idle");
            while(opModeIsActive() && !gamepad1.a) {
                if(Math.abs(gamepad1.left_stick_x) > 0.05) {
                    idleSetpoint += gamepad1.left_stick_x * Math.abs(gamepad1.left_stick_x) / 3000;
                }
                if(Math.abs(gamepad1.right_stick_x) > 0.05) {
                    idleSetpoint += gamepad1.right_stick_x * Math.abs(gamepad1.right_stick_x) / 15000;
                }
                hopperFlipper.setPosition(idleSetpoint);
                telemetry.addData("Idle Setpoint: ", idleSetpoint);
                telemetry.addData("2", "Press A to continue");
                telemetry.update();
            }

            telemetry.addData("2", "Release A");
            telemetry.update();
            while(opModeIsActive() && gamepad1.a);


            double upSetpoint = settings.getDouble("hopper_flipper_left_up");
            while(opModeIsActive() && !gamepad1.a) {
                if(Math.abs(gamepad1.left_stick_x) > 0.05) {
                    upSetpoint += gamepad1.left_stick_x * Math.abs(gamepad1.left_stick_x) / 3000;
                }
                if(Math.abs(gamepad1.right_stick_x) > 0.05) {
                    upSetpoint += gamepad1.right_stick_x * Math.abs(gamepad1.right_stick_x) / 15000;
                }
                hopperFlipper.setPosition(upSetpoint);
                telemetry.addData("up Setpoint: ", upSetpoint);
                telemetry.addData("2", "Press A to continue");
                telemetry.update();
            }


            telemetry.addData("2", "Release A");
            telemetry.update();
            while(opModeIsActive() && gamepad1.a);

            double downSetpoint = settings.getDouble("hopper_flipper_left_down");
            while(opModeIsActive() && !gamepad1.a) {
                if(Math.abs(gamepad1.left_stick_x) > 0.05) {
                    downSetpoint += gamepad1.left_stick_x * Math.abs(gamepad1.left_stick_x) / 3000;
                }
                if(Math.abs(gamepad1.right_stick_x) > 0.05) {
                    downSetpoint += gamepad1.right_stick_x * Math.abs(gamepad1.right_stick_x) / 15000;
                }
                hopperFlipper.setPosition(downSetpoint);
                telemetry.addData("Down Setpoint: ", downSetpoint);
                telemetry.addData("2", "Press A to continue");
                telemetry.update();
            }

            telemetry.addData("2", "Release A");
            telemetry.update();
            while(opModeIsActive() && gamepad1.a);

            double middleSetpoint = settings.getDouble("hopper_flipper_left_middle");
            while(opModeIsActive() && !gamepad1.a) {
                if(Math.abs(gamepad1.left_stick_x) > 0.05) {
                    middleSetpoint += gamepad1.left_stick_x * Math.abs(gamepad1.left_stick_x) / 3000;
                }
                if(Math.abs(gamepad1.right_stick_x) > 0.05) {
                    middleSetpoint += gamepad1.right_stick_x * Math.abs(gamepad1.right_stick_x) / 15000;
                }
                hopperFlipper.setPosition(middleSetpoint);
                telemetry.addData("Middle Setpoint: ", middleSetpoint);
                telemetry.addData("2", "Press A to continue");
                telemetry.update();
            }

            telemetry.addData("2", "Release A");
            telemetry.update();
            while(opModeIsActive() && gamepad1.a);



            telemetry.addData("3", "press B to test");
            telemetry.update();
            while(opModeIsActive() && !gamepad1.b);

            hopperFlipper.setPosition(idleSetpoint);
            Threading.delay(4);
            hopperFlipper.setPosition(upSetpoint);
            Threading.delay(4);
            hopperFlipper.setPosition(downSetpoint);
            Threading.delay(4);
            hopperFlipper.setPosition(middleSetpoint);
            Threading.delay(4);

            settings.setDouble("hopper_flipper_left_idle", idleSetpoint);
            settings.setDouble("hopper_flipper_left_up", upSetpoint);
            settings.setDouble("hopper_flipper_left_down", downSetpoint);
            settings.setDouble("hopper_flipper_left_middle", middleSetpoint);
            settings.save();

            telemetry.addData("5", "Saved! Press Y to try again.");
            telemetry.update();
            while(opModeIsActive() && !gamepad1.y);
        }
    }
}