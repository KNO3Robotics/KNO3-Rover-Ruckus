package net.kno3.util;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaxon on 12/10/2016.
 */

public class ValuesAdjuster {
    private Object onObject;
    private Telemetry telemetry;
    private List<AdjusterValue> values;

    private int index = 0;
    private double increment = 1;

    public ValuesAdjuster(Object onObject, Telemetry telemetry) {
        this.onObject = onObject;
        this.telemetry = telemetry;
        this.values = new ArrayList<>();
    }

    public void addValue(String fieldName, String valueName, double min, double max) {
        this.values.add(new AdjusterValue(fieldName, valueName, min, max));
    }

    public void addValue(String fieldName, String valueName) {
        addValue(fieldName, valueName, Double.NaN, Double.NaN);
    }

    public void addValue(String fieldName) {
        addValue(fieldName, null);
    }

    private boolean backState, startState, dpdLeftState, dpdRightState, leftBumperState, rightBumperState;

    public void update(Gamepad gamepad) {
        if(values.size() == 0) {
            return;
        }
        if(gamepad.dpad_down) {
            if(!backState) {
                increment /= 10;
                backState = true;
            }
        } else {
            backState = false;
        }
        if(gamepad.dpad_up) {
            if(!startState) {
                increment *= 10;
                startState = true;
            }
        } else {
            startState = false;
        }
        if(gamepad.dpad_left) {
            if(!dpdLeftState) {
                index -= 1;
                if(index >= values.size()) {
                    index = values.size() - 1;
                }
                if(index < 0) {
                    index = 0;
                }
                dpdLeftState = true;
            }
        } else {
            dpdLeftState = false;
        }
        if(gamepad.dpad_right) {
            if(!dpdRightState) {
                index += 1;
                if(index >= values.size()) {
                    index = values.size() - 1;
                }
                if(index < 0) {
                    index = 0;
                }
                dpdRightState = true;
            }
        } else {
            dpdRightState = false;
        }
        if(gamepad.left_bumper) {
            if(!leftBumperState) {
                values.get(index).increment(true);
                leftBumperState = true;
            }
        } else {
            leftBumperState = false;
        }
        if(gamepad.right_bumper) {
            if(!rightBumperState) {
                values.get(index).increment(false);
                rightBumperState = true;
            }
        } else {
            rightBumperState = false;
        }

        telemetry.addData("Currently Modifying", values.get(index).name);
        telemetry.addData("Increment", increment);
        for(AdjusterValue value : values) {
            telemetry.addData(value.name , value.getValue());
        }
    }

    private class AdjusterValue {
        private SpecificDoubleField field;
        private String name;
        private double min, max;

        AdjusterValue(String fieldName, Object onObject, String valueName, double min, double max) {
            this.field = new SpecificDoubleField(onObject.getClass(), fieldName, onObject);
            this.name = valueName;
            if(this.name == null) {
                this.name = fieldName;
            }
            this.min = min;
            this.max = max;
        }

        AdjusterValue(String fieldName, String valueName, double min, double max) {
            this(fieldName, onObject, valueName, min, max);
        }

        double increment(boolean negative) {
            double val = field.getValue();
            val += (negative ? -1 : 1) * increment;
            if(!Double.isNaN(max) && val > max) {
                val = max;
            }
            if(!Double.isNaN(min) && val < min) {
                val = min;
            }
            field.setValue(val);
            return val;
        }

        double getValue() {
            return field.getValue();
        }
    }

}
