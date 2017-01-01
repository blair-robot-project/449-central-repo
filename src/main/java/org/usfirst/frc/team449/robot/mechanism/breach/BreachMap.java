package org.usfirst.frc.team449.robot.mechanism.breach;

import org.usfirst.frc.team449.robot.components.maps.DoubleSolenoidMap;
import org.usfirst.frc.team449.robot.mechanism.MechanismMap;

/**
 * a map of constants needed for any form of Drive or its subclasses, and not
 * defined higher in the hierarchy
 */
public class BreachMap extends MechanismMap {
	public DoubleSolenoidMap front;
	public DoubleSolenoidMap back;

	/**
	 * creates a new Breach Map based on the configuration in the given message.
	 * Any maps in here are to be shared across all breaching subsystems
	 *
	 * @param message The protobuf message with the data for this object.
	 */
	public BreachMap(maps.org.usfirst.frc.team449.robot.mechanism.BreachMap.Breach message) {
		super(message.getSuper());
		front = new DoubleSolenoidMap(message.getFront());
		back = new DoubleSolenoidMap(message.getBack());
	}
}
