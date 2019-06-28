package net.kno3.util;

import java.util.LinkedList;

/**
 * @author Jaxon A Brown
 */
public class PID {
    private double setpoint;
    private double min, max;
    private double kp, ki, kd;
    private int maxQueueSize;
    private LinkedList<Double> errorHistory;

    public PID(double min, double max, double kp, double ki, double kd, int maxQueueSize) {
        this.min = min;
        this.max = max;
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
        this.maxQueueSize = maxQueueSize;
        this.errorHistory = new LinkedList<>();
    }

    public PID(double min, double max, double kp, double ki, double kd) {
        this(min, max, kp, ki, kd, 10);
    }

    public double calculate(double measuremnt) {
        double error = setpoint - measuremnt;
        if(errorHistory.size() >= maxQueueSize) {
            errorHistory.remove();
        }
        errorHistory.add(error);

        double p = kp * error;
        double i = 0;
        for(double err : errorHistory) {
            i += err;
        }
        i *= ki;
        double d = 0;
        if(errorHistory.size() >= maxQueueSize / 4) {
            for (int j = 0; j < errorHistory.size() - 1; j++) {
                d += errorHistory.get(j + 1) - errorHistory.get(j);
            }
            d /= (errorHistory.size() - 1);
        }
        d *= kd;

        double out = p + i + d;
        return out < min ? min : (out > max ? max : out);
    }

    public void setSetpoint(double setpoint) {
        this.setpoint = setpoint;
        this.errorHistory.clear();
    }
}