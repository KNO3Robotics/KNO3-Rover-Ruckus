package net.kno3.season.roverruckus.felix.program.auto.goldside;


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

@Autonomous(name = "SOLUS GOLD AUTO NO PARK", group = "Auto")
@Disabled
public class GoldAutoNoPark extends FelixAuto {
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

        hopper.setSweeperState(HopperSystem.SweeperPosition.IN);
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

        Threading.async( () -> {
            hopper.hopperLift.setPower(-0.5);
            while (opModeIsActive() && hopper.hopperLift.getEncoder() < -10) {}
            hopper.hopperLift.setPower(0);
        });

        intake.setSilverSortState(IntakeSystem.SilverSortState.OPEN);

        switch(location) {
            case LEFT:
                drive.turnPID(318);
                drive.stop();
                waitFor(.1);

                //extend and retract intake
                intake.collector.setPower(-.6);
                intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()-850);
                intake.intakeSlides.setPower(.7);
                waitFor(1.75);
                intake.intakeSlides.setTargetPosition(intake.intakeSlideZero);
                intake.intakeSlides.setPower(-.7);
                waitFor(1);


                intake.collector.setPower(0);

                drive.turnPID(5);
                drive.stop();
                waitFor(.1);
                break;

            case RIGHT:
                drive.turnPID(42);
                drive.stop();
                waitFor(.5);

                //extend and retract intake
                intake.collector.setPower(-.6);
                intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()-850);
                intake.intakeSlides.setPower(.7);
                waitFor(1.75);
                intake.intakeSlides.setTargetPosition(intake.intakeSlideZero);
                intake.intakeSlides.setPower(-.7);
                waitFor(1);
                intake.collector.setPower(0);


                drive.turnPID(5);
                drive.stop();
                waitFor(.1);
                break;

            case CENTER:
            case UNKNOWN:
            default:

                //extend and retract intake
                intake.collector.setPower(-.6);
                intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()-650);
                intake.intakeSlides.setPower(.7);
                waitFor(1.75);
                intake.intakeSlides.setTargetPosition(intake.intakeSlideZero);
                intake.intakeSlides.setPower(-.7);
                waitFor(1);

                intake.collector.setPower(0);

                drive.turnPID(5);
                drive.stop();
                waitFor(.1);
                break;

        }


        intake.setIntakeFlipperState(IntakeSystem.IntakeFlipperState.TRANSFER);

        intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()-1100);
        intake.intakeSlides.setPower(.7);
        waitFor(1);
        intake.setMarkerDumpState(IntakeSystem.MarkerDumpState.DOWN);
        waitFor(.5);
        intake.setGoldSortState(IntakeSystem.GoldSortState.OPEN);
        waitFor(.5);
        intake.setGoldSortState(IntakeSystem.GoldSortState.CLOSE);

        intake.intakeSlides.setTargetPosition(intake.intakeSlideZero);
        intake.intakeSlides.setPower(-.7);
        waitFor(1);

        intake.setIntakeFlipperState(IntakeSystem.IntakeFlipperState.DOWN);
        intake.setMarkerDumpState(IntakeSystem.MarkerDumpState.UP);


        FelixPersist.lastWasAuto = true;

    */
    }
}

