package org.usfirst.frc.team449.robot.oi;

import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.RobotMap;

public abstract class OISubsystem extends MappedSubsystem {
	public OISubsystem(RobotMap map) {
		super(map);
	}

	public abstract double getDriveAxisLeft();  // TODO put this in TankOI
	public abstract double getDriveAxisRight(); // TODO put this in TankOI
	public abstract void toggleCamera();
}