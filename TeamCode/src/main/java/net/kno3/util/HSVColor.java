package net.kno3.util;

/**
 * @author Jaxon A Brown
 */
public class HSVColor {
    private float hue, sat, val;

    public HSVColor(float hue, float sat, float val) {
        this.hue = hue;
        this.sat = sat;
        this.val = val;
    }

    public HSVColor(int red, int green, int blue) {
        float[] hsv = new float[3];
        android.graphics.Color.RGBToHSV(red, green, blue, hsv);
        this.hue = hsv[0];
        this.sat = hsv[1];
        this.val = hsv[2];
    }

    public float hue() {
        return this.hue;
    }

    public float sat() {
        return this.sat;
    }

    public float val() {
        return this.val;
    }
}
