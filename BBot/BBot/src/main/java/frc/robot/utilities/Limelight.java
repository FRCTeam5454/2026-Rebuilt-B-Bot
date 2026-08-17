package frc.robot.utilities;

import frc.robot.utilities.LimelightHelpers;

public class Limelight {

    public static LimelightHelpers.RawFiducial[] getRawTags() {
        return LimelightHelpers.getRawFiducials("limelight");
    }

    public static boolean hasTargets() {
        return getRawTags().length > 0;
    }

    public static int getFirstTagID() {
        LimelightHelpers.RawFiducial[] tags = getRawTags();

        if (tags.length > 0) {
            return tags[0].id;
        }

        return -1;
    }

    public static double getDistanceToFirstTag() {
        LimelightHelpers.RawFiducial[] tags = getRawTags();

        if (tags.length > 0) {
            return tags[0].distToRobot;
        }

        return -1;
    }
}