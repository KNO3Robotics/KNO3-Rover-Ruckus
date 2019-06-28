package net.kno3.util;

public interface IAdafruitIMU {
    double getHeading();
    double getRadians();
    void zeroHeading();
    double getPitch();
    void zeroPitch();
    double getRoll();
    void zeroRoll();
    void update();
}