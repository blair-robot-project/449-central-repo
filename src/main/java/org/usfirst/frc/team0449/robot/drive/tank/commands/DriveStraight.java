package org.usfirst.frc.team0449.robot.drive.tank.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team0449.robot.OISubsystem;
import org.usfirst.frc.team0449.robot.ReferencingCommand;
import org.usfirst.frc.team0449.robot.drive.tank.TankDriveMap;
import org.usfirst.frc.team0449.robot.drive.tank.TankDriveSubsystem;

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
		// ((TankDriveSubsystem) Robot.drive).enableDriveStraightCorrector();
		SmartDashboard.putBoolean("straight on", true);
		// ((TankDriveSubsystem) Robot.drive).enableDriveStraightCorrector();
		// SmartDashboard.putBoolean("straigt on", true);
	}

	@Override
	protected void execute() {
		// leftThrottle = Robot.oi.getDriveAxisLeft() * ((TankDriveMap)
		// (Robot.drive.map)).leftCluster.speed;
		rightThrottle = oi.getDriveAxisRight() * ((TankDriveMap) (subsystem.map)).rightCluster.speed;
		((TankDriveSubsystem) subsystem).setThrottle(rightThrottle, rightThrottle);
		SmartDashboard.putNumber("Distance", ((TankDriveSubsystem) subsystem).getDistance());
		// SmartDashboard.putBoolean("straigt on", true);
	}

	@Override
	protected boolean isFinished() {
		return false;
	}

	@Override
	protected void end() {
		// ((TankDriveSubsystem) Robot.drive).disableDriveStraightCorrector();
		SmartDashboard.putBoolean("straight on", false);
		// ((TankDriveSubsystem) Robot.drive).disableDriveStraightCorrector();
		// SmartDashboard.putBoolean("straigt on", false);
	}

	@Override
	protected void interrupted() {
		// ((TankDriveSubsystem) Robot.drive).disableDriveStraightCorrector();
		SmartDashboard.putBoolean("straight on", false);
		// ((TankDriveSubsystem) Robot.drive).disableDriveStraightCorrector();
	}
}
