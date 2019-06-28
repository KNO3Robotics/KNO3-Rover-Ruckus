package net.kno3.season.roverruckus.felix.program.calibration;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import net.kno3.robot.RobotSettings;
import net.kno3.season.roverruckus.felix.robot.Felix;
import net.kno3.util.Threading;


@TeleOp(group = "calibration", name = "Calibrate Intake Transfer")
public class CalibrateIntakeTransfer extends LinearOpMode{
    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("1", "Press start");
        telemetry.update();
        waitForStart();

        RobotSettings settings = new RobotSettings("Felix");

        //ServoDefaults.resetAllServos(hardwareMap, settings);

        while(opModeIsActive()) {
            Servo intakeTransfer = hardwareMap.servo.get(Felix.INTAKE_TRANSFER_KEY);

            double outSetpoint = settings.getDouble("intake_transfer_out");
                while(opModeIsActive() && !gamepad1.a) {
                if(Math.abs(gamepad1.left_stick_x) > 0.05) {
                    outSetpoint += gamepad1.left_stick_x * Math.abs(gamepad1.left_stick_x) / 3000;
                }
                if(Math.abs(gamepad1.right_stick_x) > 0.05) {
                    outSetpoint += gamepad1.right_stick_x * Math.abs(gamepad1.right_stick_x) / 15000;
                }
                intakeTransfer.setPosition(outSetpoint);
                telemetry.addData("up Setpoint: ", outSetpoint);
                telemetry.addData("2", "Press A to continue");
                telemetry.update();
            }

            telemetry.addData("2", "Release A");
            telemetry.update();
            while(opModeIsActive() && gamepad1.a);

            double inSetpoint = settings.getDouble("intake_transfer_in");
            while(opModeIsActive() && !gamepad1.a) {
                if(Math.abs(gamepad1.left_stick_x) > 0.05) {
                    inSetpoint += gamepad1.left_stick_x * Math.abs(gamepad1.left_stick_x) / 3000;
                }
                if(Math.abs(gamepad1.right_stick_x) > 0.05) {
                    inSetpoint += gamepad1.right_stick_x * Math.abs(gamepad1.right_stick_x) / 15000;
                }
                intakeTransfer.setPosition(inSetpoint);
                telemetry.addData("Down Setpoint: ", inSetpoint);
                telemetry.addData("2", "Press A to continue");
                telemetry.update();
            }

            telemetry.addData("2", "Release A");
            telemetry.update();
            while(opModeIsActive() && gamepad1.a);

            double idleSetpoint = settings.getDouble("intake_transfer_idle");
            while(opModeIsActive() && !gamepad1.a) {
                if(Math.abs(gamepad1.left_stick_x) > 0.05) {
                    idleSetpoint += gamepad1.left_stick_x * Math.abs(gamepad1.left_stick_x) / 3000;
                }
                if(Math.abs(gamepad1.right_stick_x) > 0.05) {
                    idleSetpoint += gamepad1.right_stick_x * Math.abs(gamepad1.right_stick_x) / 15000;
                }
                intakeTransfer.setPosition(idleSetpoint);
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

            intakeTransfer.setPosition(outSetpoint);
            Threading.delay(4);
            intakeTransfer.setPosition(inSetpoint);
            Threading.delay(4);
            intakeTransfer.setPosition(idleSetpoint);
            Threading.delay(4);

            settings.setDouble("intake_transfer_out", outSetpoint);
            settings.setDouble("intake_transfer_in", inSetpoint);
            settings.setDouble("intake_transfer_idle", idleSetpoint);
            settings.save();

            telemetry.addData("5", "Saved! Press Y to try again.");
            telemetry.update();
            while(opModeIsActive() && !gamepad1.y);
        }
    }
}
