package net.kno3.season.roverruckus.felix.program.calibration;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import net.kno3.util.Threading;


@TeleOp(group = "calibration", name = "calibrate drive encoders")
public class CalibrateDriveEncoders extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("1", "Press start");
        telemetry.update();

        waitForStart();

        DcMotor frontleft = hardwareMap.dcMotor.get("frontleft");
        frontleft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        frontleft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        while(opModeIsActive()) {
            telemetry.addData("encoder pos", frontleft.getCurrentPosition());
            telemetry.update();
        }

    }
}
