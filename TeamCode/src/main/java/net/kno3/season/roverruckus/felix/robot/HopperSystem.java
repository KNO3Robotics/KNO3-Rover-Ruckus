package net.kno3.season.roverruckus.felix.robot;

import android.util.Log;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import net.kno3.robot.Robot;
import net.kno3.robot.SubSystem;
import net.kno3.util.MotorPair;
import net.kno3.util.Threading;

public class HopperSystem extends SubSystem {
    public DcMotor hopperLift;
    private int liftOffset = 0;
    public int hopperLiftZero;
    public Servo hopperFlipperLeft, hopperFlipperRight, hopperBar;
    private IntakeSystem intakeSystem;
    private HangSystem hang;
    private LEDSystem ledSystem;

    private boolean upLock = false;
    private boolean liftUp = false;
    private boolean backLock = false;
    private boolean dpadLock = false;
    private boolean hopperFlipped = false;
    public boolean sameCrater = true;
    private boolean hasMovedup = false;
    private boolean startLock = false;
    private boolean leftBumperWasPressed = false;
    private int lastHop = 0;

    private HopperBarState hopperBarState = HopperBarState.DOWN;

    private Thread hopperThread = null;


    public HopperSystem(Robot robot) {
        super(robot);

        FlipperPosition.UP.updateSettings(robot);
        FlipperPosition.DOWN.updateSettings(robot);
        FlipperPosition.IDLE.updateSettings(robot);
        FlipperPosition.MIDDLE.updateSettings(robot);
        HopperBarState.DOWN.updateSettings(robot);
        HopperBarState.UP.updateSettings(robot);
        HopperBarState.IDLE.updateSettings(robot);

    }
    @Override
    public void init() {
        this.intakeSystem = robot.getSubSystem(IntakeSystem.class);
        this.hang = robot.getSubSystem(HangSystem.class);
        this.ledSystem = robot.getSubSystem(LEDSystem.class);

        this.hopperFlipperLeft = hardwareMap().servo.get(Felix.HOPPER_FLIPPER_LEFT_KEY);
        this.hopperFlipperRight = hardwareMap().servo.get(Felix.HOPPER_FLIPPER_RIGHT_KEY);
        this.hopperBar = hardwareMap().servo.get(Felix.HOPPER_BAR_KEY);
        this.hopperLift = hardwareMap().dcMotor.get(Felix.HOPPER_LIFT_KEY);


        if(FelixPersist.lastWasAuto) {
            resetEncoder(FelixPersist.lastLiftZero);
        } else {
            FelixPersist.lastLiftZero = resetEncoder();
        }


        hopperLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //hopperLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //hopperLift.setTargetPosition(liftOffset);
        hopperLift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        setFlipperState(FlipperPosition.DOWN);
        setHopperBarState(HopperBarState.DOWN);
    }

    @Override
    public void handle() {

        telemetry().addData("hopperLift pos", getEncoder());

        //if(!hang.hangMode) {
            if (gamepad2().y && !upLock) {
                liftUp = !liftUp;
                if(!liftUp) {
                    ledSystem.triggerScore();
                }
                upLock = true;
            }
            if (!gamepad2().y && upLock) {
                upLock = false;
            }

            telemetry().addData("liftUp ", liftUp);

            if (liftUp) {
                //hopperBarState = HopperBarState.UP;
                int encv = getEncoder();
                if (encv >= 700) {
                    hopperLift.setPower(0.5);
                } else {
                    /*
                    double liftPower = 1 - 5 / (700 - encv + 50);
                    liftPower = liftPower > 1 ? 1 : liftPower;
                    hopperLift.setPower(liftPower);
                    */
                    hopperLift.setPower(1);
                }
            } else {
                //hopperBarState = HopperBarState.DOWN;
                if (getEncoder() <= 10) {
                    hopperLift.setPower(-.3);
                } else {
                    double liftPower = (10 - getEncoder()) / 250 - .2;
                    liftPower = liftPower < -0.3 ? -0.3 : liftPower;
                    hopperLift.setPower(liftPower);
                }
            }

            //deposit minerals
            if (liftUp && gamepad2().right_bumper) {
                /*if(getEncoder() > 700) {
                    setFlipperState(FlipperPosition.UP);
                } else {
                    setFlipperState(FlipperPosition.DOWN);
                }*/
                //deposit();
            }
        //}

        //middle position broke
        if(getEncoder() > 675) {
            if(gamepad2().right_bumper) {
                setFlipperState(FlipperPosition.UP);
                hopperBarState = HopperBarState.UP;
            } else {
                setFlipperState(FlipperPosition.MIDDLE);
                hopperBarState = HopperBarState.DOWN;
            }
        } else if(getEncoder() > 100) {
            setFlipperState(FlipperPosition.MIDDLE);
            hopperBarState = HopperBarState.DOWN;
        } else {
            setFlipperState(FlipperPosition.DOWN);
            hopperBarState = HopperBarState.DOWN;
        }

        setHopperBarState(hopperBarState);


    }

    @Override
    public void stop() {
        hopperLift.setPower(0);
    }

    private void deposit() {
        synchronized(this) {
            if(hopperThread != null) {
                return;
            }
            this.hopperThread = Threading.async(() -> {
                setFlipperState(FlipperPosition.UP);
                Threading.delay(1.4, robot);
                setFlipperState(FlipperPosition.DOWN);
                hopperThread = null;
            });

        }
    }

    public void setFlipperState(FlipperPosition flipperState) {
        this.hopperFlipperLeft.setPosition(flipperState.getLeft());
        this.hopperFlipperRight.setPosition(flipperState.getRight());
    }

    public void setHopperBarState(HopperBarState state) {
        hopperBar.setPosition(state.getPosition());
    }

    private int resetEncoder() {
        liftOffset = hopperLift.getCurrentPosition();
        return liftOffset;
    }


    private void resetEncoder(int offset) {
        liftOffset = offset;
    }

    public int getEncoder() {
        return hopperLift.getCurrentPosition() - liftOffset;
    }

    public enum FlipperPosition {
        UP("hopper_flipper_left_up", "hopper_flipper_right_up"),
        MIDDLE("hopper_flipper_left_middle", "hopper_flipper_right_middle"),
        DOWN("hopper_flipper_left_down", "hopper_flipper_right_down"),
        IDLE("hopper_flipper_left_idle", "hopper_flipper_right_idle");

        private String left, right;
        private double leftP, rightP;

        public double getLeft() {
            return leftP;
        }

        public double getRight() {
            return rightP;
        }

        FlipperPosition(String left, String right) {
            this.left = left;
            this.right = right;
        }

        public void updateSettings(Robot robot) {
            leftP = robot.settings.getDouble(left);
            rightP = robot.settings.getDouble(right);
        }
    }

    public enum HopperBarState {
        IDLE("hopper_bar_idle"),
        UP("hopper_bar_up"),
        DOWN("hopper_bar_down");

        private String key;
        private double pos;

        HopperBarState(String key) {
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
