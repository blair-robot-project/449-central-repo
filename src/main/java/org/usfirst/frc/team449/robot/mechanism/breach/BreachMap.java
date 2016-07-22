package org.usfirst.frc.team449.robot.mechanism.breach;

import org.json.JSONObject;
import org.usfirst.frc.team449.robot.mechanism.MechanismMap;

/**
 * a map of constants needed for any form of Drive or its subclasses, and not
 * defined higher in the hierarchy
 */
public class BreachMap extends MechanismMap {
	public DoubleSolenoid front;
	public DoubleSolenoid back;

	/**
	 * creates a new Breach Map based on the configuration in the given json any
	 * maps in here are to be shared across all breaching subsystems
	 *
	 * @param json a JSONObject containing the configuration for the maps in this
	 *             object
	 */
	public BreachMap(JSONObject json) {
		super(json);
	}

	/**
	 * Port number of the back solenoid's forward state
	 */
	// public static final int SOLENOID_BACK_FORWARD_PORT = 0;

	/**
	 * Port number of the back solenoid's reverse state
	 */
	// public static final int SOLENOID_BACK_REVERSE_PORT = 1;

	/**
	 * Port number of the front solenoid's forward state
	 */
	// public static final int SOLENOID_FRONT_FORWARD_PORT = 2;

	/**
	 * Port number of the front solenoid's reverse state
	 */
	// public static final int SOLENOID_FRONT_REVERSE_PORT = 3;
}
