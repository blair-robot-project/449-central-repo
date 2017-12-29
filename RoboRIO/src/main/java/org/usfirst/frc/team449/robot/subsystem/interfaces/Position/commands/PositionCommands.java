package org.usfirst.frc.team449.robot.subsystem.interfaces.Position.commands;

import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandWrapper;
import org.usfirst.frc.team449.robot.subsystem.interfaces.Position.SubsystemPosition;

public class PositionCommands extends YamlCommandWrapper implements SubsystemPosition {
	@Override
	public void setPosition(int value) {

	}

	@Override
	public void setMotorOutput(int value) {

	}

	@Override
	public boolean getReverseLimit() {
		return false;
	}

	@Override
	public boolean getForwardLimit() {
		return false;
	}

	@Override
	public void enableMotor() {

	}

	@Override
	public void disableMotor() {

	}

	@Override
	protected boolean isFinished() {
		return false;
	}
}
