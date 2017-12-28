package org.usfirst.frc.team449.robot.subsystem.interfaces.intake;

public interface SubsystemPosition {

	// Method for setting the position to a value.
	public void setPosition(int value);

	// Method for setting the motor output to a value.
	public void setMotorOutput(int value);

	// Method for returning the state of the reverse limit switch.
	public boolean getReverseLimit();

	// Method for returning the state of the forward limit switch.
	public boolean getForwardLimit();

	// Method for enabling the motor.
	public void enableMotor();

	// Method for disabling the motor.
	public void disableMotor();
}
