package org.usfirst.frc.team0449.robot.drive.tank.commands;

import org.usfirst.frc.team0449.robot.ReferencingCommand;
import org.usfirst.frc.team0449.robot.drive.tank.TankDriveSubsystem;

/**
 *
 */
public class TogglePid extends ReferencingCommand {

	public TogglePid(TankDriveSubsystem drive) {
		super(drive);
		requires(drive);
		System.out.println("Drive Robot bueno");
	}

	@Override
	protected void initialize() {
		((TankDriveSubsystem) (subsystem)).togglePID();
	}

	@Override
	protected void execute() {
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
