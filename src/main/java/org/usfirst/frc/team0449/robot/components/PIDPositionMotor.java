package org.usfirst.frc.team0449.robot.components;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SpeedController;

/**
 * a PID controller to control a wheel's position through PID via the
 * PIDSubsystem
 */
public class PIDPositionMotor extends PIDComponent {
	private SpeedController motor;
	private Encoder encoder;

	public PIDPositionMotor(double p, double i, double d, SpeedController motor, Encoder encoder) {
		super(p, i, d);
		this.motor = motor;
		this.encoder = encoder;
	}

	/**
	 * used by the PIDSubsystem to calculate the output wanted for the setpoint
	 * in this class, this returns the attached encoder's distance/position via
	 * get()
	 *
	 * @return the rate of rotation of the gyro as per the encoder's get()
	 * method
	 * @see Encoder#get()
	 */
	@Override
	protected double returnPIDInput() {
		return encoder.get();
	}

	/**
	 * Uses the output decided by the PIDSubsystem This output is the normalized
	 * voltage to the motor, effectively directly proportional to the derivative
	 * of the wheel's position
	 *
	 * @param v the output decided by the PIDSubsystem
	 */
	@Override
	protected void usePIDOutput(double v) {
		if (Math.abs(v) < 0.01) {
			this.motor.set(0);
		} else {
			this.motor.set(.8);
		}
	}

	/**
	 * @return whether or not the pid subsystem is enabled
	 */
	public boolean getEnabled() {
		return getEnabled();
	}

	public void reset() {
		encoder.reset();
	}
}
