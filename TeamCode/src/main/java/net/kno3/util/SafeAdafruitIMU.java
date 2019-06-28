
package net.kno3.util;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

/**
 * @author Jaxon A Brown
 */
public class SafeAdafruitIMU implements IAdafruitIMU {
    private BNO055IMU imu;

    private Orientation orientation;
    private float headingOffset, pitchOffset, rollOffset;
    private boolean isAccelIntegRunning;

    public SafeAdafruitIMU(HardwareMap hardwareMap, String name, boolean init) {
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "AdafruitIMUCalibration.json";
        parameters.loggingEnabled      = false;
        parameters.loggingTag          = "IMU";

        imu = hardwareMap.get(BNO055IMU.class, name);
        if(init) {
            //try {
            imu.initialize(parameters);
            //} catch (Exception ex) {
            //Log.e(FtcRobotControllerActivity.TAG, "imu failed", ex);
            //}
        }
        this.isAccelIntegRunning = false;
    }

    public void update() {
        this.orientation = imu.getAngularOrientation();
    }

    public double getHeading() {
        return AngleUtil.normalize(orientation.firstAngle * -1 - headingOffset); // *-1 heading is technically pitch, change others later?
    }

    public double getRadians() {
        return orientation.firstAngle * (Math.PI / 180);
    }

    public void zeroHeading() {
        this.headingOffset = orientation.firstAngle * -1;
    } // *-1

    public double getPitch() {
        return AngleUtil.normalize(orientation.thirdAngle * -1 - pitchOffset);
    }

    public void zeroPitch() {
        this.pitchOffset = orientation.thirdAngle * -1;
    }

    public double getRoll() {
        return AngleUtil.normalize(orientation.secondAngle * -1 - rollOffset);
    }

    public void zeroRoll() {
        this.rollOffset = orientation.secondAngle * -1;
    }

    public Position getPosition() {
        return imu.getPosition();
    }

    public void startIntegration(Position position, Velocity velocity, int pollInterval) {
        if(this.isAccelIntegRunning) {
            imu.stopAccelerationIntegration();
        }

        imu.startAccelerationIntegration(position, velocity, pollInterval);
    }

    public double getFrontBackAccel() {
        return -imu.getAcceleration().xAccel;
    }
}