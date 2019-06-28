package net.kno3.season.roverruckus.felix.program.calibration;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import net.kno3.util.LEDRiver;

import static net.kno3.season.roverruckus.felix.robot.LEDSystem.GREEN_WHEEL;


@TeleOp(group = "calibration", name = "Configure LEDRivers")
public class ConfigureLEDRivers extends LinearOpMode {

    private LEDRiver aesthetic;


    @Override
    public void runOpMode() throws InterruptedException {
        this.aesthetic = hardwareMap.get(LEDRiver.IMPL, "ledasthetics");
        aesthetic.setLEDMode(LEDRiver.LEDMode.RGB);
        aesthetic.setColorDepth(LEDRiver.ColorDepth.BIT_24);
        aesthetic.setLEDCount(167);


        waitForStart();



        aesthetic.setMode(LEDRiver.Mode.PATTERN);
        aesthetic.setPattern(GREEN_WHEEL);
        aesthetic.apply();

        Thread.sleep(1000);

        aesthetic.save();

        Thread.sleep(5000);

        telemetry.addData("Done", "done");
        telemetry.update();
    }
}
