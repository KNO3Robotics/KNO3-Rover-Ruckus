package net.kno3.season.roverruckus.felix.program.auto;


import com.disnodeteam.dogecv.detectors.roverrukus.SamplingOrderDetector;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import net.kno3.season.roverruckus.felix.program.auto.silverside.SilverAutoBase;
import net.kno3.season.roverruckus.felix.program.dogecvtests.CroppingGoldDetector;
import net.kno3.season.roverruckus.felix.program.dogecvtests.KNO3Vision;
import net.kno3.season.roverruckus.felix.robot.FelixPersist;
import net.kno3.season.roverruckus.felix.robot.IntakeSystem;
import net.kno3.util.AutoTransitioner;
import net.kno3.util.Threading;

@Autonomous(name = "FELIX SILVER AUTO TEST", group = "Auto")
public class TestAuto extends SilverAutoBase {
    @Override
    public void main() {

        landToMarkerAndSample();
        scoreTest();
        acquireMinerals();
        scoreTest();

        FelixPersist.lastWasAuto = true;
    }
}
