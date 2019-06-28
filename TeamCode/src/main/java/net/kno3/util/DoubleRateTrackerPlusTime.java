package net.kno3.util;

import java.util.LinkedList;

/**
 * @author Jaxon A Brown
 */
public class DoubleRateTrackerPlusTime extends DoubleRateTracker {
    protected LinkedList<Double> recents = new LinkedList<>();
    protected int size;

    public DoubleRateTrackerPlusTime(double defaultRate, double startingPosition, int size) {
        super(defaultRate, startingPosition);
        this.size = size;
    }

    public synchronized double getRate() {
        return rate;
    }

    public synchronized void update(double setpoint) {
        super.update(setpoint);
        double recentAvg = rate;
        for(double rct : recents) {
            recentAvg += rct;
        }
        recentAvg /= recents.size() + 1;
        this.rate = recentAvg;
        recents.addFirst(rate);
        if(recents.size() > size) {
            recents.removeLast();
        }
    }
}
