package org.usfirst.frc.team449.robot.mechanism.intake;

import org.json.JSONObject;
import org.usfirst.frc.team449.robot.components.maps.DoubleSolenoidMap;
import org.usfirst.frc.team449.robot.components.maps.IRSensorMap;
import org.usfirst.frc.team449.robot.components.maps.MotorMap;
import org.usfirst.frc.team449.robot.components.maps.UltrasonicMap;
import org.usfirst.frc.team449.robot.mechanism.MechanismMap;

/**
 * a map of constants needed for any form of Drive or its subclasses, and not
 * defined higher in the hierarchy
 */
public class IntakeMap extends MechanismMap {
	public MotorMap motor;
	public DoubleSolenoidMap solenoid;
	public IRSensorMap leftIR;
	public IRSensorMap rightIR;
	public double OUTPUT_SPEED;
	public double INPUT_SPEED;
	public UltrasonicMap leftUltrasonic;

	public UltrasonicMap rightUltrasonic;

	/**
	 * creates a new Intake Map based on the configuration in the given json any
	 * maps in here are to be shared across all intake subsystems
	 *
	 * @param json a JSONObject containing the configuration for the maps in this
	 *             object
	 */
	public IntakeMap(JSONObject json) {
		super(json);
	}
}
