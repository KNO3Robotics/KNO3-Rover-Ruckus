package net.kno3.season.roverruckus.felix.program.auto.silverside;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import net.kno3.season.roverruckus.felix.robot.FelixPersist;

@Autonomous(name = "FELIX SILVER AUTO NORMAL", group = "Auto")
public class SilverAutoNormal extends SilverAutoBase {
    @Override
    public void main() {

        landDriveForward();
        sample();
        score();
        alignMarker(true);
        scoreMarker();
        returnForPark();
        park();

        FelixPersist.lastWasAuto = true;
    }
}
