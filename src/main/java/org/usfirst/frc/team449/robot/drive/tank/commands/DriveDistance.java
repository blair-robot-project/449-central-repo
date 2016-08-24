package org.usfirst.frc.team449.robot.drive.tank.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.tank.TankDriveMap;
import org.usfirst.frc.team449.robot.drive.tank.TankDriveSubsystem;

/**
 * <code>Command</code> for driving a certain distance (for use in auto period)
 */
public class DriveDistance extends ReferencingCommand {

	private double distance;
	private int direction;

	public DriveDistance(TankDriveSubsystem drive, double distance, double timeout) {
		super(drive, timeout);
		requires(drive);
		if (distance < 0) {
			this.direction = -1;
		} else {
			this.direction = 1;
		}
		this.distance = direction * distance;
	}

	@Override
	protected void initialize() {
		System.out.println("DriveDistance init");
		((TankDriveSubsystem) (subsystem)).encoderReset();
	}

	@Override
	protected void execute() {
		TankDriveMap map = (TankDriveMap) (subsystem.map);
		((TankDriveSubsystem) (subsystem)).setThrottle(direction * -map.leftCluster.speed * .5, direction * -map
				.rightCluster.speed * .5);
	}

	@Override
	protected boolean isFinished() {
		SmartDashboard.putNumber("Distance", ((TankDriveSubsystem) (subsystem)).getDistance());
		return ((TankDriveSubsystem) (subsystem)).getDistance() > this.distance;
	}

	@Override
	protected void end() {
		System.out.println("DriveDistance end");
		((TankDriveSubsystem) (subsystem)).setThrottle(0, 0);
	}

	@Override
	protected void interrupted() {
		System.out.println("DriveDistance interupted");
		TankDriveSubsystem drive = (TankDriveSubsystem) (subsystem);
		drive.setThrottle(0, 0);
	}
}
