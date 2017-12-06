package org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.TwoSideMPSubsystem.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandGroupWrapper;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.TwoSideMPSubsystem.SubsystemMPTwoSides;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.commands.GetPathFromJetson;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.commands.RunProfile;

/**
 * A command that drives the given subsystem to a position relative to the current position.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class GoToRelativePosition <T extends YamlSubsystem & SubsystemMPTwoSides> extends YamlCommandGroupWrapper{

	/**
	 * Default constructor.
	 *
	 * @param getPath The command to get a path from the Jetson.
	 * @param subsystem The subsystem to run the path gotten from the Jetson on.
	 */
	@JsonCreator
	public GoToRelativePosition(@NotNull @JsonProperty(required = true) GetPathFromJetson getPath,
	                            @NotNull @JsonProperty(required = true) T subsystem){
		addSequential(getPath.getCommand());
		if(getPath.getMotionProfileData().length == 1){
			addSequential(new RunProfile<>(subsystem, getPath.getMotionProfileData()[0], 10));
		} else {
			addSequential(new RunProfileTwoSides<>(subsystem, getPath.getMotionProfileData()[0],
					getPath.getMotionProfileData()[1], 10));
		}
	}
}
