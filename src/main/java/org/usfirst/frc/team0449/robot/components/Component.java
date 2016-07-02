package org.usfirst.frc.team0449.robot.components;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * A class designed for subsystems not linked to commands and that are parts of
 * more complex subsystems. Should be the super class for all non-pid based
 * classes in components packages
 */
public abstract class Component extends Subsystem {

	public Component(String name) {
		super(name);
	}

	public Component() {
	}

	@Override
	final protected void initDefaultCommand() {
	}

	public abstract boolean getInverted();

	public abstract void setInverted(boolean b);
}
