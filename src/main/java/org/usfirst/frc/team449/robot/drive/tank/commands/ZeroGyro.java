package org.usfirst.frc.team449.robot.drive.tank.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.tank.TankDriveSubsystem;

public class ZeroGyro extends ReferencingCommand {

	public ZeroGyro(TankDriveSubsystem drive) {
		super(drive);
		requires(drive);
	}

	public void execute() {
		((TankDriveSubsystem) subsystem).zeroGyro();
	}

	@Override
	protected void initialize() {
	}

	@Override
	protected boolean isFinished() {
		return true;
	}

	@Override
	protected void end() {
	}

	@Override
	protected void interrupted() {
	}
}
