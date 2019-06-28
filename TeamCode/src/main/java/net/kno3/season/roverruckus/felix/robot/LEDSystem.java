package net.kno3.season.roverruckus.felix.robot;

import android.graphics.Color;

import net.kno3.opMode.DriverControlledProgram;
import net.kno3.robot.Robot;
import net.kno3.robot.SubSystem;
import net.kno3.util.LEDRiver;

import java.util.HashMap;
import java.util.Map;

public class LEDSystem extends SubSystem {
    public static final int GOLD = Color.rgb(0xFF, 0xAA, 0x00);
    public static final int SILVER = Color.rgb(0xC0, 0xC0, 0xC0);
    public static final int CURRENT = Color.rgb(0x00, 0xCC, 0x00);
    public static final int OPPOSITE = Color.rgb(0x00, 0x00, 0x00);
    public static final int YELLOW = Color.rgb(0xFF, 0xFF, 0x00);
    public static final int RED = Color.rgb(0xFF, 0x00, 0x00);

    public static final LEDRiver.Pattern GREEN_WHEEL = LEDRiver.Pattern.COLOR_WHEEL.builder().setSpeed(40).setLength(20).setMinHue(100).setMaxHue(125);
    public static final LEDRiver.Pattern RAINBOW_WHEEL = LEDRiver.Pattern.COLOR_WHEEL.builder().setSpeed(5).setLength(15).setMinHue(0).setMaxHue(360);
    public static final LEDRiver.Pattern GREEN_THEATRE = LEDRiver.Pattern.THEATRE_RUNNING.builder().setSpeed(70).setLength(3).setSpacing(5);



    private LEDRiver aesthetic;

    private DriveSystem driveSystem;
    private HopperSystem hopperSystem;
    private IntakeSystem intakeSystem;
    private HangSystem hangSystem;


    public LEDSystem(Robot robot) {
        super(robot);
    }

    @Override
    public void init() {
        this.driveSystem = robot.getSubSystem(DriveSystem.class);
        this.hopperSystem = robot.getSubSystem(HopperSystem.class);
        this.intakeSystem = robot.getSubSystem(IntakeSystem.class);
        this.hangSystem = robot.getSubSystem(HangSystem.class);

        this.aesthetic = hardwareMap().get(LEDRiver.IMPL, "ledasthetics");
        aesthetic.setLEDMode(LEDRiver.LEDMode.RGB);
        aesthetic.setColorDepth(LEDRiver.ColorDepth.BIT_24);
        aesthetic.setLEDCount(167);

        setAestheticStandard();
        updateAesthetic();
    }

    private boolean trigger_60s = true, trigger_45s = true, trigger_30s = true, trigger_10s = true;
    private boolean reset_60s = true, reset_45s = true, reset_30s = true, reset_10s = true;
    private boolean trigger_end = true;
    private boolean time_color = false;
    private boolean rainbowTrigger = false;
    private double rainbow_stop = -1;

    @Override
    public void handle() {
        /*
        Aesthetic Lights
            In init show gradient green
            In auto, gradient green
            In teleop, gradient green


            At 1 minute, flash yellow for 1 second
            At 45 seconds, flash yellow for 1 second
            At 30 seconds, flash red for 1 second
            At 10 seconds, flash red


            When hopper comes back, quick bit of rainbow


            When hang mode active, show theatre green/light green
            Just before end of game, set to gradient green




         */

        boolean updates = false;

        double remainingTime = ((DriverControlledProgram) robot.opMode).getTimeRemaining();
        if(remainingTime < 60 && trigger_60s) {
            trigger_60s = false;
            time_color = true;
            setAestheticYellow();
            updateAesthetic();
            return;
        } else if(remainingTime < 60 - 1 && reset_60s) {
            reset_60s = false;
            time_color = false;
            setAestheticStandard();
            updates = true;
        } else if(remainingTime < 45 && trigger_45s) {
            trigger_45s = false;
            time_color = true;
            setAestheticYellow();
            updateAesthetic();
            return;
        } else if(remainingTime < 45 - 1 && reset_45s) {
            reset_45s = false;
            time_color = false;
            setAestheticStandard();
            updates = true;
        } else if(remainingTime < 30 && trigger_30s) {
            trigger_30s = false;
            time_color = true;
            setAestheticRed();
            updateAesthetic();
            return;
        } else if(remainingTime < 30 - 1 && reset_30s) {
            reset_30s = false;
            time_color = false;
            setAestheticStandard();
            updates = true;
        } else if(remainingTime < 10 && trigger_10s) {
            trigger_10s = false;
            time_color = true;
            setAestheticRed();
            updateAesthetic();
            return;
        } else if(remainingTime < 10 - 1 && reset_10s) {
            reset_10s = false;
            time_color = false;
            setAestheticStandard();
            updates = true;
        } else if(remainingTime < 1 && trigger_end) {
            trigger_end = false;
            time_color = true;
            setAestheticStandard();
            updateAesthetic();
            return;
        }

        if(!time_color) {

            if (hangSystem.hangMode) {
                setAestheticTheatre();
                updates = true;
            }

            if (rainbowTrigger) {
                rainbowTrigger = false;
                rainbow_stop = remainingTime - 1;
                setAestheticRainbow();
                updates = true;
            } else if (remainingTime < rainbow_stop) {
                rainbow_stop = -1;
                setAestheticStandard();
                updates = true;
            }


            if (updates) {
                updateAesthetic();
            }
        }

    }

    @Override
    public void stop() {
        setAestheticStandard();
        updateAesthetic();
    }


    public void updateAesthetic() {
        aesthetic.apply();
    }

    public void setAestheticStandard() {
        aesthetic.setMode(LEDRiver.Mode.PATTERN);
        aesthetic.setPattern(GREEN_WHEEL);
    }

    public void setAestheticYellow() {
        aesthetic.setMode(LEDRiver.Mode.SOLID);
        aesthetic.setColor(YELLOW);
    }

    public void setAestheticRed() {
        aesthetic.setMode(LEDRiver.Mode.SOLID);
        aesthetic.setColor(RED);
    }

    public void setAestheticRainbow() {
        aesthetic.setMode(LEDRiver.Mode.PATTERN);
        aesthetic.setPattern(RAINBOW_WHEEL);
    }

    public void setAestheticTheatre() {
        aesthetic.setMode(LEDRiver.Mode.PATTERN);
        aesthetic.setPattern(GREEN_THEATRE);
        aesthetic.setColor(0, Color.GREEN);
        aesthetic.setColor(1, Color.GREEN);
        aesthetic.setColor(2, Color.BLACK);
    }


    public void triggerScore() {
        rainbowTrigger = true;
    }

    private Map<String, Object> changeMap = new HashMap<>();
    private boolean hasChanged(String key, boolean value) {
        boolean hasChanged;
        if(changeMap.containsKey(key)) {
            hasChanged = (Boolean) changeMap.get(key) != value;
        } else {
            hasChanged = true;
        }

        if(hasChanged) {
            changeMap.put(key, value);
        }

        return hasChanged;
    }
}
