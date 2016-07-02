package org.usfirst.frc.team0449.robot.drive;

import org.json.JSONObject;
import org.usfirst.frc.team0449.robot.RobotMap;

/**
 * a map of constants needed for any form of Drive or its subclasses, and not
 * defined higher in the hierarchy
 */
public abstract class DriveMap extends RobotMap {
	/**
	 * creates a new Drive Map based on the configuration in the given json any
	 * maps in here are to be shared across all drive subsystems
	 *
	 * @param json a JSONObject containing the configuration for the maps in this
	 *             object
	 */
	public DriveMap(JSONObject json) {
		super(json);
	}
}
