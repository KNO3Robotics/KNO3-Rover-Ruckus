package net.kno3.season.roverruckus.felix.program.dogecvtests;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;

import net.kno3.season.roverruckus.felix.program.auto.FelixAuto;
import net.kno3.season.roverruckus.felix.robot.HopperSystem;
import net.kno3.season.roverruckus.felix.robot.IntakeSystem;
import net.kno3.util.Threading;

@Disabled
@Autonomous(name = "VISION TEST Silver TestAuto WLAND")
public class KNO3VisionTestWLand extends FelixAuto {
    private KNO3Vision vision;


    @Override
    public void postInit() {
        /*
        super.postInit();
        hopper.setLandState(HopperSystem.LandPosition.LOCKED);

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
        //AutoTransitioner.transitionOnStop(this, "Felix Teleop");
        */
    }

    @Override
    public void main(){
        /*
        vision.getDetector().disable();
        CroppingGoldDetector.GoldLocation location = vision.getDetector().getLastOrder();
        intake.intakeSlides.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        hopper.setLandState(HopperSystem.LandPosition.OPEN);
        hopper.hopperLift.setPower(-.9);
        waitFor(0.5);
        hopper.hopperLift.setPower(0.5);
        while(opModeIsActive() && hopper.hopperLift.getEncoder() > -2870) {
            hopper.hopperLift.setPower(0);
            hopper.hopperLift.setPower(0.5);
            waitFor(0.05);
        }
        hopper.hopperLift.setPower(0);
        hopper.setHangState(HopperSystem.HangPosition.OPEN);
        waitFor(2.7);
        hopper.setHangState(HopperSystem.HangPosition.CLOSE);

        drive.driveForAlt(15);
        drive.stop();

        hopper.hopperLift.setPower(-0.5);
        while (opModeIsActive() && hopper.hopperLift.getEncoder() < -10) {}
        hopper.hopperLift.setPower(0);

        waitFor(.15);
        switch(location) {
            case LEFT:
                drive.turnPIDbad(315);
                drive.stop();
                waitFor(.1);

                //extend and retract intake
                intake.collector.setPower(-1);
                intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()-850);
                intake.intakeSlides.setPower(.7);
                waitFor(1.75);
                intake.intakeSlides.setTargetPosition(intake.intakeSlideZero);
                intake.intakeSlides.setPower(-.7);
                waitFor(1);

                break;

            case RIGHT:
                drive.turnPIDbad(45);
                drive.stop();
                waitFor(.1);

                //extend and retract intake
                intake.collector.setPower(-1);
                intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()-850);
                intake.intakeSlides.setPower(.7);
                waitFor(1.75);
                intake.intakeSlides.setTargetPosition(intake.intakeSlideZero);
                intake.intakeSlides.setPower(-.7);
                waitFor(1);

                break;

            case CENTER:
            case UNKNOWN:
            default:

                //extend and retract intake
                intake.collector.setPower(-1);
                intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()-600);
                intake.intakeSlides.setPower(.7);
                waitFor(1.75);
                intake.intakeSlides.setTargetPosition(intake.intakeSlideZero);
                intake.intakeSlides.setPower(-.7);
                waitFor(1);


                break;
        }

        intake.collector.setPower(0);

        drive.turnPIDbad(273);
        drive.stop();
        waitFor(.1);

        if(location == CroppingGoldDetector.GoldLocation.RIGHT)
            drive.driveForAlt(25);
        else
            drive.driveForAlt(47);

        drive.stop();
        waitFor(.5);

        if(location == CroppingGoldDetector.GoldLocation.RIGHT)
            drive.turnPIDbad(242);
        else
            drive.turnPIDbad(230);

        drive.stop();
        waitFor(.5);

        if(location != CroppingGoldDetector.GoldLocation.RIGHT) {
            intake.setIntakeFlipperState(IntakeSystem.IntakeFlipperState.TRANSFER);
            intake.collector.setPower(-1);
        }
        //40

        intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()-1150);
        intake.intakeSlides.setPower(.7);
        if(location == CroppingGoldDetector.GoldLocation.RIGHT)
            drive.driveForAlt(23);
        else
            drive.driveForAlt(5);
        drive.stop();
        if(location == CroppingGoldDetector.GoldLocation.RIGHT)
            intake.setIntakeFlipperState(IntakeSystem.IntakeFlipperState.TRANSFER);
        intake.setGoldSortState(IntakeSystem.GoldSortState.OPEN);
        waitFor(.5);
        intake.setMarkerDumpState(IntakeSystem.MarkerDumpState.DOWN);
        waitFor(1);
        intake.setGoldSortState(IntakeSystem.GoldSortState.CLOSE);

        intake.collector.setPower(0);

        intake.intakeSlides.setTargetPosition(intake.intakeSlideZero);
        intake.intakeSlides.setPower(-.7);
        waitFor(1.5);

        intake.setIntakeFlipperState(IntakeSystem.IntakeFlipperState.DOWN);
        intake.setMarkerDumpState(IntakeSystem.MarkerDumpState.UP);

        if(location == CroppingGoldDetector.GoldLocation.RIGHT) {
            drive.turnPIDbad(30);
            drive.stop();
            waitFor(.5);

            drive.driveForAlt(27);
            drive.stop();
            waitFor(.5);
        }
        else {
            drive.driveForAlt(-23);
            drive.stop();
            waitFor(.5);

            drive.turnPIDbad(33);
            drive.stop();
            waitFor(.5);
        }
        intake.setIntakeFlipperState(IntakeSystem.IntakeFlipperState.TRANSFER);

        intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()-550);
        intake.intakeSlides.setPower(.7);
        waitFor(1);
        intake.intakeSlides.setPower(0);


        */
    }
}

