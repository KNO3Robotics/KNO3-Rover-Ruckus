package net.kno3.season.roverruckus.felix.program.auto.silverside;

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

public abstract class SilverAutoBase extends FelixAuto {
    private KNO3Vision vision;
    private boolean hasScoredOnce = false;
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

    protected void landToMarkerAndSample() {
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

        drive.driveForAlt(10);
        drive.stop();

        hang.setHangState(HangSystem.HangPosition.CLOSE);
        hang.lowerHang();

        drive.turnPID(290);
        drive.stop();
        waitFor(.1);

        drive.driveForAlt(42);
        drive.stop();
        waitFor(.1);

        drive.turnPID(235);
        drive.stop();
        waitFor(.1);

        intake.intakeSlides.setTargetPosition(intake.intakeSlides.getCurrentPosition() - 640);
        intake.intakeSlides.setPower(-.3);

        drive.driveForAlt(8.5);
        drive.stop();

        while(opModeIsActive() && intake.getIntakeSlideEncoder() > -620) {
            waitFor(0.05);
        }

        intake.setMarkerDumpState(IntakeSystem.MarkerDumpState.DOWN);
        waitFor(0.7);
        intake.setMarkerDumpState(IntakeSystem.MarkerDumpState.UP);

        intake.intakeSlides.setTargetPosition(intake.intakeSlideZero);
        intake.intakeSlides.setPower(.4);
        waitFor(.3);

        drive.turnPID(270);
        drive.stop();
        waitFor(.1);

        drive.driveForAlt(-44.5);
        drive.stop();

        int intakePos;
        waitFor(.15);
        switch(location) {
            case LEFT:
                drive.turnPID(320);
                drive.stop();
                waitFor(.1);
                intakePos = 240;
                break;
            case RIGHT:
                drive.turnPID(42);
                drive.stop();
                waitFor(.1);
                intakePos = 220;
                break;
            case CENTER:
            case UNKNOWN:
            default:
                drive.turnPID(0);
                drive.stop();
                waitFor(.1);
                intakePos = 175;
                break;
        }

        // Extend and retract intake
        intake.collector.setPower(1);

        intake.setIntakeFlipperState(IntakeSystem.IntakeFlipperState.DOWN);
        intake.intakeSlides.setTargetPosition(intake.intakeSlides.getCurrentPosition() - intakePos);
        intake.intakeSlides.setPower(-.5);
        while(opModeIsActive() && intake.getIntakeSlideEncoder() > 10 - intakePos) {
            waitFor(0.05);
        }
        intake.intakeSlides.setPower(0);

        waitFor(0.2);

    }

