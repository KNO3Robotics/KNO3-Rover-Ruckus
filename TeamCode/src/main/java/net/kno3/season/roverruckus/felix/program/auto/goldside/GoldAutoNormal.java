package net.kno3.season.roverruckus.felix.program.auto.goldside;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import net.kno3.season.roverruckus.felix.robot.FelixPersist;

@Autonomous(name = "FELIX GOLD AUTO NORMAL", group = "Auto")
public class GoldAutoNormal extends GoldAutoBase {
    @Override
    public void main() {
        landDriveForward();
        sample();
        retractIntake();
        dumpMarker();
        score();
        park();

        FelixPersist.lastWasAuto = true;
    }
}
