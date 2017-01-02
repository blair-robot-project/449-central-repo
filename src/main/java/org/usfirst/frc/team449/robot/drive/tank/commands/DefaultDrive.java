package org.usfirst.frc.team449.robot.drive.tank.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.tank.TankDriveSubsystem;
import org.usfirst.frc.team449.robot.oi.OISubsystem;

public class DefaultDrive extends ReferencingCommand {
	public OISubsystem oi;

	double leftThrottle;
	double rightThrottle;

	public DefaultDrive(TankDriveSubsystem drive, OISubsystem oi) {
		super(drive);
		this.oi = oi;
		requires(subsystem);
		System.out.println("Drive Robot bueno");
	}

	@Override
	protected void initialize() {
		((TankDriveSubsystem) subsystem).encoderReset();
		((TankDriveSubsystem) subsystem).setThrottle(0, 0);
	}

	@Override
	protected void execute() {
		leftThrottle = oi.getDriveAxisLeft() * ((maps.org.usfirst.frc.team449.robot.drive.tank.TankDriveMap.TankDrive)
				(subsystem.map)).getLeftCluster().getVelocityPID().getSpeed();
		rightThrottle = oi.getDriveAxisRight() * ((maps.org.usfirst.frc.team449.robot.drive.tank.TankDriveMap.TankDrive)
				(subsystem.map)).getRightCluster().getVelocityPID().getSpeed();
		// pushing forward on the stick gives -1 so it is negated
		((TankDriveSubsystem) subsystem).setThrottle(leftThrottle, rightThrottle);
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
		((TankDriveSubsystem) subsystem).setThrottle(oi.getDriveAxisLeft(), oi.getDriveAxisRight());
	}
}