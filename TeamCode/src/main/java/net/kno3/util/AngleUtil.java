package net.kno3.util;

/**
 * @author Jaxon A Brown
 */
public class AngleUtil {

    public static int directionalDifference(int initial, int end, boolean clockwise) {
        if(clockwise) {
            int angle = 360 - initial + end;
            if(angle > 360) {
                return angle - 360;
            }
            return angle;
        } else {
            int angle = 360 - initial + end;
            if(angle > 360) {
                return angle - 360;
            }
            return angle;
        }
    }

    public static float directionalDifference(float initial, float end, boolean clockwise) {
        if(clockwise) {
            float angle = 360 - initial + end;
            if(angle > 360) {
                return angle - 360;
            }
            return angle;
        } else {
            float angle = 360 - initial + end;
            if(angle > 360) {
                return angle - 360;
            }
            return angle;
        }
    }

    public static int difference(int initial, int end) {
        int c = directionalDifference(initial, end, true);
        int cc = directionalDifference(initial, end, false);
        return c > cc ? cc : c;
    }

    public static float difference(float initial, float end) {
        float c = directionalDifference(initial, end, true);
        float cc = directionalDifference(initial, end, false);
        return c > cc ? cc : c;
    }

    public static double normalize(double angle) {
        while(angle >= 360) {
            angle -= 360;
        }
        while(angle < 0) {
            angle += 360;
        }
        return angle;
    }

    public static double normalize180(double angle) {
        while(angle > 180) {
            angle -= 360;
        }
        while(angle <= -180) {
            angle += 360;
        }
        return angle;
    }
}
