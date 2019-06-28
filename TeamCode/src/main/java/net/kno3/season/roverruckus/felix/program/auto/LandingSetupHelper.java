package net.kno3.season.roverruckus.felix.program.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import net.kno3.season.roverruckus.felix.robot.HangSystem;
import net.kno3.season.roverruckus.felix.robot.HopperSystem;

@Autonomous(name = "RUN FIRST !!!!! Landing setup helper")
@Disabled
public class LandingSetupHelper extends FelixAuto {
    @Override
    public void postInit() {
        super.postInit();

        hang.setLandState(HangSystem.LandPosition.LOCKED);
    }

    @Override
    public void main() {

    }
}
