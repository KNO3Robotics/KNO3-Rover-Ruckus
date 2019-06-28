package net.kno3.season.roverruckus.felix.program.auto;

import android.util.Log;

import net.kno3.opMode.AutonomousProgram;
import net.kno3.robot.Robot;
import net.kno3.season.roverruckus.felix.robot.HangSystem;
import net.kno3.season.roverruckus.felix.robot.HopperSystem;
import net.kno3.season.roverruckus.felix.robot.IntakeSystem;
import net.kno3.season.roverruckus.felix.robot.Felix;
import net.kno3.season.roverruckus.felix.robot.DriveSystem;
import net.kno3.season.roverruckus.felix.robot.FelixPersist;

public abstract class FelixAuto extends AutonomousProgram {
    public DriveSystem drive;
    public HopperSystem hopper;
    public IntakeSystem intake;
    public HangSystem hang;

    public FelixAuto() {
        FelixPersist.lastWasAuto = false;
        Log.d("KNO3Encoder", "last was auto set to false (FelixAuto ctor)");
    }

    @Override
    protected Robot buildRobot() {
        Felix felix = new Felix(this);
        drive = felix.getSubSystem(DriveSystem.class);
        hopper = felix.getSubSystem(HopperSystem.class);
        intake = felix.getSubSystem(IntakeSystem.class);
        hang = felix.getSubSystem(HangSystem.class);
        return felix;
    }

    @Override
    public void postInit() {
        drive.modeSpeed();
        FelixPersist.lastWasAuto = true;
        Log.d("KNO3Encoder", "last was auto set to true (FelixAuto postinit)");
    }
}
