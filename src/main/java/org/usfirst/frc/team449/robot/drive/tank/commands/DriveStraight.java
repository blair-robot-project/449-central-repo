package org.usfirst.frc.team449.robot.drive.tank.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.tank.TankDriveSubsystem;
import org.usfirst.frc.team449.robot.oi.OISubsystem;

public class DriveStraight extends ReferencingCommand {
	double leftThrottle;
	double rightThrottle;

	private OISubsystem oi;

	public DriveStraight(TankDriveSubsystem drive, OISubsystem oi) {
		super(drive);
		requires(drive);
		this.oi = oi;
		System.out.println("Drive Robot bueno");
	}

	@Override
	protected void initialize() {
	}

	@Override
	protected void execute() {
		rightThrottle = oi.getDriveAxisRight() * ((maps.org.usfirst.frc.team449.robot.drive.tank.TankDriveMap
				.TankDrive)
				(subsystem.map)).getRightCluster().getVelocityPID().getSpeed();
		((TankDriveSubsystem) subsystem).setThrottle(rightThrottle, rightThrottle);
	}

	@Override
	protected boolean isFinished() {
		return false;
	}

	@Override
	protected void end() {
	}

	@Override
	protected void interrupted() {
	}
}