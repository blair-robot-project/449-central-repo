package org.usfirst.frc.team449.robot.oi;

import org.usfirst.frc.team449.robot.MappedSubsystem;

public abstract class OISubsystem extends MappedSubsystem {
	public OISubsystem(maps.org.usfirst.frc.team449.robot.oi.OIMap.OI map) {
		super(map);
	}

	public abstract double getDriveAxisLeft();  // TODO put this in TankOI

	public abstract double getDriveAxisRight(); // TODO put this in TankOI

	public abstract void toggleCamera();
}