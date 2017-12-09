package org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.TwoSideMPSubsystem.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.components.PathRequester;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandGroupWrapper;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.other.UnidirectionalPoseEstimator;
import org.usfirst.frc.team449.robot.subsystem.interfaces.AHRS.SubsystemAHRS;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.TwoSideMPSubsystem.SubsystemMPTwoSides;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.commands.GetPathFromJetson;

/**
 * A command that drives the given subsystem to an absolute position.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class GoToPosition<T extends YamlSubsystem & SubsystemMPTwoSides & SubsystemAHRS> extends YamlCommandGroupWrapper {

    /**
     * Default constructor
     *
     * @param subsystem     The subsystem to run the path gotten from the Jetson on.
     * @param pathRequester The object for interacting with the Jetson.
     * @param poseEstimator The object to get robot pose from.
     * @param x             The absolute X position, in feet, for the robot to go to.
     * @param y             The absolute Y position, in feet, for the robot to go to.
     * @param theta         The absolute angle, in radians, for the robot to go to.
     */
    @JsonCreator
    public GoToPosition(@NotNull @JsonProperty(required = true) T subsystem,
                        @NotNull @JsonProperty(required = true) PathRequester pathRequester,
                        @NotNull @JsonProperty(required = true) UnidirectionalPoseEstimator poseEstimator,
                        @JsonProperty(required = true) double x,
                        @JsonProperty(required = true) double y,
                        @JsonProperty(required = true) double theta) {
        double[] pos = poseEstimator.getPos();
        GetPathFromJetson getPath = new GetPathFromJetson(pathRequester, x - pos[0], y - pos[1],
                theta - Math.toRadians(subsystem.getHeading()), false, false);
        addSequential(new GoToPositionRelative<>(getPath, subsystem));
    }
}
