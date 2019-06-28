package net.kno3.season.roverruckus.felix.program.auto.goldside;

import com.qualcomm.robotcore.hardware.DcMotor;

import net.kno3.season.roverruckus.felix.program.auto.FelixAuto;
import net.kno3.season.roverruckus.felix.program.dogecvtests.CroppingGoldDetector;
import net.kno3.season.roverruckus.felix.program.dogecvtests.KNO3Vision;
import net.kno3.season.roverruckus.felix.robot.HangSystem;
import net.kno3.season.roverruckus.felix.robot.HopperSystem;
import net.kno3.season.roverruckus.felix.robot.IntakeSystem;
import net.kno3.util.AutoTransitioner;
import net.kno3.util.Threading;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class GoldAutoBase extends FelixAuto {
    private KNO3Vision vision;
    protected CroppingGoldDetector.GoldLocation location;

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
        intake.setIntakeTransferState(IntakeSystem.IntakeTransferState.IN);
    }

    protected void landDriveForward() {
        Threading.async(() -> vision.getDetector().disable());
        location = vision.getDetector().getLastOrder();
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
    }

    protected void sample() {
        int intakePos;

        waitFor(.15);
        switch(location) {
            case LEFT:
                drive.turnPID(322);
                drive.stop();
                waitFor(.1);
                intakePos = 250;
                break;
            case RIGHT:
                drive.turnPID(44);
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
        while(opModeIsActive() && intake.getIntakeSlideEncoder() > 10-intakePos) {
            waitFor(0.05);
        }
        intake.intakeSlides.setPower(0);

        waitFor(0.2);

    }

    protected void retractIntake() {
        intake.setIntakeFlipperState(IntakeSystem.IntakeFlipperState.UP);
        intake.collector.setPower(0);
        intake.intakeSlides.setTargetPosition(intake.intakeSlideZero);
        intake.intakeSlides.setPower(.5);
        while (opModeIsActive() && intake.intakeSlides.isBusy()) {
            waitFor(0.05);
        }
        intake.intakeSlides.setPower(1);
    }

    protected void dumpMarker() {
        drive.turnPID(355);
        drive.stop();

        intake.intakeSlides.setPower(-.4);
        intake.intakeSlides.setTargetPosition(intake.intakeSlideZero - 750);
        while(opModeIsActive() && intake.getIntakeSlideEncoder() > -720) {
            waitFor(0.05);
        }

        waitFor(0.2);
        intake.setMarkerDumpState(IntakeSystem.MarkerDumpState.DOWN);
        waitFor(0.5);
        intake.setMarkerDumpState(IntakeSystem.MarkerDumpState.UP);


    }

    protected void score() {
        AtomicBoolean readyDump = new AtomicBoolean(false);

        Threading.async(() -> {
            retractIntake();
            intake.setIntakeTransferState(IntakeSystem.IntakeTransferState.OUT);
            intake.collect(0.8);
            waitFor(0.5);
            intake.collect(0);
            while(opModeIsActive() && hopper.getEncoder() < 700) {
                if (hopper.getEncoder() >= 700) {
                    hopper.hopperLift.setPower(0.5);
                } else {
                    double liftPower = (700 - hopper.getEncoder()) / 60 + .6;
                    liftPower = liftPower > 1 ? 1 : liftPower;
                    hopper.hopperLift.setPower(liftPower);
                }
                waitFor(0.05);
            }
            hopper.hopperLift.setPower(0.5);
            readyDump.set(true);
        });
        drive.turnPID(10);
        waitFor(0.1);
        drive.driveForAlt(-9.5);
        drive.stop();
        waitFor(0.1);
        while (opModeIsActive() && !readyDump.get()) waitFor(0.05);
        hopper.setFlipperState(HopperSystem.FlipperPosition.UP);
        intake.setIntakeTransferState(IntakeSystem.IntakeTransferState.IN);
        waitFor(1);
        hopper.setFlipperState(HopperSystem.FlipperPosition.DOWN);
        Threading.async(() -> {
            while(opModeIsActive() && hopper.getEncoder() > 2) {
                if (hopper.getEncoder() <= 2) {
                    hopper.hopperLift.setPower(0);
                } else {
                    double liftPower = (2 - hopper.getEncoder()) / 250 - .1;
                    liftPower = liftPower < -0.3 ? -0.3 : liftPower;
                    hopper.hopperLift.setPower(liftPower);
                }
                waitFor(0.05);
            }
            hopper.hopperLift.setPower(0);
        });
        drive.driveForAlt(10.5);
        drive.stop();
        waitFor(0.5);
    }

    protected void park() {
        drive.turnPID(280);
        drive.stop();
        waitFor(.1);

        drive.driveForAlt(38);
        drive.stop();
        waitFor(.1);

        drive.turnPID(250);
        drive.stop();
        waitFor(.1);

        intake.intakeSlides.setTargetPosition(intake.intakeSlides.getCurrentPosition() - 270);
        intake.intakeSlides.setPower(-.3);
        while(opModeIsActive() && intake.getIntakeSlideEncoder() > -250) {
            waitFor(0.05);
        }
        intake.intakeSlides.setPower(0);

        waitFor(.2);
        intake.setIntakeFlipperState(IntakeSystem.IntakeFlipperState.DOWN);
        intake.collect(.7);
    }




}

