package org.usfirst.frc.team0449.robot;

public abstract class OISubsystem extends MappedSubsystem {
	public OISubsystem(RobotMap map) {
		super(map);
	}

	public abstract double getDriveAxisLeft();  // TODO put this in TankOI

	public abstract double getDriveAxisRight(); // TODO put this in TankOI

	public abstract void toggleCamera();
}