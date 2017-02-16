package org.usfirst.frc.team449.robot.oi;

import edu.wpi.first.wpilibj.command.Subsystem;

public abstract class OISubsystem extends Subsystem {

	public abstract double getDriveAxisLeft();  // TODO put this in TankOI

	public abstract double getDriveAxisRight(); // TODO put this in TankOI

	public abstract void toggleCamera();
}