package net.kno3.season.roverruckus.felix.program.auto.silverside.old;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;

import net.kno3.season.roverruckus.felix.program.auto.FelixAuto;
import net.kno3.season.roverruckus.felix.program.dogecvtests.CroppingGoldDetector;
import net.kno3.season.roverruckus.felix.program.dogecvtests.KNO3Vision;
import net.kno3.season.roverruckus.felix.robot.HopperSystem;
import net.kno3.season.roverruckus.felix.robot.IntakeSystem;
import net.kno3.season.roverruckus.felix.robot.FelixPersist;
import net.kno3.util.AutoTransitioner;
import net.kno3.util.Threading;


@Autonomous(name = "SOLUS SILVER AUTO NO MARKER", group = "Auto")
@Disabled
public class SilverAutoNoMarker extends FelixAuto {
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
        AutoTransitioner.transitionOnStop(this, "Felix Teleop");
        hopper.setFlipperState(HopperSystem.FlipperPosition.IDLE);
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
        hopper.setFlipperState(HopperSystem.FlipperPosition.DOWN);
        hopper.hopperLift.setPower(0);
        hopper.setHangState(HopperSystem.HangPosition.OPEN);
        waitFor(0.5);

        drive.driveForAlt(15);
        drive.stop();
        hopper.setHangState(HopperSystem.HangPosition.CLOSE);
        waitFor(.3);

        intake.setSilverSortState(IntakeSystem.SilverSortState.CLOSE);

        hopper.hopperLift.setPower(-0.5);
        while (opModeIsActive() && hopper.hopperLift.getEncoder() < -10) {}
        hopper.hopperLift.setPower(0);

        waitFor(.15);
        switch(location) {
            case LEFT:
                drive.turnPIDbad(318);
                drive.stop();
                waitFor(.1);

                //extend and retract intake
                intake.collector.setPower(-.75);
                intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()-850);
                intake.intakeSlides.setPower(.7);
                waitFor(1.75);
                intake.intakeSlides.setTargetPosition(intake.intakeSlideZero);
                intake.intakeSlides.setPower(-.7);
                waitFor(1);

                break;

            case RIGHT:
                drive.turnPIDbad(42);
                drive.stop();
                waitFor(.1);

                //extend and retract intake
                intake.collector.setPower(-.75);
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
                intake.collector.setPower(-.75);
                intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()-600);
                intake.intakeSlides.setPower(.7);
                waitFor(1.75);
                intake.intakeSlides.setTargetPosition(intake.intakeSlideZero);
                intake.intakeSlides.setPower(-.7);
                waitFor(1);


                break;
        }

        intake.collector.setPower(0);


        intake.setIntakeFlipperState(IntakeSystem.IntakeFlipperState.TRANSFER);
        intake.setMarkerDumpState(IntakeSystem.MarkerDumpState.DOWN);


        intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()-1150);
        intake.intakeSlides.setPower(.7);
        waitFor(1);
        intake.intakeSlides.setPower(0);


        drive.turnPIDbad(0);
        drive.stop();
        waitFor(.1);



        FelixPersist.lastWasAuto = true;
        */
    }
}

