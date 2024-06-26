/*
 * Copyright (c) 2024 RoboGym Robotics & Patrick Martin. All rights reserved. This library is subject to the MIT license, which can be found in its root directory.
 */

package lib.odometry;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.constants.Constants;

import java.util.function.DoubleSupplier;

public final class ScoringKinematics {

    public static final Pose2d redSpeaker = new Pose2d(16.55, 5.55, Rotation2d.fromDegrees(0));
    public static final Pose2d blueSpeaker = new Pose2d(0, 5.55, Rotation2d.fromDegrees(0));

    public static DriverStation.Alliance alliance = DriverStation.getAlliance().get();
    public static final Pose2d activeSpeaker;

    static {
        activeSpeaker = alliance.equals(DriverStation.Alliance.Red) ? redSpeaker : blueSpeaker;
    }

    public static double getHeadingAdjustment(Pose2d botPose, Pose2d speakerPose) {
        var speakerDistance = botPose.getTranslation().getDistance(speakerPose.getTranslation());

        return Math.acos(botPose.getX() / speakerDistance);
    }

    public static double getLobPivot(DoubleSupplier distance) {
        return (Math.PI/2 - Math.asin(Constants.Physics.yVelocity/getVelocityVector(distance))) * (0.25/(Math.PI/2));
    }

    public static double getAllianceSpeakerDistance(Swerve drive){
        return drive.getState().Pose.getTranslation().getDistance(ScoringKinematics.activeSpeaker.getTranslation());
    }

    public static double getVelocityVector(DoubleSupplier distance) {
        var d = distance.getAsDouble();

        return Math.hypot(d/Constants.Physics.airTime, Constants.Physics.yVelocity);
    }

    public static double getLobVelocityRps(DoubleSupplier distance) {
        var rpsMsConversion = (187.978/60);

        return rpsMsConversion * getVelocityVector(distance);
    }

    public static double getHeadingDifference(Pose2d activeSpeaker, Pose2d botPose) {
        var xDistance = botPose.getTranslation().getX() - activeSpeaker.getX();
        var yDistance = botPose.getTranslation().getY() - activeSpeaker.getY();

        var targetAngle = Math.atan2(yDistance, xDistance);
        var currentAngle = botPose.getRotation().getRadians();

        return currentAngle - targetAngle;
    }

    public static double getTargetPivot(DoubleSupplier distance) {
        var gravityComp = distance.getAsDouble() < 3.99 ?
            0.01 * Math.pow(distance.getAsDouble(), 2):
            0.019 * Math.pow(distance.getAsDouble(), 2);

        return 0.25 - ((Math.atan2(Constants.Physics.targetSpeakerHeight + gravityComp, distance.getAsDouble()) * (180 / Math.PI)) / 360);
    }
}
