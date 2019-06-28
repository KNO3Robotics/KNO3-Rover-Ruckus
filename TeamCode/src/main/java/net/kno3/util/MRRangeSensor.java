package net.kno3.util;

import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 * Created by jaxon on 3/13/2017.
 */

public class MRRangeSensor {
    private I2cDevice i2cDevice;
    private I2cDeviceSynch deviceReader;
    private byte[] cache;

    public MRRangeSensor(I2cDevice device, I2cAddr addr) {
        this.i2cDevice = device;
        this.deviceReader = new I2cDeviceSynchImpl(i2cDevice, addr, false);
        this.deviceReader.engage();
    }

    public double getDistance() {
        cache = deviceReader.read(0x04, 2);  //Read 2 bytes starting at 0x04

        // Ultrasonic value is at index 0.
        int LUS = cache[0] & 0xFF;  // & 0xFF creates a value between 0 and 255 instead of -127 to 128

        // Optical distance value is at index 1.
        int LODS = cache[1] & 0xFF;  // & 0xFF creates a value between 0 and 255 instead of -127 to 128

        double cmOptical = cmFromOptical(LODS);
        double cm        = cmOptical > 0 ? cmOptical : LUS;
        return DistanceUnit.INCH.fromUnit(DistanceUnit.CM, cm);
    }



    private double pParam = -1.02001;
    private double qParam = 0.00311326;
    private double rParam = -8.39366;
    private int    sParam = 10;

    private double cmFromOptical(int opticalReading)
    {
        if (opticalReading < sParam)
            return 0;
        else
            return pParam * Math.log(qParam * (rParam + opticalReading));
    }
}
