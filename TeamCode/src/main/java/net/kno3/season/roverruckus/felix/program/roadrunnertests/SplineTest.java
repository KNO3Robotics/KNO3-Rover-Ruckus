package net.kno3.season.roverruckus.felix.program.roadrunnertests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.canvas.Canvas;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import net.kno3.season.roverruckus.felix.program.auto.FelixAuto;
import net.kno3.season.roverruckus.felix.robot.acmedrive.DashboardUtil;

@Autonomous
public class SplineTest extends FelixAuto {
    private FtcDashboard dashboard;
    private Trajectory trajectory;

    @Override
    public void postInit() {
        super.postInit();
        dashboard = FtcDashboard.getInstance();

        trajectory = drive.roadrunner.trajectoryBuilder()
                .splineTo(new Pose2d(30, 30, 0))
                .waitFor(1)
                .splineTo(new Pose2d(0, 0, 0))
                .build();
    }


    @Override
    public void main() {
        if (isStopRequested()) return;

        drive.roadrunner.followTrajectory(trajectory);
        while (!isStopRequested() && drive.roadrunner.isFollowingTrajectory()) {
            Pose2d currentPose = drive.roadrunner.getPoseEstimate();

            TelemetryPacket packet = new TelemetryPacket();
            Canvas fieldOverlay = packet.fieldOverlay();

            packet.put("x", currentPose.getX());
            packet.put("y", currentPose.getY());
            packet.put("heading", currentPose.getHeading());

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