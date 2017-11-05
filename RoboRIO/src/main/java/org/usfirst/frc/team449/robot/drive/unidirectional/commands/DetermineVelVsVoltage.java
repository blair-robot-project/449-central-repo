package org.usfirst.frc.team449.robot.drive.unidirectional.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectional;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandWrapper;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.other.Clock;
import org.usfirst.frc.team449.robot.other.Logger;

/**
 * A command to run the robot at a range of voltages and record the velocity.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class DetermineVelVsVoltage<T extends YamlSubsystem & DriveUnidirectional> extends YamlCommandWrapper {

	/**
	 * The subsystem to execute this command on.
	 */
	@NotNull
	private final T subsystem;

	/**
	 * How far, in feet, to drive for each trial.
	 */
	private final double distanceToDrive;

	/**
	 * How many trials to do for each voltage.
	 */
	private final int numTrials;

	/**
	 * A list of all the voltages to be tested, from (0, 1].
	 */
	private final double[] voltagePercentsToTest;

	/**
	 * The maximum measured speed, in feet/sec, for the current trial.
	 */
	private double maxSpeedForTrial;

	/**
	 * The time the maximum speed for this trial was measured at.
	 */
	private long timeMaxMeasuredAt;

	/**
	 * The index, in the list of voltages to test, of the voltage currently being tested.
	 */
	private int voltageIndex;

	/**
	 * How many trials are left for the current voltage.
	 */
	private int trialsRemaining;

	/**
	 * The current sign of the output. Alternates every trial so we just drive back and forth.
	 */
	private int sign;

	/**
	 * The average speed of the two sides. Field to avoid garbage collection.
	 */
	private double avgSpeed;

	/**
	 * Whether the distance for this trial has been driven. Field to avoid garbage collection.
	 */
	private boolean drivenDistance;

	/**
	 * Default constructor.
	 *
	 * @param subsystem       The subsystem to execute this command on.
	 * @param distanceToDrive How far, in feet, to drive for each trial.
	 * @param numTrials       How many trials to do for each voltage.
	 * @param voltagesToTest  A list of all the voltages to be tested, from (0, 12].
	 */
	@JsonCreator
	public DetermineVelVsVoltage(@NotNull @JsonProperty(required = true) T subsystem,
	                             @JsonProperty(required = true) double distanceToDrive,
	                             @JsonProperty(required = true) int numTrials,
	                             @JsonProperty(required = true) double[] voltagesToTest) {
		this.subsystem = subsystem;
		this.distanceToDrive = distanceToDrive;
		this.numTrials = numTrials;

		//Convert from volts to percentages because CANTalons.
		this.voltagePercentsToTest = new double[voltagesToTest.length];
		for (int i = 0; i < voltagesToTest.length; i++) {
			this.voltagePercentsToTest[i] = voltagesToTest[i] / 12.;
		}
	}

	/**
	 * Reset the encoder position and variables.
	 */
	@Override
	protected void initialize() {
		subsystem.resetPosition();
		sign = 1;
		voltageIndex = 0;
		maxSpeedForTrial = 0;
		timeMaxMeasuredAt = 0;
		trialsRemaining = numTrials;
		subsystem.setOutput(voltagePercentsToTest[voltageIndex], voltagePercentsToTest[voltageIndex]);
		SmartDashboard.putNumber("Desired voltage", voltagePercentsToTest[voltageIndex] * 12);
	}

	/**
	 * Update the max speed for this trial and check if this trial is finished.
	 */
	@Override
	protected void execute() {
		//Multiply each by sign so that only the movement in the correct direction is counted and leftover momentum from
		// the previous trial isn't.

		avgSpeed = (sign * subsystem.getLeftVelCached() + sign * subsystem.getRightVelCached()) / 2.;

		if (avgSpeed > maxSpeedForTrial) {
			maxSpeedForTrial = avgSpeed;
			timeMaxMeasuredAt = Clock.currentTimeMillis();
		}

		SmartDashboard.putNumber("Average Distance", (subsystem.getLeftPosCached() + subsystem.getRightPosCached()) / 2.);
		SmartDashboard.putNumber("Average Speed", avgSpeed);

		//Check if we've driven past the given distance
		if (sign == -1) {
			drivenDistance = (subsystem.getLeftPosCached() + subsystem.getRightPosCached()) / 2. <= 0;
		} else {
			drivenDistance = (subsystem.getLeftPosCached() + subsystem.getRightPosCached()) / 2. >= distanceToDrive;
		}

		//If we've driven past, log the max speed and reset the variables.
		if (drivenDistance) {
			//Log
			Logger.addEvent(Long.toString(timeMaxMeasuredAt), this.getClass());

			//Reset
			maxSpeedForTrial = 0;

			//Switch direction
			sign *= -1;

			//Finished a trial
			trialsRemaining--;

			//Go onto the next voltage if we've done enough trials
			if (trialsRemaining <= 0) {
				trialsRemaining = numTrials;
				voltageIndex++;

				//Exit if we've done all trials for all voltages
				if (voltageIndex >= voltagePercentsToTest.length) {
					return;
				}
			}

			//Set the output to the correct voltage and sign
			subsystem.setOutput(sign * voltagePercentsToTest[voltageIndex], sign * voltagePercentsToTest[voltageIndex]);
			SmartDashboard.putNumber("Desired voltage", voltagePercentsToTest[voltageIndex] * 12.);
		}
	}

	/**
	 * Finish when all trials have been run.
	 *
	 * @return true if all trials have be run, false otherwise.
	 */
	@Override
	protected boolean isFinished() {
		return voltageIndex >= voltagePercentsToTest.length;
	}

	/**
	 * Do nothing, no logging because we want to be able to use R's subset method to find the max speeds.
	 */
	@Override
	protected void end() {
		//Nothing!
	}

	/**
	 * Log when interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("DetermineVelVsVoltage Interrupted!", this.getClass());
	}
}