package net.kno3.season.roverruckus.felix.program.auto.silverside.old;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;

import net.kno3.season.roverruckus.felix.program.auto.FelixAuto;
import net.kno3.season.roverruckus.felix.program.dogecvtests.CroppingGoldDetector;
import net.kno3.season.roverruckus.felix.program.dogecvtests.KNO3Vision;
import net.kno3.season.roverruckus.felix.robot.FelixPersist;
import net.kno3.season.roverruckus.felix.robot.HopperSystem;
import net.kno3.season.roverruckus.felix.robot.IntakeSystem;
import net.kno3.util.AutoTransitioner;
import net.kno3.util.Threading;


@Autonomous(name = "SOLUS SILVER AUTO 2 Gold", group = "Auto")
@Disabled
public class SilverAuto2G extends FelixAuto {
    private KNO3Vision vision;
    private boolean threadFinished = false;


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

        waitFor(.1);
        switch(location) {
            case LEFT:
                drive.turnPID(318);
                drive.stop();
                waitFor(.1);

                //extend and retract intake
                intake.collector.setPower(-.7);
                intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()-850);
                intake.intakeSlides.setPower(.7);
                waitFor(1.5);
                intake.intakeSlides.setTargetPosition(intake.intakeSlideZero);
                intake.intakeSlides.setPower(-.7);
                waitFor(.7);

                break;

            case RIGHT:
                drive.turnPID(42);
                drive.stop();
                waitFor(.1);

                //extend and retract intake
                intake.collector.setPower(-.7);
                intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()-850);
                intake.intakeSlides.setPower(.7);
                waitFor(1.5);
                intake.intakeSlides.setTargetPosition(intake.intakeSlideZero);
                intake.intakeSlides.setPower(-.7);
                waitFor(.7);

                break;

            case CENTER:
            case UNKNOWN:
            default:

                //extend and retract intake
                intake.collector.setPower(-.7);
                intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()-500);
                intake.intakeSlides.setPower(.7);
                waitFor(1.5);
                intake.intakeSlides.setTargetPosition(intake.intakeSlideZero);
                intake.intakeSlides.setPower(-.7);
                waitFor(.7);


                break;
        }

        intake.collector.setPower(0);

        drive.turnPID(271);
        drive.stop();
        waitFor(.1);

        drive.driveForAlt(45);
        drive.stop();
        waitFor(.1);



        //for masquerade  REMOVE LATER
        //waitFor(1);

        switch(location) {
            case RIGHT:

                drive.turnPID(187);
                drive.stop();
                waitFor(.1);


                intake.collector.setPower(-.8);
                intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()-400); //find real encoder value
                intake.intakeSlides.setPower(.7);

                waitFor(1.5);



                drive.turnPID(232);
                drive.stop();

                intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()-650);

                intake.collector.setPower(0);
                waitFor(0.5);

                break;

            case LEFT:

                drive.turnPID(190);
                drive.stop();
                waitFor(.1);

                intake.setIntakeFlipperState(IntakeSystem.IntakeFlipperState.TRANSFER);

                waitFor(0.5);

                intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()-1150); //find real encoder value
                intake.intakeSlides.setPower(.7);
                intake.collector.setPower(-.8);

                drive.driveForAlt(10);
                drive.stop();

                intake.setIntakeFlipperState(IntakeSystem.IntakeFlipperState.DOWN);

                drive.driveForAlt(4);
                drive.stop();
                waitFor(.5);

                intake.setIntakeFlipperState(IntakeSystem.IntakeFlipperState.TRANSFER);

                drive.driveForAlt(-14);
                drive.stop();

                drive.turnPID(232);
                drive.stop();

                intake.collector.setPower(0);
                waitFor(.5);

                break;

            case CENTER:

                drive.turnPID(187);
                drive.stop();
                waitFor(.1);

                intake.setIntakeFlipperState(IntakeSystem.IntakeFlipperState.TRANSFER);

                waitFor(0.5);

                intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()-1000); //find real encoder value
                intake.intakeSlides.setPower(.7);
                intake.collector.setPower(-.8);
                waitFor(.5);

                intake.setIntakeFlipperState(IntakeSystem.IntakeFlipperState.DOWN);
                waitFor(0.4);

                drive.turnPID(232);
                drive.stop();

                waitFor(.5);

                break;




        }

        intake.setIntakeFlipperState(IntakeSystem.IntakeFlipperState.TRANSFER);
        //40

        intake.collector.setPower(0);

        drive.driveForAlt(3);
        drive.stop();
        intake.setMarkerDumpState(IntakeSystem.MarkerDumpState.DOWN);
        waitFor(0.5);


        intake.intakeSlides.setTargetPosition(intake.intakeSlideZero);
        intake.intakeSlides.setPower(-.8);

        Threading.async( () -> {
            waitFor(0.7);
            intake.setIntakeFlipperState(IntakeSystem.IntakeFlipperState.UP);
        });


        drive.turnPID(259);
        drive.stop();
        waitFor(.1);
        drive.driveForAlt(-40);
        drive.stop();
        waitFor(.1);

        drive.turnPID(355);
        drive.stop();
        waitFor(.1);

        hopper.setRoofState(HopperSystem.RoofPosition.GOLD);

        intake.setIntakeFlipperState(IntakeSystem.IntakeFlipperState.TRANSFER);

        Threading.async( () -> {
            intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()-1150);
            intake.intakeSlides.setPower(.7);
            waitFor(1);
            intake.intakeSlides.setPower(0);
        });

        Threading.async( () -> {
            hopper.hopperLift.setPower(0.8);
            while (opModeIsActive() && hopper.hopperLift.getEncoder() > -2600) {}
            hopper.hopperLift.setPower(0);
            waitFor(0.5);
            hopper.setFlipperState(HopperSystem.FlipperPosition.GOLD);
            waitFor(.5);
            hopper.setSweeperState(HopperSystem.SweeperPosition.OUT);
            waitFor(1.5);
            hopper.setSweeperState(HopperSystem.SweeperPosition.IN);
            hopper.setFlipperState(HopperSystem.FlipperPosition.DOWN);
            hopper.setRoofState(HopperSystem.RoofPosition.OPEN);

            threadFinished = true;
        });

        drive.driveForAlt(-12);
        drive.stop();

        while(!threadFinished) {
            waitFor(0.1);
        }

        FelixPersist.lastWasAuto = true;
        */
    }
}

