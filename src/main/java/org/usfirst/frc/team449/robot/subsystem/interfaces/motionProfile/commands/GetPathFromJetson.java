package org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.components.PathRequester;
import org.usfirst.frc.team449.robot.generalInterfaces.poseCommand.PoseCommand;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandWrapper;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.other.MotionProfileData;

import java.util.function.DoubleSupplier;

/**
 * Requests and receives a profile from the Jetson, accessible via a getter.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class GetPathFromJetson extends YamlCommandWrapper implements PoseCommand {

    /**
     * The object for interacting with the Jetson.
     */
    @NotNull
    private final PathRequester pathRequester;
    /**
     * The time between setpoints in the profile, in seconds.
     */
    private final double deltaTime;
    /**
     * Whether to reset the encoder position before running the profile.
     */
    private final boolean resetPosition;

    /**
     * Whether or not to invert the motion profile.
     */
    private boolean inverted;
    /**
     * Parameters for the motion profile, with x and y in feet and theta in radians. Null to use lambdas.
     */
    @Nullable
    private Double x, y, theta;
    /**
     * Getters for the motion profile parameters, with x and y in feet and theta in radians. Must not be null if the
     * Double parameters are null, otherwise are ignored.
     */
    @Nullable
    private DoubleSupplier xSupplier, ySupplier, thetaSupplier;

    /**
     * The motion profile to return.
     */
    @Nullable
    private MotionProfileData[] motionProfileData;

    /**
     * Default constructor.
     *
     * @param pathRequester The object for interacting with the Jetson.
     * @param x             The X (forwards) distance for the robot to travel, in feet. Can be null to set pose using
     *                      setters.
     * @param y             The Y (sideways) distance for the robot to travel, in feet. Can be null to set pose using
     *                      setters.
     * @param theta         The angle, in degrees, for the robot to turn to while travelling. Can be null to set pose
     *                      using setters.
     * @param deltaTime     The time between setpoints in the profile, in seconds.
     * @param resetPosition Whether or not to reset the encoder position before running the profile.
     */
    @JsonCreator
    public GetPathFromJetson(@NotNull @JsonProperty(required = true) PathRequester pathRequester,
                             @Nullable Double x,
                             @Nullable Double y,
                             @Nullable Double theta,
                             @JsonProperty(required = true) double deltaTime,
                             boolean resetPosition) {
        this.pathRequester = pathRequester;
        this.x = x;
        this.y = y;
        this.theta = theta;
        this.deltaTime = deltaTime;
        this.resetPosition = resetPosition;
    }

    /**
     * Log when this command is initialized and send the request to the Jetson.
     */
    @Override
    protected void initialize() {
        Logger.addEvent("GetPathFromJetson init", this.getClass());
        if (x != null) {
            inverted = x < 0;
            pathRequester.requestPath(Math.abs(x), y, (inverted ? -1 : 1) * theta, deltaTime);
        } else {
            //Store getAsDouble in x so it doesn't change between checking inversion and requesting the path
            x = xSupplier.getAsDouble();
            inverted = x < 0;
            pathRequester.requestPath(Math.abs(x), ySupplier.getAsDouble(), (inverted ? -1 : 1) * thetaSupplier.getAsDouble(), deltaTime);
        }
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

    /**
     * Set the destination to given values.
     *
     * @param x     The X destination, in feet.
     * @param y     The Y destination, in feet.
     * @param theta The destination angle, in degrees.
     */
    @Override
    public void setDestination(double x, double y, double theta) {
        this.x = x;
        this.y = y;
        this.theta = theta;
    }

    /**
     * Set the destination to doubles from a function.
     *
     * @param xSupplier     A getter for the X destination, in feet.
     * @param ySupplier     A getter for the Y destination, in feet.
     * @param thetaSupplier A getter for the destination angle, in degrees.
     */
    @Override
    public void setDestination(DoubleSupplier xSupplier, DoubleSupplier ySupplier, DoubleSupplier thetaSupplier) {
        this.x = null;
        this.y = null;
        this.theta = null;
        this.xSupplier = xSupplier;
        this.ySupplier = ySupplier;
        this.thetaSupplier = thetaSupplier;
    }
}
