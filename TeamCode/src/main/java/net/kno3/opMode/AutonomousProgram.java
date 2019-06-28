package net.kno3.opMode;

import net.kno3.robot.Robot;
import net.kno3.util.Condition;
import net.kno3.util.Threading;

import org.firstinspires.ftc.robotcontroller.internal.FtcOpModeRegister;
import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.firstinspires.ftc.robotcore.internal.opmode.OpModeManagerImpl;

/**
 * An Autonomous program.
 * NOTE: Unlike Driver-controlled programs, it does NOT track time remaining. The driver station does that.
 * Usage:
 * This class should be extended by an abstract class representing an autonomous program for the robot.
 * @Author Jaxon Brown
 */
public abstract class AutonomousProgram extends KNO3LinearOpMode {
    private Robot robot;

    /**
     * Build a robot. This should be overridden by your abstract class and there marked final.
     * Construct your robot and cache its subcomponents as protected for use in the actual programs themselves.
     * @return Robot this program controls.
     */
    protected abstract Robot buildRobot();

    /**
     * Allow this method to be overridden in the Autonomous program.
     * From there, you can code just like a linear opmode.
     */
    public abstract void main();

    public void postInit() {}

    @Override
    public final void runOpMode() throws InterruptedException {
        robot = buildRobot();

        FtcOpModeRegister.opModeManager = (OpModeManagerImpl) getRobot().opMode.internalOpModeServices;

        try {
            robot.init();
            postInit();
        } catch(Exception ex) {
            telemetry.addData("ERROR!!!", ex.getMessage());
        }

        while(!opModeIsActive() && !isStopRequested()) {
            telemetry.addData("Status", "Waiting for start command...");
            telemetry.update();
        }

        try {
            int delay = Integer.parseInt(FtcRobotControllerActivity.autonomousProgramTextAutoDelay.getText().toString());
            waitFor(delay);
        } catch(Exception ex) {
            telemetry.addData("ERROR!!!", ex.getMessage());
            ex.printStackTrace();
        }

        try {
            main();
        } catch(Exception ex) {
            telemetry.addData("ERROR!!!", ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Gets the robot. If you properly cached your subcomponents in buildRobot(), you probably don't need this.
     * @return the robot.
     */
    protected final Robot getRobot() {
        return robot;
    }

    /**
     * Wait a period of time. This will be non-blocking, so Thread away!
     */
    protected final void waitFor(double seconds) {
        long stopTime = System.currentTimeMillis() + (int) (seconds * 1000);
        while(opModeIsActive() && System.currentTimeMillis() < stopTime) {}
    }

    protected final void waitFor(double seconds, Runnable update) {
        long stopTime = System.currentTimeMillis() + (int) (seconds * 1000);
        while(opModeIsActive() && System.currentTimeMillis() < stopTime) {
            update.run();
        }
    }

    protected final void waitFor(Condition condition) {
        while(opModeIsActive() && !condition.get()) {}
    }
}