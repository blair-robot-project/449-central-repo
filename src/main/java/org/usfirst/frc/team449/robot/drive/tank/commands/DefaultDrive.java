package org.usfirst.frc.team449.robot.drive.tank.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.tank.TankDriveMap;
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
		leftThrottle = oi.getDriveAxisLeft() * ((TankDriveMap) (subsystem.map)).leftCluster.speed;
		rightThrottle = oi.getDriveAxisRight() * ((TankDriveMap) (subsystem.map)).rightCluster.speed;
		// pushing forward on the stick gives -1 so it is negated
		SmartDashboard.putNumber("Left Joystick", leftThrottle);
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