package org.usfirst.frc.team449.robot.subsystem.interfaces.Position.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.Position.SubsystemPosition;

@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SetPosition extends Command {

	/**
	 * The subsystem to execute this command on.
	 */
	@NotNull
	private final SubsystemPosition subsystem;

	private double point;

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on.
	 */
	@JsonCreator
	public SetPosition (@NotNull@JsonProperty(required = true) SubsystemPosition subsystem, double point){
		this.subsystem = subsystem;
		this.point = point;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("SetPosition init.", this.getClass());
	}

	/**
	 * Sets position.
	 */
	@Override
	protected void execute(){
		subsystem.setPosition(point);
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
	protected void end(){
		Logger.addEvent("SetPosition ends.", this.getClass());
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted(){
		Logger.addEvent("SetPosition interrupted!", this.getClass());
	}
}
