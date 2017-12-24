package org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.TwoSideMPSubsystem.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.poseCommand.PoseCommand;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandGroupWrapper;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.other.MotionProfileData;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.TwoSideMPSubsystem.SubsystemMPTwoSides;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.commands.GetPathFromJetson;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.commands.RunProfile;

import java.util.function.DoubleSupplier;

/**
 * A command that drives the given subsystem to a position relative to the current position.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class GoToPositionRelative<T extends YamlSubsystem & SubsystemMPTwoSides> extends YamlCommandGroupWrapper implements PoseCommand{

    /**
     * The command for getting the path from the Jetson.
     */
    private final GetPathFromJetson getPath;

    /**
     * Default constructor.
     *
     * @param getPath   The command to get a path from the Jetson.
     * @param subsystem The subsystem to run the path gotten from the Jetson on.
     */
    @JsonCreator
    public GoToPositionRelative(@NotNull @JsonProperty(required = true) GetPathFromJetson getPath,
                                @NotNull @JsonProperty(required = true) T subsystem) {
        this.getPath = getPath;
        addSequential(getPath.getCommand());
        addSequential(new RunProfileTwoSides<>(subsystem, this::getLeft,
                this::getRight, 10));
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
        getPath.setDestination(x, y, theta);
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
        getPath.setDestination(xSupplier, ySupplier, thetaSupplier);
    }

    /**
     * @return The motion profile for the left side to run, or null if not received from the Jetson yet.
     */
    @Nullable
    private MotionProfileData getLeft(){
        if (getPath.getMotionProfileData() == null){
            return null;
        } else {
            return getPath.getMotionProfileData()[0];
        }
    }

    /**
     * @return The motion profile for the right side to run, or null if not received from the Jetson yet.
     */
    @Nullable
    private MotionProfileData getRight(){
        if (getPath.getMotionProfileData() == null){
            return null;
        } else if (getPath.getMotionProfileData().length < 2){
            //If it's only 1 profile, then it's the same one for both sides.
            return getPath.getMotionProfileData()[0];
        } else {
            return getPath.getMotionProfileData()[1];
        }
    }
}
