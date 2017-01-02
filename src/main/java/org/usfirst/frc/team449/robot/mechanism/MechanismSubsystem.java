package org.usfirst.frc.team449.robot.mechanism;

import org.usfirst.frc.team449.robot.MappedSubsystem;

/**
 * <p>
 * This is the base class for all mechanisms on the robot. It extends
 * {@link MappedSubsystem} and contains a Message to hold constants.
 * </p>
 */
public abstract class MechanismSubsystem extends MappedSubsystem {
	/**
	 * Instantiates a new <code>MechanismSubsystem</code> with a
	 * <code>RobotMap</code>
	 *
	 * @param map constants map
	 */
	public MechanismSubsystem(maps.org.usfirst.frc.team449.robot.mechanism.MechanismMap.Mechanism map) {
		super(map);
	}
}
