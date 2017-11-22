package org.usfirst.frc.team449.robot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedRunnable;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommand;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.oi.buttons.CommandButton;
import org.usfirst.frc.team449.robot.other.Logger;

import java.util.List;

/**
 * The robot map for the Robot In A Box PID module.
 */
public class RobotInABoxMap {

	/**
	 * The buttons for controlling this robot.
	 */
	@NotNull
	private final List<CommandButton> buttons;

	/**
	 * The logger for recording events and telemetry data.
	 */
	@NotNull
	private final Logger logger;

	/**
	 * A runnable that updates cached variables.
	 */
	@NotNull
	private final Runnable updater;

	/**
	 * The command to run when the robot is first enabled. Can be null to not run a command.
	 */
	@Nullable
	private final YamlCommand startupCommand;

	/**
	 * Subsystems that don't go anywhere else.
	 */
	@Nullable
	private final List<YamlSubsystem> variousSubsystems;

	/**
	 * Default constructor.
	 *
	 * @param buttons The buttons for controlling this robot.
	 * @param logger  The logger for recording events and telemetry data.
	 * @param updater A runnable that updates cached variables.
	 * @param startupCommand The command to run when the robot is first enabled. Can be null to not run a command.
	 * @param variousSubsystems Subsystems that don't go anywhere else.
	 */
	@JsonCreator
	public RobotInABoxMap(@NotNull @JsonProperty(required = true) List<CommandButton> buttons,
	                      @NotNull @JsonProperty(required = true) Logger logger,
	                      @NotNull @JsonProperty(required = true) MappedRunnable updater,
	                      @Nullable YamlCommand startupCommand,
	                      @Nullable List<YamlSubsystem> variousSubsystems) {
		this.buttons = buttons;
		this.logger = logger;
		this.updater = updater;
		this.startupCommand = startupCommand;
		this.variousSubsystems = variousSubsystems;
	}

	/**
	 * @return The buttons for controlling this robot.
	 */
	@NotNull
	public List<CommandButton> getButtons() {
		return buttons;
	}

	/**
	 * @return The logger for recording events and telemetry data.
	 */
	@NotNull
	public Logger getLogger() {
		return logger;
	}

	/**
	 * @return A runnable that updates cached variables.
	 */
	@NotNull
	public Runnable getUpdater() {
		return updater;
	}

	/**
	 * @return The command to run when the robot is first enabled. Can be null to not run a command.
	 */
	@Nullable
	public YamlCommand getStartupCommand() {
		return startupCommand;
	}
}