    protected void sample() {
        int intakePos;

        waitFor(.15);
        switch(location) {
            case LEFT:
                drive.turnPID(320);
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
        drive.turnPID(15);
        waitFor(0.1);
        drive.driveForAlt(-13.5);
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
        drive.driveForAlt(12);
        drive.stop();
        waitFor(0.5);
    }

    protected void scoreTest() {
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
        if(!hasScoredOnce) {
            drive.turnPID(20);
        }
        waitFor(0.1);
        drive.driveForAlt(-11);
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

        if(hasScoredOnce) {
            Threading.async( ()-> park());
        }

        drive.driveForAlt(11);
        drive.stop();
        waitFor(0.2);



        hasScoredOnce = true;
    }

    protected void acquireMinerals() {
        intake.intakeSlides.setTargetPosition(intake.intakeSlides.getCurrentPosition() - 430);
        intake.intakeSlides.setPower(-.4);
        while(opModeIsActive() && intake.getIntakeSlideEncoder() > -410) {
            waitFor(0.05);
        }

        intake.setIntakeFlipperState(IntakeSystem.IntakeFlipperState.DOWN);
        intake.collect(1);
        waitFor(.4);
        intake.intakeSlides.setTargetPosition(intake.intakeSlides.getCurrentPosition() - 520);
        waitFor(.8);
        intake.setIntakeFlipperState(IntakeSystem.IntakeFlipperState.UP);
        intake.collect(-.4);
        waitFor(.3);
    }


    protected void alignMarker(boolean turnToMarker) {
        drive.turnPID(282);
        drive.stop();
        waitFor(.1);

        if(turnToMarker) {
            drive.driveForAlt(43 );
            drive.stop();
            waitFor(.5);

            drive.turnPID(230);
            drive.stop();
            waitFor(.5);
        } else {

            drive.driveForAlt(45 );
            drive.stop();
            waitFor(.5);
        }
    }

    protected void alignDoubleSample() {
        alignMarker(false);
        switch (location) {
            case RIGHT:
                drive.turnPID(207);
                break;
            case LEFT:
                drive.turnPID(198);
                break;
            case CENTER:
            case UNKNOWN:
            default:
                drive.turnPID(198);
                break;
        }
    }

    protected void doubleSample() {
        int intakePos;
        int intakeDrop;

        switch(location) {
            case LEFT:
                intakePos = 750;
                intakeDrop = 550;
                drive.driveFor(7.5);
                break;
            case RIGHT:
                intakePos = 250;
                intakeDrop = 0;
                break;
            case CENTER:
            case UNKNOWN:
            default:
                intakePos = 550;
                intakeDrop = 300;
                break;
        }

        // Extend and retract intake
        intake.collector.setPower(1);

        intake.intakeSlides.setTargetPosition(intake.intakeSlides.getCurrentPosition() - intakeDrop);
        intake.intakeSlides.setPower(-.3);
        while(opModeIsActive() && intake.getIntakeSlideEncoder() > 10-intakeDrop) {
            waitFor(0.05);
        }

        Threading.async( () -> {
            drive.turnPID(197);
            drive.stop();
        });
        intake.intakeSlides.setPower(0);
        intake.setIntakeFlipperState(IntakeSystem.IntakeFlipperState.DOWN);
        waitFor(0.1);
        intake.intakeSlides.setTargetPosition(intake.intakeSlides.getCurrentPosition() - intakePos);
        intake.intakeSlides.setPower(-.3);
        while(opModeIsActive() && intake.getIntakeSlideEncoder() > 10-intakePos) {
            waitFor(0.05);
        }
        intake.intakeSlides.setPower(0);
        waitFor(0.5);

        if(location == CroppingGoldDetector.GoldLocation.LEFT) {
            drive.driveFor(-5);
            drive.stop();
        }

        drive.turnPID(220);
        drive.stop();

        intake.intakeSlides.setTargetPosition(intake.intakeSlides.getCurrentPosition() - 650);
        intake.intakeSlides.setPower(0.3);

        waitFor(0.5);

        intake.setMarkerDumpState(IntakeSystem.MarkerDumpState.DOWN);
        waitFor(0.6);
        intake.setMarkerDumpState(IntakeSystem.MarkerDumpState.UP);

        intake.setIntakeFlipperState(IntakeSystem.IntakeFlipperState.UP);

        intake.intakeSlides.setTargetPosition(intake.intakeSlideZero);
        intake.intakeSlides.setPower(.3);
        while(opModeIsActive() && intake.intakeSlides.isBusy()) {
            waitFor(0.05);
        }
        intake.intakeSlides.setPower(1);
        intake.setIntakeTransferState(IntakeSystem.IntakeTransferState.OUT);
        intake.collect(0.6);
    }


    protected void scoreMarker() {
        intake.intakeSlides.setTargetPosition(intake.intakeSlides.getCurrentPosition() - 900);
        intake.intakeSlides.setPower(-.3);
        while(opModeIsActive() && intake.getIntakeSlideEncoder() > -800) {
            waitFor(0.05);
        }
        intake.setMarkerDumpState(IntakeSystem.MarkerDumpState.DOWN);

        waitFor(0.5);

        intake.setMarkerDumpState(IntakeSystem.MarkerDumpState.UP);

        intake.intakeSlides.setTargetPosition(intake.intakeSlideZero);
        intake.intakeSlides.setPower(.3);
        while(opModeIsActive() && intake.intakeSlides.isBusy()) {
            waitFor(0.05);
        }
        intake.intakeSlides.setPower(1);
    }

    protected void returnForParkDouble() {
        drive.turnPID(270);
        drive.stop();
        waitFor(.1);

        intake.collect(0);

        drive.driveForAlt(-40);
        drive.stop();
        waitFor(.2);

        drive.turnPID(15);
        drive.stop();
        waitFor(.2);
    }

    protected void scoreDouble() {
        AtomicBoolean readyDump = new AtomicBoolean(false);

        Threading.async(() -> {
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

        drive.driveForAlt(-10);
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

        Threading.async(() -> park());

    }

    protected void returnForPark() {
        drive.driveForAlt(-13);
        drive.stop();
        waitFor(.5);

        drive.turnPID(33);
        drive.stop();
        waitFor(.5);
    }


    protected void park() {
        intake.intakeSlides.setTargetPosition(intake.intakeSlides.getCurrentPosition() - 430);
        intake.intakeSlides.setPower(-.4);
        while(opModeIsActive() && intake.getIntakeSlideEncoder() > -400) {
            waitFor(0.05);
        }
        intake.intakeSlides.setPower(0);

        waitFor(.2);
        intake.setIntakeFlipperState(IntakeSystem.IntakeFlipperState.DOWN);
        intake.collect(.7);
    }


}
