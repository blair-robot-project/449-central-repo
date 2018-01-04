package org.usfirst.frc.team449.robot.subsystem.interfaces.Position.commands;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandWrapper;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.Position.SubsystemPosition;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.SubsystemIntake;

@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class PositionCommands extends YamlCommandWrapper {

	/**
	 * The subsystem to execute this command on.
	 */
	@NotNull
	private final SubsystemPosition subsystem;

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on.
	 */
	public PositionCommands (@NotNull@JsonProperty(required = true) SubsystemPosition subsystem){
		this.subsystem = subsystem;
	}

	/**
	 * Log when this command is initialized
	 */

	@Override
	protected void initialize() { Logger.addEvent("PositionCommands init.", this.getClass());}

	/**
	 * Enables the motor.
	 */
	@Override
	protected void execute() {
		subsystem.enableMotor();
	}

	/**
	 * Finish immediately because this is a state-change command.
	 *
	 * @return true
	 */
	@Override
	protected boolean isFinished() {
		return false;
	}

	/**
	 * Log when this command ends
	 */
	@Override
	protected void end() {
		Logger.addEvent("PositionCommands end.", this.getClass());
	}

}

