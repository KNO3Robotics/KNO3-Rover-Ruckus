package net.kno3.season.roverruckus.felix.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import net.kno3.robot.Robot;
import net.kno3.robot.SubSystem;

public class HangSystem extends SubSystem {
    public DcMotor hangLift;
    private int hangOffset = 0;
    public Servo hangServo, land;
    private IntakeSystem intakeSystem;
    private LEDSystem ledSystem;

    public boolean hangMode = false;
    private boolean hanging = false;
    private boolean hangLatch = false;

    private boolean startLock = false;
    private boolean hangLock = false;
    private boolean bLock = false;



    public HangSystem(Robot robot) {
        super(robot);

        HangPosition.OPEN.updateSettings(robot);
        HangPosition.CLOSE.updateSettings(robot);
        LandPosition.IDLE.updateSettings(robot);
        LandPosition.OPEN.updateSettings(robot);
        LandPosition.LOCKED.updateSettings(robot);

    }
    @Override
    public void init() {
        this.hangServo = hardwareMap().servo.get(Felix.HANG_SERVO_KEY);
        this.land = hardwareMap().servo.get(Felix.LAND_SERVO_KEY);
        this.hangLift = hardwareMap().dcMotor.get(Felix.HANG_MOTOR_KEY);

        if(FelixPersist.lastWasAuto) {
            resetEncoder(FelixPersist.lastHangZero);
        } else {
            FelixPersist.lastHangZero = resetEncoder();
        }
        hangLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        hangLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hangLift.setTargetPosition(hangOffset);

        setHangState(HangPosition.CLOSE);
    }

    @Override
    public void handle() {

        //toggle hangmode
        if(gamepad1().start && !startLock) {
            hangMode = !hangMode;
            startLock = true;
        }
        if(!gamepad1().start && startLock)
            startLock = false;

        //hanging mechanism
        if(hangMode) {
            //toggleHanging
            if(gamepad1().y && !hangLock) {
                hanging = !hanging;
                hangLock = true;
            }
            if(!gamepad1().y && hangLock) {
                hangLock = false;
            }

            hangLift.setPower(1);

            if(hanging) {
                raiseHang();
            }
            else {
                lowerHang();
            }



            //toggle latch
            if(gamepad1().b && !bLock) {
                hangLatch = !hangLatch;
                bLock = true;
            }
            if(!gamepad1().b && bLock) {
                bLock = false;
            }


            if(hangLatch)
                setHangState(HangPosition.OPEN);
            else
                setHangState(HangPosition.CLOSE);

        }


    }

    @Override
    public void stop() {
        hangLift.setPower(0);
    }


    public void setHangState (HangPosition hangState) {
        this.hangServo.setPosition(hangState.getPosition());
    }

    public void setLandState(LandPosition landState) {
        this.land.setPosition(landState.getPosition());
    }

    public void raiseHang() {
        hangLift.setTargetPosition(1930 + hangOffset);
    }

    public boolean isHangUp() {
        return hangLift.getCurrentPosition() > 1920 + hangOffset;
    }

    public void lowerHang() {
        hangLift.setTargetPosition(hangOffset);
    }

    private int resetEncoder() {
        hangOffset = hangLift.getCurrentPosition();
        return hangOffset;
    }

    private void resetEncoder(int offset) {
        hangOffset = offset;
    }

    private int getEncoder() {
        return hangLift.getCurrentPosition() - hangOffset;
    }


    public enum HangPosition {
        OPEN("hang_open"),
        CLOSE("hang_close");

        private String key;
        private double pos;

        HangPosition(String key) {
            this.key = key;
        }

        public double getPosition() {
            return pos;
        }

        public void updateSettings(Robot robot) {
            this.pos = robot.settings.getDouble(key);
        }
    }

    public enum LandPosition {
        OPEN("land_open"),
        LOCKED("land_locked"),
        IDLE("land_idle");

        private String key;
        private double pos;

        LandPosition(String key) {
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