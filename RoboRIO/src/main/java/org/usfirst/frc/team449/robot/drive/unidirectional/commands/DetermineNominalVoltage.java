package org.usfirst.frc.team449.robot.drive.unidirectional.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectional;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandWrapper;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.other.Logger;

/**
 * Run the motors until they move, slowly increasing the voltage up from 0.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class DetermineNominalVoltage<T extends YamlSubsystem & DriveUnidirectional> extends YamlCommandWrapper {

	/**
	 * The drive subsystem to execute this command on.
	 */
	@NotNull
	private final T subsystem;
	/**
	 * The minimum speed, in RPS, at which the drive is considered to be moving.
	 */
	private final double minSpeed;
	/**
	 * The current percent of max output commanded, on [0, 1].
	 */
	private double percentCommanded;

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on
	 * @param minSpeed  The minimum speed, in RPS, at which the drive is considered to be moving.
	 */
	@JsonCreator
	public DetermineNominalVoltage(@NotNull @JsonProperty(required = true) T subsystem,
	                               @JsonProperty(required = true) double minSpeed) {
		//Initialize stuff
		this.subsystem = subsystem;
		this.minSpeed = minSpeed;
		requires(subsystem);
	}

	/**
	 * Log initialization
	 */
	@Override
	protected void initialize() {
		percentCommanded = 0;
		//Reset drive velocity (for safety reasons)
		subsystem.fullStop();
		Logger.addEvent("DetermineNominalVoltage init", this.getClass());
	}

	/**
	 * Send output to motors
	 */
	@Override
	protected void execute() {
		//Adjust it by 0.01 per second, so 0.01 * 20/1000, which is 0.0002
		percentCommanded += 0.0002;
		//Set the velocity
		subsystem.setOutput(percentCommanded, percentCommanded);
	}

	/**
	 * Exit if the motors are moving.
	 *
	 * @return True if the motors are moving, false otherwise.
	 */
	@Override
	protected boolean isFinished() {
		return Math.max(Math.abs(subsystem.getLeftVelCached()), Math.abs(subsystem.getRightVelCached())) >= minSpeed;
	}

	/**
	 * Stop the drive and log when the command ends.
	 */
	@Override
	protected void end() {
		//Brake on exit. Yes this should be setOutput because often we'll be testing how well the PID loop handles a full stop.
		subsystem.fullStop();
		Logger.addEvent("DetermineNominalVoltage end.", this.getClass());
	}

	/**
	 * Log and stop the drive when the command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("DetermineNominalVoltage Interrupted! Stopping the robot.", this.getClass());
		//Brake if we're interrupted
		subsystem.fullStop();
	}
}
