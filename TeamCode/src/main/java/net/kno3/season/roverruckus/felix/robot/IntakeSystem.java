package net.kno3.season.roverruckus.felix.robot;


import android.util.Log;

import com.qualcomm.hardware.hitechnic.HiTechnicNxtDcMotorController;
import com.qualcomm.hardware.lynx.LynxDcMotorController;
import com.qualcomm.hardware.motors.NeveRest20Gearmotor;
import com.qualcomm.hardware.motors.NeveRest3_7GearmotorV1;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.MotorControlAlgorithm;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import net.kno3.robot.Robot;
import net.kno3.robot.SubSystem;
import net.kno3.util.Threading;

public class IntakeSystem extends SubSystem {
    public DcMotor intakeSlides, collector;
    public Servo intakeFlipper, intakeTransfer, markerDumper;
    public int intakeSlideZero;

    private HopperSystem hopperSystem;

    public boolean silverMode = true;
    private boolean rightBumperWasPressed = false;
    private boolean wasIn = true;
    private boolean hasMovedup = false;
    private boolean yLock = false;
    private boolean intakeHasMovedOut = false;
    private int lastSlide = 0;

    private boolean intSlidePowerWasRunning = false;
    private int prevIntTarget = 0;
    private double collectPower = 0;

    private IntakeFlipperState intakeFlipperState = IntakeFlipperState.DOWN;
    private IntakeTransferState intakeTransferState = IntakeTransferState.IN;

    private Thread outtakeThread = null;
    public Thread intakeThread = null;



    public IntakeSystem(Robot robot) {
        super(robot);

        IntakeFlipperState.UP.updateSettings(robot);
        IntakeFlipperState.DOWN.updateSettings(robot);
        IntakeFlipperState.IDLE.updateSettings(robot);
        IntakeTransferState.IN.updateSettings(robot);
        IntakeTransferState.OUT.updateSettings(robot);
        IntakeTransferState.IDLE.updateSettings(robot);
        MarkerDumpState.DOWN.updateSettings(robot);
        MarkerDumpState.UP.updateSettings(robot);
        MarkerDumpState.OUT.updateSettings(robot);

    }
    @Override
    public void init() {
        this.hopperSystem = robot.getSubSystem(HopperSystem.class);

        this.intakeTransfer = hardwareMap().servo.get(Felix.INTAKE_TRANSFER_KEY);
        this.intakeFlipper = hardwareMap().servo.get(Felix.INTAKE_FLIPPER_KEY);
        this.markerDumper = hardwareMap().servo.get(Felix.TEAM_MARKER_DUMPER_KEY);

        intakeSlides = hardwareMap().dcMotor.get(Felix.INTAKE_SLIDE_MOTOR_KEY);
        collector = hardwareMap().dcMotor.get(Felix.COLLECTOR_KEY);
        collector.setDirection(DcMotorSimple.Direction.REVERSE);

        intakeSlides.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        intakeSlides.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //intakeSlides.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeSlides.setDirection(DcMotorSimple.Direction.FORWARD);


        zeroIntakeSlideEnc();
        prevIntTarget = getIntakeSlideEncoder() + intakeSlideZero;
        telemetry().addData("intake slide zero", intakeSlideZero);
        telemetry().addData("intake slide pos", getIntakeSlideEncoder());

        //PIDFCoefficients pid = new PIDFCoefficients(15, 0.01, 0.01, 0, MotorControlAlgorithm.PIDF);
        //((LynxDcMotorController) intakeSlides.getController()).setPIDFCoefficients(intakeSlides.getPortNumber(), DcMotor.RunMode.RUN_TO_POSITION, pid);

        setMarkerDumpState(MarkerDumpState.UP);

        setIntakeFlipperState(IntakeFlipperState.UP);

    }

