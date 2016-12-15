package org.usfirst.frc.team449.robot.components;

import edu.wpi.first.wpilibj.SpeedController;

public class PIDOutputGetter implements SpeedController {
	private double corrector;

	public PIDOutputGetter() {
		this.corrector = 0;
	}

	@Override
	public void pidWrite(double output) {
		set(output);
	}

	@Override
	public void stopMotor() {
		this.corrector = 0;
	}

	@Override
	public void setInverted(boolean isInverted) {
	}

	public void set(double speed, byte syncGroup) {
		set(speed);
	}

	@Override
	public void set(double speed) {
		corrector = speed;
	}

	@Override
	public boolean getInverted() {
		return false;
	}

	@Override
	public double get() {
		return corrector;
	}

	@Override
	public void disable() {
		this.corrector = 0;
	}
}
