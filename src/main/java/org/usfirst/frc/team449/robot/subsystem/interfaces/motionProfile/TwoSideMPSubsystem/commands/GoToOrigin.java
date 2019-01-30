package org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.TwoSideMPSubsystem.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.components.PathRequester;
import org.usfirst.frc.team449.robot.generalInterfaces.poseCommand.PoseCommand;
import org.usfirst.frc.team449.robot.generalInterfaces.poseEstimator.PoseEstimator;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandGroupWrapper;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.subsystem.interfaces.AHRS.SubsystemAHRS;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.TwoSideMPSubsystem.SubsystemMPTwoSides;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.commands.GetPathFromJetson;

import java.util.function.DoubleSupplier;

/**
 * A command that drives the given subsystem to x=0,z=0,yaw=0.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class GoToOrigin<T extends YamlSubsystem & SubsystemMPTwoSides & SubsystemAHRS> extends YamlCommandGroupWrapper implements PoseCommand {

    /**
     * The subsystem to run the path gotten from the Jetson on.
     */
    @NotNull
    private final T subsystem;
    /**
     * The displacement to the origin, with distance in feet and angle in degrees. Can be null to use lambdas.
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
     * Default constructor
     *
     * @param subsystem     The subsystem to run the path gotten from the Jetson on.
     * @param pathRequester The object for interacting with the Jetson.
     * @param x             The absolute X position, in feet, for the robot to go to. Can be null to set pose using
     *                      setters.
     * @param y             The absolute Y position, in feet, for the robot to go to. Can be null to set pose using
     *                      setters.
     * @param theta         The absolute angle, in degrees, for the robot to go to. Can be null to set pose using
     *                      setters.
     * @param deltaTime     The time between setpoints in the profile, in seconds.
     */
    @JsonCreator
    public GoToOrigin(@NotNull @JsonProperty(required = true) T subsystem,
                      @NotNull @JsonProperty(required = true) PathRequester pathRequester,
                      @Nullable Double x,
                      @Nullable Double y,
                      @Nullable Double theta,
                      @JsonProperty(required = true) double deltaTime) {
        this.x = x;
        this.y = y;
        this.theta = theta;
        this.subsystem = subsystem;
        GetPathFromJetson getPath = new GetPathFromJetson(pathRequester, null, null,
                null, deltaTime,  false);
        GoToPositionRelative goToPositionRelative = new GoToPositionRelative<>(getPath, subsystem);
        goToPositionRelative.setDestination(this::getX, this::getY, this::getTheta);
        addSequential(goToPositionRelative);
    }

    /**
     * @return The relative X distance to the setpoint, in feet.
     */
    private double getX() {
        if (x != null) {
            return x;
        } else {
            return xSupplier.getAsDouble();
        }
    }

    /**
     * @return The relative Y distance to the setpoint, in feet.
     */
    private double getY() {
        if (y != null) {
            return y;
        } else {
            return ySupplier.getAsDouble();
        }
    }

    /**
     * @return The relative angular distance to the setpoint, in degrees.
     */
    private double getTheta() {
        if (theta != null) {
            return theta;
        } else {
            return thetaSupplier.getAsDouble();
        }
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
