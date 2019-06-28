package net.kno3.util;

/**
 * @author Jaxon A Brown
 */
public class FloatRateTracker {
    private long lastTime;
    private float rate;
    private float lastSetpoint;

    public FloatRateTracker(float defaultRate) {
        this.rate = defaultRate;
        lastTime = System.currentTimeMillis();
    }

    public float getRate() {
        return rate;
    }

    public void update(float setpoint) {
        rate = ((setpoint - lastSetpoint) / (System.currentTimeMillis() - lastSetpoint));
    }
}
