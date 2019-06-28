package net.kno3.opMode;

import android.content.SharedPreferences;
import android.util.Log;

import com.qualcomm.ftccommon.FtcRobotControllerService;
import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.robot.RobotState;
import net.kno3.robot.Robot;
import net.kno3.season.roverruckus.felix.robot.FelixPersist;

import org.firstinspires.ftc.robotcontroller.internal.FtcOpModeRegister;
import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.firstinspires.ftc.robotcore.internal.opmode.OpModeManagerImpl;

import java.lang.reflect.Field;

/**
 * A driver controlled program.
 * NOTE: This class will automatically stop itself two minutes after starting!
 * Usage:
 * This class should be extended by a single program in which you MUST:
 *  - Override #buildRobot()
 * and MAY:
 *  - Override #onStart()
 *  - Override #onUpdate()
 *  - Override #onStop()
 * @Author Jaxon Brown
 */
public abstract class DriverControlledProgram extends OpMode {
    private static Field eventLoop;

    static {
        try {
            eventLoop = FtcRobotControllerService.class.getDeclaredField("eventLoopManager");
            eventLoop.setAccessible(true);
        } catch(NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private Robot robot;
    private OpModeManagerImpl opModeManager;
    private long stoppingTime;
    private long displayStoppingTime;
    private boolean timerDisabled = false;
    private SharedPreferences settings;

    /**
     * Build a robot. This should be overridden by your Program.
     * Construct your robot and make any necessary changes to the subsystems.
     * @return Robot this program controls.
     */
    protected abstract Robot buildRobot();

    /**
     * Called when the the program is started.
     */
    protected void onStart() {}

    /**
     * Called when the loop finishes.
     */
    protected void onUpdate() {}

    /**
     * Called when the robot is stopped.
     */
    protected void onStop() {}

    @Override
    public final void init() {
        robot = buildRobot();

        opModeManager = (OpModeManagerImpl) getRobot().opMode.internalOpModeServices;
        FtcOpModeRegister.opModeManager = (OpModeManagerImpl) getRobot().opMode.internalOpModeServices;
        this.settings = hardwareMap.appContext.getSharedPreferences("RobotState", 0);


        try {
            robot.init();
        } catch(Exception ex) {
            telemetry.addData("ERROR!!!", ex.getMessage());
        }
    }

    @Override
    public final void start() {
        FelixPersist.lastWasAuto = false;
        Log.d("KNO3Encoder", "last was auto set to false (DCP Start)");
        stoppingTime = settings.getLong("compstarttime", System.currentTimeMillis()) + 1000 * 125;
        displayStoppingTime = settings.getLong("compstarttime", System.currentTimeMillis()) + 1000 * 121;

        onStart();
    }

    @Override
    public void init_loop() {
        telemetry.addData("Status", "loop test... waiting for start");
    }

    @Override
    public final void loop() {
        if(!timerDisabled && stoppingTime <= System.currentTimeMillis()) {
            opModeManager.requestOpModeStop(this);
            return;
        }

        telemetry.addData("Time", (displayStoppingTime - System.currentTimeMillis())/1000D);

        robot.driverControlledUpdate();
        onUpdate();
    }

    @Override
    public final void stop() {
        onStop();
        try {
            if(((EventLoopManager) eventLoop.get(FtcRobotControllerActivity.controllerService)).state != RobotState.EMERGENCY_STOP) {
                settings.edit().remove("compstarttime").remove("compteleop").apply();
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            settings.edit().remove("compstarttime").remove("compteleop").apply();
        }
    }

    /**
     * Gets the robot. If you properly cached your subcomponents in buildRobot(), you probably don't need this.
     * @return
     */
    protected final Robot getRobot() {
        return robot;
    }

    protected void disableTimer() {
        this.timerDisabled = true;
    }

    public double getTimeRemaining() {
        return (displayStoppingTime - System.currentTimeMillis())/1000D;
    }
}