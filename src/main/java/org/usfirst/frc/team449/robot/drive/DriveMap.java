package org.usfirst.frc.team449.robot.drive;

import org.usfirst.frc.team449.robot.RobotMap;

/**
 * a map of constants needed for any form of Drive or its subclasses, and not
 * defined higher in the hierarchy
 */
public abstract class DriveMap extends RobotMap {
	/**
	 * creates a new Drive Map based on the configuration in the given message.
	 * Anything in here is to be shared across all drive subsystems.
	 *
	 * @param message The protobuf message with the data for this object
	 */
	public DriveMap(maps.org.usfirst.frc.team449.robot.drive.DriveMap.Drive message) {
		super(message);
	}
}
