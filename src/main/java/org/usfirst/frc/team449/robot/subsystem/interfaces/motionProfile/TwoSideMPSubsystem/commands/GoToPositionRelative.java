package org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.TwoSideMPSubsystem.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.poseCommand.PoseCommand;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandGroupWrapper;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
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
        if (getPath.getMotionProfileData().length == 1) {
            addSequential(new RunProfile<>(subsystem, getPath.getMotionProfileData()[0], 10));
        } else {
            addSequential(new RunProfileTwoSides<>(subsystem, getPath.getMotionProfileData()[0],
                    getPath.getMotionProfileData()[1], 10));
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
}
