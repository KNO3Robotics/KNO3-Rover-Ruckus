package net.kno3.season.roverruckus.felix.program.calibration;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import net.kno3.robot.RobotSettings;
import net.kno3.season.roverruckus.felix.robot.Felix;
import net.kno3.util.Threading;


@TeleOp(group = "calibration", name = "Calibrate Land")
public class CalibrateLand extends LinearOpMode{
    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("1", "Press start");
        telemetry.update();
        waitForStart();

        RobotSettings settings = new RobotSettings("Felix");

        //ServoDefaults.resetAllServos(hardwareMap, settings);

        while(opModeIsActive()) {
            Servo land = hardwareMap.servo.get(Felix.LAND_SERVO_KEY);


            double idleSetpoint = settings.getDouble("land_idle");
            while(opModeIsActive() && !gamepad1.a) {
                if(Math.abs(gamepad1.left_stick_x) > 0.05) {
                    idleSetpoint += gamepad1.left_stick_x * Math.abs(gamepad1.left_stick_x) / 3000;
                }
                if(Math.abs(gamepad1.right_stick_x) > 0.05) {
                    idleSetpoint += gamepad1.right_stick_x * Math.abs(gamepad1.right_stick_x) / 15000;
                }
                land.setPosition(idleSetpoint);
                telemetry.addData("Idle Setpoint: ", idleSetpoint);
                telemetry.addData("2", "Press A to continue");
                telemetry.update();
            }

            telemetry.addData("2", "Release A");
            telemetry.update();
            while(opModeIsActive() && gamepad1.a);

            double openSetpoint = settings.getDouble("land_open");
                while(opModeIsActive() && !gamepad1.a) {
                if(Math.abs(gamepad1.left_stick_x) > 0.05) {
                    openSetpoint += gamepad1.left_stick_x * Math.abs(gamepad1.left_stick_x) / 3000;
                }
                if(Math.abs(gamepad1.right_stick_x) > 0.05) {
                    openSetpoint += gamepad1.right_stick_x * Math.abs(gamepad1.right_stick_x) / 15000;
                }
                land.setPosition(openSetpoint);
                telemetry.addData("Open Setpoint: ", openSetpoint);
                telemetry.addData("2", "Press A to continue");
                telemetry.update();
            }

            telemetry.addData("2", "Release A");
            telemetry.update();
            while(opModeIsActive() && gamepad1.a);

            double lockedSetpoint = settings.getDouble("land_locked");
            while(opModeIsActive() && !gamepad1.a) {
                if(Math.abs(gamepad1.left_stick_x) > 0.05) {
                    lockedSetpoint += gamepad1.left_stick_x * Math.abs(gamepad1.left_stick_x) / 3000;
                }
                if(Math.abs(gamepad1.right_stick_x) > 0.05) {
                    lockedSetpoint += gamepad1.right_stick_x * Math.abs(gamepad1.right_stick_x) / 15000;
                }
                land.setPosition(lockedSetpoint);
                telemetry.addData("locked Setpoint: ", lockedSetpoint);
                telemetry.addData("2", "Press A to continue");
                telemetry.update();
            }


            telemetry.addData("2", "Release A");
            telemetry.update();
            while(opModeIsActive() && gamepad1.a);


            telemetry.addData("3", "press B to test");
            telemetry.update();
            while(opModeIsActive() && !gamepad1.b);

            land.setPosition(idleSetpoint);
            Threading.delay(4);
            land.setPosition(openSetpoint);
            Threading.delay(4);
            land.setPosition(lockedSetpoint);
            Threading.delay(4);

            settings.setDouble("land_open", openSetpoint);
            settings.setDouble("land_locked", lockedSetpoint);
            settings.setDouble("land_idle", idleSetpoint);
            settings.save();

            telemetry.addData("5", "Saved! Press Y to try again.");
            telemetry.update();
            while(opModeIsActive() && !gamepad1.y);
        }
    }
}
