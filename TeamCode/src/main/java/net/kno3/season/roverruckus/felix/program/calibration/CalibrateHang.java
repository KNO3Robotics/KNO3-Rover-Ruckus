package net.kno3.season.roverruckus.felix.program.calibration;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import net.kno3.robot.RobotSettings;
import net.kno3.season.roverruckus.felix.robot.Felix;
import net.kno3.util.Threading;


@TeleOp(group = "calibration", name = "Calibrate Hang")
public class CalibrateHang extends LinearOpMode{
    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("1", "Press start");
        telemetry.update();
        waitForStart();

        RobotSettings settings = new RobotSettings("Felix");

        //ServoDefaults.resetAllServos(hardwareMap, settings);

        while(opModeIsActive()) {
            Servo hang = hardwareMap.servo.get(Felix.HANG_SERVO_KEY);

            double openSetpoint = settings.getDouble("hang_open");
                while(opModeIsActive() && !gamepad1.a) {
                if(Math.abs(gamepad1.left_stick_x) > 0.05) {
                    openSetpoint += gamepad1.left_stick_x * Math.abs(gamepad1.left_stick_x) / 3000;
                }
                if(Math.abs(gamepad1.right_stick_x) > 0.05) {
                    openSetpoint += gamepad1.right_stick_x * Math.abs(gamepad1.right_stick_x) / 15000;
                }
                hang.setPosition(openSetpoint);
                telemetry.addData("Open Setpoint: ", openSetpoint);
                telemetry.addData("2", "Press A to continue");
                telemetry.update();
            }

            telemetry.addData("2", "Release A");
            telemetry.update();
            while(opModeIsActive() && gamepad1.a);

            double closeSetpoint = settings.getDouble("hang_close");
            while(opModeIsActive() && !gamepad1.a) {
                if(Math.abs(gamepad1.left_stick_x) > 0.05) {
                    closeSetpoint += gamepad1.left_stick_x * Math.abs(gamepad1.left_stick_x) / 3000;
                }
                if(Math.abs(gamepad1.right_stick_x) > 0.05) {
                    closeSetpoint += gamepad1.right_stick_x * Math.abs(gamepad1.right_stick_x) / 15000;
                }
                hang.setPosition(closeSetpoint);
                telemetry.addData("close Setpoint: ", closeSetpoint);
                telemetry.addData("2", "Press A to continue");
                telemetry.update();
            }


            telemetry.addData("2", "Release A");
            telemetry.update();
            while(opModeIsActive() && gamepad1.a);


            telemetry.addData("3", "press B to test");
            telemetry.update();
            while(opModeIsActive() && !gamepad1.b);

            hang.setPosition(openSetpoint);
            Threading.delay(4);
            hang.setPosition(closeSetpoint);
            Threading.delay(4);

            settings.setDouble("hang_open", openSetpoint);
            settings.setDouble("hang_close", closeSetpoint);
            settings.save();

            telemetry.addData("5", "Saved! Press Y to try again.");
            telemetry.update();
            while(opModeIsActive() && !gamepad1.y);
        }
    }
}
