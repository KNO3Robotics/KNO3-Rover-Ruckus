package net.kno3.season.roverruckus.felix.program.auto.goldside;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;

import net.kno3.season.roverruckus.felix.program.auto.FelixAuto;
import net.kno3.season.roverruckus.felix.program.dogecvtests.CroppingGoldDetector;
import net.kno3.season.roverruckus.felix.program.dogecvtests.KNO3Vision;
import net.kno3.season.roverruckus.felix.robot.FelixPersist;
import net.kno3.season.roverruckus.felix.robot.HangSystem;
import net.kno3.season.roverruckus.felix.robot.HopperSystem;
import net.kno3.season.roverruckus.felix.robot.IntakeSystem;
import net.kno3.util.AutoTransitioner;
import net.kno3.util.Threading;

@Autonomous(name = "FELIX GOLD AUTO", group = "Auto")
@Disabled
public class GoldAuto extends FelixAuto {
    private KNO3Vision vision;


    @Override
    public void postInit() {
        super.postInit();
        hang.setLandState(HangSystem.LandPosition.LOCKED);
        hang.hangLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        vision = new KNO3Vision(this, "Webcam 1");

        Threading.async(() -> {
            while(!vision.getDetector().isFound()) {
                if(isStopRequested()) {
                    return;
                }
                Thread.yield();
            }
            while(!isStarted() && !isStopRequested()) {
                CroppingGoldDetector.GoldLocation location = vision.getDetector().getLastOrder();
                telemetry.addData("current location", vision.getDetector().getCurrentOrder().toString());
                telemetry.addData("last location", vision.getDetector().getLastOrder().toString());
                telemetry.addData("gold x", vision.getDetector().getGoldRectX());
                telemetry.update();
                Thread.yield();
            }
        });

        drive.imu.zeroHeading();
        AutoTransitioner.transitionOnStop(this, "Felix Teleop");
        hopper.setFlipperState(HopperSystem.FlipperPosition.IDLE);
    }

    @Override
    public void main(){

        vision.getDetector().disable();
        CroppingGoldDetector.GoldLocation location = vision.getDetector().getLastOrder();
        intake.intakeSlides.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        hang.setLandState(HangSystem.LandPosition.OPEN);
        waitFor(0.5);
        hang.hangLift.setPower(1);
        hang.raiseHang();

        while(opModeIsActive() && !hang.isHangUp()) {
            waitFor(0.05);
        }
        hang.setHangState(HangSystem.HangPosition.OPEN);
        waitFor(.5);

        drive.driveForAlt(15);
        drive.stop();

        hang.setHangState(HangSystem.HangPosition.CLOSE);
        hang.lowerHang();

        int intakePos;

        waitFor(.15);
        switch(location) {
            case LEFT:
                drive.turnPID(318);
                drive.stop();
                waitFor(.1);
                intakePos = 250;
                break;
            case RIGHT:
                drive.turnPID(42);
                drive.stop();
                waitFor(.1);
                intakePos = 250;
                break;
            case CENTER:
            case UNKNOWN:
            default:
                intakePos = 175;
                break;
        }

        // Extend and retract intake
        intake.collector.setPower(1);

        intake.setIntakeFlipperState(IntakeSystem.IntakeFlipperState.DOWN);
        intake.intakeSlides.setTargetPosition(intake.intakeSlides.getCurrentPosition() - intakePos);
        intake.intakeSlides.setPower(-.5);
        while(opModeIsActive() && intake.intakeSlides.isBusy()) {
            waitFor(0.05);
        }
        intake.intakeSlides.setPower(0);

        waitFor(1);

        intake.setIntakeFlipperState(IntakeSystem.IntakeFlipperState.UP);
        intake.collector.setPower(0);
        intake.intakeSlides.setTargetPosition(intake.intakeSlideZero);
        intake.intakeSlides.setPower(.5);
        while(opModeIsActive() && intake.intakeSlides.isBusy()) {
            waitFor(0.05);
        }
        intake.intakeSlides.setPower(1);

        waitFor(.1);



        FelixPersist.lastWasAuto = true;

    }
}

