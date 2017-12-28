package org.usfirst.frc.team449.robot.subsystem.interfaces.intake;

public interface SubsystemPosition {

	// Method for setting the position to a value.
	public static void setPosition(int value){

	}

	// Method for setting the motor output to a value.
	public static void moterOutput(int value){

	}

	// Method for returning the state of the reverse limit switch.
	public static boolean stateOfReverseLimitSwitch(){
		return false;
	}

	// Method for returning the state of the forward limit switch.
	public static boolean stateOfForwardLimitSwitch(){
		return false;
	}

	// Method for enabling the motor.
	public static void enableTheMoter(){

	}

	// Method for disabling the motor.
	public static void disablingTheMoter(){

	}
}
