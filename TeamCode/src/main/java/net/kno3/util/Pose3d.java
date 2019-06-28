package net.kno3.util;

import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;

public class Pose3d {
    public final Position position;
    public final Orientation orientation;

    public Pose3d(Position position, Orientation orientation) {
        this.position = position;
        this.orientation = orientation;
    }


}