    @Override
    public void handle() {
        double intSlidePower = gamepad2().left_stick_y;


        if(intSlidePower >= 0.1 || intSlidePower <= -0.1) {
            intSlidePower *= .7;
        } else {
            intSlidePower *= 0;
        }

        if(!gamepad2().x) {
            if (hasMovedup && intSlidePower > 0 && (getIntakeSlideEncoder()) >= -10) {
                intSlidePower = 0;
            }
            if (intSlidePower < 0 && (getIntakeSlideEncoder()) <= -760) {
                intSlidePower = 0;
            }
            if (getIntakeSlideEncoder() >= 100) {
                hasMovedup = true;
            }
        }

        if(!gamepad2().x && rightBumperWasPressed) {
            zeroIntakeSlideEnc();
            rightBumperWasPressed = false;
        }
        else if(gamepad2().x) {
            rightBumperWasPressed = true;
        }

        if(getIntakeSlideEncoder() < -760 && intSlidePower < 0) {
            intSlidePower = 0;
        }

        if(getIntakeSlideEncoder() > -20 && Math.abs(intSlidePower) < 0.1) {
            intakeSlides.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            intakeSlides.setTargetPosition(intakeSlideZero+20);
            intakeSlides.setPower(1);
        } else {
            intakeSlides.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            intakeSlides.setPower(intSlidePower);
        }

        telemetry().addData("Intake Slides raw pos: ", intakeSlides.getCurrentPosition());
        telemetry().addData("intake slide zero", intakeSlideZero);
        telemetry().addData("Intake slide pos", getIntakeSlideEncoder());
        //telemetry().addData("int pid", ((LynxDcMotorController) intakeSlides.getController()).getPIDFCoefficients(intakeSlides.getPortNumber(), DcMotor.RunMode.RUN_TO_POSITION));
        // P=10 i=0.05, d=0, f=0, alg=LegacyPID

        //Intake Escapement Override
        if(gamepad2().dpad_left){
            intakeTransferState = IntakeTransferState.OUT;
        }

        //Intake Flipper Up
        if(gamepad2().b) {
            //setIntakeFlipperState(IntakeFlipperState.UP);
            intakeFlipperState = IntakeFlipperState.UP;
            outtake();
        }

        //Intake Flipper Down
        if(gamepad2().a) {
            //setIntakeFlipperState(IntakeFlipperState.DOWN);
            intakeFlipperState = IntakeFlipperState.DOWN;
            intakeTransferState = IntakeTransferState.IN;
            outtakeThread = null;
        }

        //Manual control over Intake Transfer for testing
        /*
        if(gamepad2().y && !yLock) {
            intakeTransferState = IntakeTransferState.OUT;;
            yLock = true;
        }
        if(!gamepad2().y && yLock) {
            intakeTransferState = IntakeTransferState.IN;
            yLock = false;
        }
        */

        //intake transfer automation
        if(getIntakeSlideEncoder() >= -75 && intakeHasMovedOut) {
            intakeHasMovedOut = false;
            intakeFlipperState = IntakeFlipperState.UP;
            transferIntake();
        }
        if(getIntakeSlideEncoder() <= -120) {
            intakeTransferState = IntakeTransferState.IN;
            intakeHasMovedOut = true;
            intakeThread = null;
        }



        //Collector
        collectPower = gamepad2().right_trigger - gamepad2().left_trigger;
        if(outtakeThread == null && intakeThread == null)
            collect(collectPower);


        setIntakeFlipperState(intakeFlipperState);
        setIntakeTransferState(intakeTransferState);

        lastSlide = getIntakeSlideEncoder();

    }
    /*
    @Override
    public void handle() {
        double intSlidePower = gamepad2().left_stick_y;


        if(intSlidePower >= 0.1 || intSlidePower <= -0.1) {
            intSlidePower *= 1;
        } else {
            intSlidePower *= 0;
        }

        if(getIntakeSlideEncoder() < -760 && intSlidePower < 0) {
            intSlidePower = 0;
        }

        if(getIntakeSlideEncoder() > -50 && intSlidePower < -0.1) {
            intakeSlides.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            intakeSlides.setTargetPosition(intakeSlideZero);
            intakeSlides.setPower(1);
        } else {
            intakeSlides.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            intakeSlides.setPower(intSlidePower);
        }

        telemetry().addData("Intake Slides raw pos: ", intakeSlides.getCurrentPosition());
        telemetry().addData("intake slide zero", intakeSlideZero);
        telemetry().addData("Intake slide pos", getIntakeSlideEncoder());
    }*/

    @Override
    public void stop() {
        collector.setPower(0);
        intakeSlides.setPower(0);
    }

    public void outtake() {
        synchronized(this) {
            if(outtakeThread != null) {
                return;
            }
            this.outtakeThread = Threading.async(() -> {
                Threading.delay(0.04, robot);

                collect(-.4);

                Threading.delay(.3, robot);

                outtakeThread = null;
            });
        }
    }

    public void transferIntake() {
        synchronized(this) {
            if(intakeThread != null) {
                return;
            }
            this.intakeThread = Threading.async(() -> {
                intakeTransferState = IntakeTransferState.OUT;
                collect(.8);

                Threading.delay(0.5, robot);

                intakeThread = null;
            });

        }
    }


    public int getIntakeSlideEncoder() {
        return intakeSlides.getCurrentPosition() - intakeSlideZero;
    }

    public void zeroIntakeSlideEnc() {
        if(FelixPersist.lastWasAuto) {
            Log.d("KNO3Encoder", "last was auto, intake slide zero is " + FelixPersist.lastIntakeZero);
            this.intakeSlideZero = FelixPersist.lastIntakeZero;
        } else {
            this.intakeSlideZero = intakeSlides.getCurrentPosition();
            FelixPersist.lastIntakeZero = this.intakeSlideZero;
            Log.d("KNO3Encoder", "last was NOT auto, intake slide zero is " + this.intakeSlideZero);
        }
    }




    public void collect(double speed) {
        this.collector.setPower(speed);
    }

    public void setIntakeFlipperState(IntakeFlipperState state) {
        intakeFlipper.setPosition(state.getPosition());
    }

    public void setIntakeTransferState(IntakeTransferState state) {
        intakeTransfer.setPosition(state.getPosition());
    }

    public void setMarkerDumpState(MarkerDumpState state) {
        markerDumper.setPosition(state.getPosition());
    }

    public enum IntakeTransferState {
        IDLE("intake_transfer_idle"),
        IN("intake_transfer_in"),
        OUT("intake_transfer_out");

        private String key;
        private double pos;

        IntakeTransferState(String key) {
            this.key = key;
        }

        public double getPosition() {
            return pos;
        }

        public void updateSettings(Robot robot) {
            this.pos = robot.settings.getDouble(key);
        }
    }

    public enum IntakeFlipperState {
        UP("intake_flipper_up"),
        DOWN("intake_flipper_down"),
        IDLE("intake_flipper_idle");

        private String key;
        private double pos;

        IntakeFlipperState(String key) {
            this.key = key;
        }

        public double getPosition() {
            return pos;
        }

        public void updateSettings(Robot robot) {
            this.pos = robot.settings.getDouble(key);
        }
    }

    public enum MarkerDumpState {
        UP("marker_dumper_up"),
        OUT("marker_dumper_out"),
        DOWN("marker_dumper_down");

        private String key;
        private double pos;

        MarkerDumpState(String key) {
            this.key = key;
        }

        public double getPosition() {
            return pos;
        }

        public void updateSettings(Robot robot) {
            this.pos = robot.settings.getDouble(key);
        }
    }
}