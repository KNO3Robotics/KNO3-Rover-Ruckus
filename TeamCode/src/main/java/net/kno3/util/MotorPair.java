package net.kno3.util;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * @author Jaxon A Brown
 */
public class MotorPair {
    public DcMotor motor1, motor2;
    public double power;
    public double position;
    public int encOffset = 0;

    public MotorPair(DcMotor motor1, DcMotor motor2) {
        this.motor1 = motor1;
        this.motor2 = motor2;
    }

    public void setPower(double power) {
        this.power = power;
        motor1.setPower(power);
        motor2.setPower(power);
    }

    public void setTargetPosition(int position) {
        this.position = position;
        motor1.setTargetPosition(position);
        motor2.setTargetPosition(position);
    }

    public void stop() {
        setPower(0);
    }

    public double getPower() {
        return this.power;
    }

    public int getEncoder() {
        return (motor1.getCurrentPosition() + motor2.getCurrentPosition()) / 2 - encOffset;
    }

    public int resetEncoder() {
        this.encOffset = (motor1.getCurrentPosition() + motor2.getCurrentPosition()) / 2;
        return this.encOffset;
    }

    public void resetEncoder(int offset) {
        this.encOffset = offset;
    }

    public void floatMode() {
        this.motor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.motor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    public void brakeMode() {
        this.motor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.motor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void runMode(DcMotor.RunMode mode) {
        this.motor1.setMode(mode);
        this.motor2.setMode(mode);
    }
}
