package org.usfirst.frc.team449.robot.subsystem.interfaces.intake.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandWrapper;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.SubsystemIntake;

/**
 * Sets the mode of the intake.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SetIntakeMode extends YamlCommandWrapper {

	/**
	 * The subsystem to execute this command on.
	 */
	@NotNull
	private final SubsystemIntake subsystem;

	/**
	 * The mode to set this subsystem to.
	 */
	@NotNull
	private final SubsystemIntake.IntakeMode mode;

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on.
	 * @param mode      The mode to set the intake to.
	 */
	@JsonCreator
	public SetIntakeMode(@NotNull @JsonProperty(required = true) SubsystemIntake subsystem,
	                     @NotNull @JsonProperty(required = true) SubsystemIntake.IntakeMode mode) {
		this.subsystem = subsystem;
		this.mode = mode;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("SetIntakeMode init.", this.getClass());
	}

	/**
	 * Set the intake to the given mode.
	 */
	@Override
	protected void execute() {
		subsystem.setMode(mode);
	}

	/**
	 * Finish immediately because this is a state-change command.
	 *
	 * @return true
	 */
	@Override
	protected boolean isFinished() {
		return true;
	}

	/**
	 * Log when this command ends
	 */
	@Override
	protected void end() {
		Logger.addEvent("SetIntakeMode end.", this.getClass());
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("SetIntakeMode Interrupted!", this.getClass());
	}
}