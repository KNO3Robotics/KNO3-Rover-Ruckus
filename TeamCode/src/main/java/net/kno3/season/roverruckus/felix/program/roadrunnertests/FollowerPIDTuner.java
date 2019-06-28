package net.kno3.season.roverruckus.felix.program.roadrunnertests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.canvas.Canvas;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import net.kno3.season.roverruckus.felix.program.auto.FelixAuto;
import net.kno3.season.roverruckus.felix.robot.acmedrive.DashboardUtil;

/*
 * Op mode for tuning follower PID coefficients. This is the final step in the tuning process.
 */
@Autonomous
public class FollowerPIDTuner extends FelixAuto {

    private FtcDashboard dashboard;
    private Trajectory trajectory;

    @Override
    public void postInit() {
        super.postInit();

        dashboard = FtcDashboard.getInstance();

        drive.roadrunner.setPoseEstimate(new Pose2d(-24, -24, 0));
        trajectory = drive.roadrunner.trajectoryBuilder()
                .forward(48)
                .turn(Math.toRadians(90))
                .forward(48)
                .turn(Math.toRadians(90))
                .forward(48)
                .turn(Math.toRadians(90))
                .forward(48)
                .turn(Math.toRadians(90))
                .build();
    }


    @Override
    public void main() {
        if (isStopRequested()) return;

        while (!isStopRequested()) {
            drive.roadrunner.followTrajectory(trajectory);

            while (!isStopRequested() && drive.roadrunner.isFollowingTrajectory()) {
                Pose2d currentPose = drive.roadrunner.getPoseEstimate();
                Pose2d error = drive.roadrunner.getFollowingError();

                TelemetryPacket packet = new TelemetryPacket();
                Canvas fieldOverlay = packet.fieldOverlay();

                packet.put("xError", error.getX());
                packet.put("yError", error.getY());
                packet.put("headingError", error.getHeading());

                fieldOverlay.setStrokeWidth(4);
                fieldOverlay.setStroke("green");
                DashboardUtil.drawSampledTrajectory(fieldOverlay, trajectory);

                fieldOverlay.setFill("blue");
                fieldOverlay.fillCircle(currentPose.getX(), currentPose.getY(), 3);

                dashboard.sendTelemetryPacket(packet);

                drive.roadrunner.update();
            }
        }
    }
}
