package org.usfirst.frc.team449.robot;

import com.google.protobuf.Message;

/**
 * NOTE: This class is probably useless. It's here because Noah hasn't gotten around to deleting it.
 * <p>
 * This is the base class for robot maps constructed from the .cfg configuration
 * file. This class holds basic map classes like <code>Motor</code>,
 * <code>Encoder</code>, <code>PID</code>.
 * </p>
 */
public abstract class RobotMap {
	protected Message message;

	public RobotMap(Message message){
		this.message = message;
	}
}