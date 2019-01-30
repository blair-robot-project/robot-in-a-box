package org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.TwoSideMPSubsystem.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.components.PathRequester;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandGroupWrapper;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.subsystem.interfaces.AHRS.SubsystemAHRS;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.TwoSideMPSubsystem.SubsystemMPTwoSides;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.commands.GetPathFromJetson;

/**
 * A command that drives the given subsystem to x=0,z=0,yaw=0.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class GoToVisionTarget<T extends YamlSubsystem & SubsystemMPTwoSides & SubsystemAHRS> extends YamlCommandGroupWrapper {

    /**
     * The subsystem to run the path gotten from the Jetson on.
     */
    @NotNull
    private final T subsystem;
    /**
     * Network table for pulling info from the Jetson
     */
    private final ITable table;

    /**
     * Default constructor
     *
     * @param subsystem     The subsystem to run the path gotten from the Jetson on.
     * @param pathRequester The object for interacting with the Jetson.
     * @param deltaTime     The time between setpoints in the profile, in seconds.
     */
    @JsonCreator
    public GoToVisionTarget(@NotNull @JsonProperty(required = true) T subsystem,
                            @NotNull @JsonProperty(required = true) PathRequester pathRequester,
                            @JsonProperty(required = true) double deltaTime) {
        this.subsystem = subsystem;
        this.table = NetworkTable.getTable("SmartDashboard").getSubTable("jetson-vision");
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
        return -table.getNumber("x", 0);
    }

    /**
     * @return The relative Y distance to the setpoint, in feet.
     */
    private double getY() {
        return -table.getNumber("z", 0);
    }

    /**
     * @return The relative angular distance to the setpoint, in degrees.
     */
    private double getTheta() {
        return -table.getNumber("yaw", 0);
    }
}
