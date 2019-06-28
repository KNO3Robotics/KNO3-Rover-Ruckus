package net.kno3.util;

/**
 * @author Jaxon A Brown
 */
public class DoubleRateTracker {
    protected double lastTime;
    protected double rate;
    protected double lastSetpoint;

    public DoubleRateTracker(double defaultRate, double startingPosition) {
        this.rate = defaultRate;
        lastTime = System.nanoTime();
        this.lastSetpoint = startingPosition;
    }

    public synchronized double getRate() {
        return rate;
    }

    public synchronized void update(double setpoint) {
        //System.out.println("Time Diff: " + (System.nanoTime() - lastTime));
        long now = System.nanoTime();
        double minutes = (now - lastTime) / 1000000000.0 / 60.0;

        double rotationsMotor = (float)(setpoint - lastSetpoint) / 3.0;
        double rotationsWheel = rotationsMotor * 64.0 / 20.0;

        rate = rotationsWheel / minutes;
        lastSetpoint = setpoint;
        lastTime = now;
    }
}
