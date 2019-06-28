package net.kno3.season.roverruckus.felix.program.dogecvtests;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;

import net.kno3.season.roverruckus.felix.program.auto.FelixAuto;
import net.kno3.season.roverruckus.felix.robot.IntakeSystem;

import net.kno3.util.AutoTransitioner;
import net.kno3.util.Threading;

@Disabled
@Autonomous(name = "VISION TEST Silver TestAuto")
public class KNO3VisionTest extends FelixAuto {
    private KNO3Vision vision;


    @Override
    public void postInit() {
        super.postInit();
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

        AutoTransitioner.transitionOnStop(this, "Felix Teleop");

    }

    @Override
    public void main(){
        /*
        vision.getDetector().disable();
        CroppingGoldDetector.GoldLocation location = vision.getDetector().getLastOrder();
        intake.intakeSlides.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        drive.driveForAlt(15);
        drive.stop();
        waitFor(.5);

        switch(location) {
            case LEFT:
                drive.turnPIDbad(315);
                drive.stop();
                waitFor(.1);

                //extend and retract intake
                intake.collector.setPower(-1);
                intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()-850);
                intake.intakeSlides.setPower(.5);
                waitFor(2);
                intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()+850);
                intake.intakeSlides.setPower(-.5);
                waitFor(1.5);

                intake.collector.setPower(0);

                intake.intakeSlides.setPower(0);

                drive.turnPIDbad(315);
                drive.stop();
                waitFor(.5);
                break;

            case RIGHT:
                drive.turnPIDbad(45);
                drive.stop();
                waitFor(.5);

                //extend and retract intake
                intake.collector.setPower(-1);
                intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()-850);
                intake.intakeSlides.setPower(.5);
                waitFor(2);
                intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()+850);
                intake.intakeSlides.setPower(-.5);
                waitFor(1.5);
                intake.collector.setPower(0);
                intake.intakeSlides.setPower(0);


                drive.turnPIDbad(230);
                drive.stop();
                waitFor(.5);
                break;

            case CENTER:
            case UNKNOWN:
            default:

                //extend and retract intake
                intake.collector.setPower(-.5);
                intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()-650);
                intake.intakeSlides.setPower(.5);
                waitFor(2);
                intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()+650);
                intake.intakeSlides.setPower(-.5);
                waitFor(1.5);

                intake.collector.setPower(0);
                intake.intakeSlides.setPower(0);


                drive.turnPIDbad(273);
                drive.stop();
                waitFor(.5);
                break;
        }

        drive.driveForAlt(45.5);
        drive.stop();
        waitFor(.5);

        drive.turnPIDbad(318);
        drive.stop();
        waitFor(.5);

        //40
        drive.driveForAlt(5);
        drive.stop();
        waitFor(.5);

        intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()-1100);
        intake.intakeSlides.setPower(.5);
        waitFor(2);

        intake.setMarkerDumpState(IntakeSystem.MarkerDumpState.DOWN);
        waitFor(1);


        intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()+1100);
        intake.intakeSlides.setPower(-.5);
        waitFor(2);


        intake.setMarkerDumpState(IntakeSystem.MarkerDumpState.UP);

        drive.driveForAlt(-20);
        drive.stop();
        waitFor(.5);

        drive.turnPIDbad(165);
        drive.stop();
        waitFor(.5);

        intake.setIntakeFlipperState(IntakeSystem.IntakeFlipperState.TRANSFER);

        intake.intakeSlides.setTargetPosition(intake.getIntakeSlideEncoder()-550);
        intake.intakeSlides.setPower(.5);
        waitFor(2);
        intake.intakeSlides.setPower(0);

        //FelixPersist.lastWasAuto = true;
        //FelixPersist.lastIntakeZero = intake.intakeSlideZero;


        */
    }
}

