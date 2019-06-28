package net.kno3.util;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.robotcore.internal.opmode.OpModeManagerImpl;
import com.qualcomm.robotcore.util.WeakReferenceSet;

import net.kno3.opMode.KNO3LinearOpMode;
import net.kno3.robot.Robot;

import org.firstinspires.ftc.robotcontroller.internal.FtcOpModeRegister;

import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Jaxon A Brown
 */
public class Threading {
    private static Set<Thread> threads = new WeakReferenceSet<>();

    public static Thread startThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        threads.add(thread);
        thread.start();
        return thread;
    }

    public static Thread startThread(Thread thread) {
        threads.add(thread);
        thread.start();
        return thread;
    }

    public static void clearStopAllThreads() {
        for(Thread thread : threads) {
            thread.stop();
        }
        threads.clear();
    }

    public static void delay(double seconds) {
        if(seconds == 0) {
            return;
        }
        long duration = (long) (seconds * 1000);
        try {
            Thread.sleep(duration);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public static void delay(double seconds, Robot robot) {
        if(seconds == 0) {
            return;
        }
        long duration = (long) (seconds * 1000);
        try {
            Thread.sleep(duration);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public static void idle() {
        Thread.yield();
    }

    public static void idle(Robot robot) {
        ((LinearOpMode) robot.opMode).idle();
    }

    public static void waitFor(Supplier<Boolean> supplier) {
        while(!supplier.get()) {
            if(!isOpModeActive()) {
                //System.out.println("waitforleaveprinted");
                //throw new RuntimeException("waitForLeave");
            }
            idle();
        }
    }

    public static void waitFor(Robot robot, Supplier<Boolean> supplier) {
        while(!supplier.get()) {
            if(!((LinearOpMode) robot.opMode).opModeIsActive()) {
                Thread.currentThread().interrupt();
            }
            try {
                idle(robot);
            } catch(Exception ex) {}
        }
    }

    public static void waitFor(Supplier<Boolean> supplier, double delay) {
        while(!supplier.get()) {
            if(!isOpModeActive()) {
                Thread.currentThread().interrupt();
            }
            try {
                delay(delay);
            }
            catch(Exception ex) {}
        }
    }

    public static Thread async(Runnable runnable) {
        return startThread(runnable);
    }

    public static boolean isOpModeActive() {
        //System.out.println("opmodeactive");
        OpMode opMode = FtcOpModeRegister.opModeManager.getActiveOpMode();
        // If some other code has put us in DEFAULT_OP_MODE, we need to stop immediately
        if (opMode == OpModeManagerImpl.DEFAULT_OP_MODE) {
            Thread.currentThread().interrupt();
            return false;
        }

        // We should only ever be in a linear op mode, but if for some reason we're not, stop immediately.
        else if (!(opMode instanceof KNO3LinearOpMode)) {
            Thread.currentThread().interrupt();
            return false;
        }

        // If we're in a linear op mode but a stop has been requested, stop immediately
        else if(((KNO3LinearOpMode) opMode).isStopRequested()) {
            Thread.currentThread().interrupt();
            return false;
        }

        else return true;
    }

    public static boolean clearContinue() {
        if(!isOpModeActive()) {
            Thread.currentThread().interrupt();
            return false;
        }
        return true;
    }
}
