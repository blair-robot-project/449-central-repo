package org.usfirst.frc.team0449.robot.drive.tank.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team0449.robot.ReferencingCommand;
import org.usfirst.frc.team0449.robot.drive.tank.TankDriveSubsystem;

public class TurnAngle extends ReferencingCommand {

	private double theta;
	private boolean done;

	public TurnAngle(TankDriveSubsystem drive, double theta) {
		super(drive);
		requires(drive);
		this.theta = theta;
	}

	public TurnAngle(TankDriveSubsystem drive, double theta, double timeout) {
		super(drive, timeout);
		requires(drive);
		this.theta = theta;
	}

	@Override
	protected void initialize() {
		System.out.println("TurnAngle init");
		((TankDriveSubsystem) subsystem).enableAngleController();
		((TankDriveSubsystem) subsystem).setTurnToAngle(theta);
		done = false;
		SmartDashboard.putBoolean("turnangle done", done);
	}

	@Override
	protected void execute() {
	}

	@Override
	protected boolean isFinished() {
		done = ((TankDriveSubsystem) subsystem).getTurnAngleDone();
		SmartDashboard.putBoolean("turnangle done", done);
		return done;
	}

	@Override
	protected void end() {
		System.out.println("TurnAngle end");

		((TankDriveSubsystem) subsystem).disableAngleController();
		done = true;
		SmartDashboard.putBoolean("turnangle done", done);
	}

	@Override
	protected void interrupted() {
		System.out.println("TurnAngle interrupted");
		((TankDriveSubsystem) subsystem).disableAngleController();
		done = true;
		SmartDashboard.putBoolean("turnangle done", done);
	}
}
