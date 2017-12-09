package org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.components.PathRequester;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandWrapper;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.other.MotionProfileData;

/**
 * Requests and receives a profile from the Jetson, accessible via a getter.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class GetPathFromJetson extends YamlCommandWrapper {

    /**
     * The object for interacting with the Jetson.
     */
    @NotNull
    private final PathRequester pathRequester;

    /**
     * Parameters for the motion profile, with x and y in feet and theta in radians.
     */
    private final double x, y, theta;

    /**
     * Whether to invert the profile and whether to reset the encoder position before running the profile.
     */
    private final boolean inverted, resetPosition;

    /**
     * The motion profile to return.
     */
    @Nullable
    private MotionProfileData[] motionProfileData;

    /**
     * Default constructor.
     *
     * @param pathRequester The object for interacting with the Jetson.
     * @param x             The X (forwards) distance for the robot to travel, in feet.
     * @param y             The Y (sideways) distance for the robot to travel, in feet.
     * @param theta         The angle, in radians, for the robot to turn to while travelling.
     * @param inverted      Whether or not to invert the profile.
     * @param resetPosition Whether or not to reset the encoder position before running the profile.
     */
    @JsonCreator
    public GetPathFromJetson(@NotNull @JsonProperty(required = true) PathRequester pathRequester,
                             @JsonProperty(required = true) double x,
                             @JsonProperty(required = true) double y,
                             @JsonProperty(required = true) double theta,
                             boolean inverted,
                             boolean resetPosition) {
        this.pathRequester = pathRequester;
        this.x = x;
        this.y = y;
        this.theta = theta;
        this.inverted = inverted;
        this.resetPosition = resetPosition;
    }

    /**
     * Log when this command is initialized and send the request to the Jetson.
     */
    @Override
    protected void initialize() {
        Logger.addEvent("GetPathFromJetson init", this.getClass());
        pathRequester.requestPath(x, y, theta);
        motionProfileData = null;
    }

    /**
     * Receive the path, or null if the Jetson hasn't replied yet.
     */
    @Override
    protected void execute() {
        motionProfileData = pathRequester.getPath(inverted, resetPosition);
    }

    /**
     * Stop when profile received.
     *
     * @return true when the profile is received from the Jetson, false otherwise.
     */
    @Override
    protected boolean isFinished() {
        return motionProfileData != null;
    }

    /**
     * Log that the command has ended.
     */
    @Override
    protected void end() {
        Logger.addEvent("GetPathFromJetson end", this.getClass());
    }

    /**
     * Log that the command has been interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("GetPathFromJetson interrupted!", this.getClass());
    }

    /**
     * @return The motion profile gotten from the Jetson or null if not finished yet.
     */
    @Nullable
    public MotionProfileData[] getMotionProfileData() {
        return motionProfileData;
    }
}
