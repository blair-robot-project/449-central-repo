package org.usfirst.frc.team449.robot.mechanism;

import org.usfirst.frc.team449.robot.RobotMap;

/**
 * a map of constants needed for any form of Mechanism or its subclasses, and
 * not defined higher in the hierarchy
 */
public abstract class MechanismMap extends RobotMap {

	/**
	 * creates a new Mechanism Map based on the configuration in the given message.
	 * Any maps in here are to be shared across all mechanism subsystems.
	 *
	 * @param message The protobuf message with the data for this object.
	 */
	public MechanismMap(maps.org.usfirst.frc.team449.robot.mechanism.MechanismMap.Mechanism message) {
		super(message);
	}
}