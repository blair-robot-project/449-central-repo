package org.usfirst.frc.team449.robot.mechanism;

import org.json.JSONObject;
import org.usfirst.frc.team449.robot.RobotMap;

/**
 * a map of constants needed for any form of Mechanism or its subclasses, and
 * not defined higher in the hierarchy
 */
public abstract class MechanismMap extends RobotMap {

	/**
	 * creates a new Mechanism Map based on the configuration in the given json
	 * any maps in here are to be shared across all mechanism subsystems
	 *
	 * @param json a JSONObject containing the configuration for the maps in this
	 *             object
	 */
	public MechanismMap(JSONObject json) {
		super(json);
	}

	public static class DoubleSolenoid extends MapObject {
		public int forward;
		public int reverse;

		public DoubleSolenoid(JSONObject json, String objPath, Class enclosing) {
			super(json, objPath, enclosing);
		}
	}
}
