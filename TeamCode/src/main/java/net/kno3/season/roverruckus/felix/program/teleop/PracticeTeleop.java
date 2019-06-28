package net.kno3.season.roverruckus.felix.program.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import net.kno3.opMode.DriverControlledProgram;
import net.kno3.robot.Robot;
import net.kno3.season.roverruckus.felix.robot.Felix;

@TeleOp(name = "Prac Teleop Neutral")
public class PracticeTeleop extends DriverControlledProgram {

    public PracticeTeleop() {
        disableTimer();
    }

    @Override
    protected Robot buildRobot() {
        return new Felix(this);
    }
}