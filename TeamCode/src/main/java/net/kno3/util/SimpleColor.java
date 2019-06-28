package net.kno3.util;

import com.qualcomm.robotcore.hardware.ColorSensor;

/**
 * Created by jaxon on 12/18/2016.
 */

public enum SimpleColor {
    RED,
    BLUE,
    SILVER,
    GOLD;

    public static SimpleColor simpleGetSimpleColor(int red, int blue) {
        if(red > blue) {
            return RED;
        } else if(blue > red) {
            return BLUE;
        }
        return null;
    }

    public SimpleColor other() {
        if(this == RED) {
            return BLUE;
        } else {
            return RED;
        }
    }

    public static SimpleColor simpleGetSimpleColor(ColorSensor sensor) {
        return simpleGetSimpleColor(sensor.red(), sensor.blue());
    }

    public static SimpleColor diffGetSimpleColor(int red, int blue, int diff) {
        if(red - blue > diff) {
            return RED;
        } else if(blue - red > diff) {
            return BLUE;
        }
        return null;
    }

    public static SimpleColor diffGetSimpleColor(ColorSensor sensor, int diff) {
        return diffGetSimpleColor(sensor.red(), sensor.blue(), diff);
    }
}
