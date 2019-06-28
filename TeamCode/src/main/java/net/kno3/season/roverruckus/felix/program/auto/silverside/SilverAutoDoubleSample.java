package net.kno3.season.roverruckus.felix.program.auto.silverside;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import net.kno3.season.roverruckus.felix.robot.FelixPersist;

@Autonomous(name = "FELIX SILVER AUTO DS", group = "Auto")
public class SilverAutoDoubleSample extends SilverAutoBase {
    @Override
    public void main() {

        landDriveForward();
        sample();
        retractIntake();
        alignDoubleSample();
        doubleSample();
        returnForParkDouble();
        scoreDouble();

        FelixPersist.lastWasAuto = true;
    }
}




