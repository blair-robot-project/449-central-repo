package org.usfirst.frc.team449.robot.subsystem.interfaces.Position;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
public interface SubsystemPosition {

	// Method for setting the position to a value.
	void setPosition(double value);

	// Method for setting the motor output to a value.
	void setMotorOutput(double value);

	// Method for returning the state of the reverse limit switch.
	boolean getReverseLimit();

	// Method for returning the state of the forward limit switch.
	boolean getForwardLimit();

	// Method for enabling the motor.
	void enableMotor();

	// Method for disabling the motor.
	void disableMotor();
}
