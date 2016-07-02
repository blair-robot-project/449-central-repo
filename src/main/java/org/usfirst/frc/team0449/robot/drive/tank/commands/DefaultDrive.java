package org.usfirst.frc.team0449.robot.drive.tank.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team0449.robot.OISubsystem;
import org.usfirst.frc.team0449.robot.ReferencingCommand;
import org.usfirst.frc.team0449.robot.drive.tank.TankDriveMap;
import org.usfirst.frc.team0449.robot.drive.tank.TankDriveSubsystem;

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
		((TankDriveSubsystem) subsystem).reset();
	}

	@Override
	protected void execute() {
		leftThrottle = oi.getDriveAxisLeft() * ((TankDriveMap) (subsystem.map)).leftCluster.speed;
		rightThrottle = oi.getDriveAxisRight() * ((TankDriveMap) (subsystem.map)).rightCluster.speed;
		// pushing forward on the stick gives -1 so it is negated
		((TankDriveSubsystem) subsystem).setThrottle(leftThrottle, rightThrottle);
		SmartDashboard.putNumber("Distance", ((TankDriveSubsystem) subsystem).getDistance());
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
