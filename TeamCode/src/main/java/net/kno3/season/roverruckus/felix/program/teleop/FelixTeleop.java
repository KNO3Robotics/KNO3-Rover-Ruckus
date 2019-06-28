package net.kno3.season.roverruckus.felix.program.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import net.kno3.opMode.DriverControlledProgram;
import net.kno3.robot.Robot;
import net.kno3.season.roverruckus.felix.robot.Felix;

@TeleOp(name = "Felix Teleop")
public class FelixTeleop extends DriverControlledProgram {


    @Override
    protected Robot buildRobot() {
        return new Felix(this);
    }
}