package org.usfirst.frc.team449.robot.components;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * a PID controller to control a motor's velocity through PID via the
 * PIDSubsystem
 */
public class PIDVelocityMotor extends PIDComponent implements SpeedController {
	private SpeedController motor;
	private Encoder encoder;
	private double integratedVelocity = 0;
	private double v = 0;
	private String velName;
	private boolean inverted;
	private double speed;
	private boolean rampEnabled;
	private double rampRate;
	private double change;
	private double DELTAT = 0.020;  // TODO EXTERNALIZE THIS
	/**
	 * <p>
	 * This defines the deadband around zero which, when read from
	 * {@link #returnPIDInput()}, will be result in no signal to the motor when
	 * {@link #getSetpoint()} returns 0.
	 * </p>
	 * <p>
	 * This is done to avoid wheel jitter at near-zero values, since it is known
	 * that for a stationary robot, in the absence of external forces, 0 signal
	 * to the motor will result in no wheel movement.
	 * </p>
	 */
	protected double zeroTolerance = 0; // speed at which speed ~= 0

	public PIDVelocityMotor(double p, double i, double d, SpeedController motor, Encoder encoder, String name) {
		super(p, i, d);
		this.motor = motor;
		this.encoder = encoder;
		this.encoder.setPIDSourceType(PIDSourceType.kRate);
		this.velName = name;

	}

	/**
	 * used by the PIDSubsystem to calculate the output wanted for the setpoint
	 * in this class, this returns the attached encoder's rate via getRate()
	 *
	 * @return the rate of rotation of the gyro as per the encoder's getRate()
	 * method
	 * @see Encoder#getRate()
	 */
	@Override
	protected double returnPIDInput() {
		return (inverted ? -encoder.getRate() : encoder.getRate());
	}

	@Override
	public void disable() {
		motor.set(0);
		super.disable();
	}

	@Override
	public void enable() {
		integratedVelocity = 0;
		super.enable();
	}

	/**
	 * Uses the output decided by the PIDSubsystem This output is actually the
	 * derivative of the voltage (and therefore also of the velocity) so it is
	 * integrated by multiplying by the change in time and adding to a field in
	 * this class
	 *
	 * @param output the output decided by the PIDSubsystem, which is the
	 *          derivative of voltage (and velocity)
	 */
	@Override
	protected void usePIDOutput(double output) {
		// If within zero tolerance, do not drive
//		if  (getSetpoint() == 0 && Math.abs(output) < zeroTolerance) {
//			output = 0;
//		}
//
//		motor.pidWrite(output);

//		integratedVelocity += output / 130;
////		integratedVelocity += output;
//		integratedVelocity = Math.max(-1, Math.min(1, integratedVelocity));
//		if (getSetpoint() == 0 && Math.abs(returnPIDInput()) < zeroTolerance) {
//			integratedVelocity = 0;
//		}
//
//		motor.pidWrite(integratedVelocity);

		motor.pidWrite(output / 130);

		SmartDashboard.putNumber(velName + " v", output * 1000);
		SmartDashboard.putNumber(velName + " ztol", zeroTolerance);
//		v = integratedVelocity;
		v = output / 130;
	}

	/**
	 * <p>
	 * This defines the deadband around zero which, when read from
	 * {@link #returnPIDInput()}, will be result in no signal to the motor when
	 * {@link #getSetpoint()} returns 0.
	 * </p>
	 * <p>
	 * This is done to avoid wheel jitter at near-zero values, since it is known
	 * that for a stationary robot, in the absence of external forces, 0 signal
	 * to the motor will result in no wheel movement.
	 * </p>
	 *
	 * @param zeroTolerance the radius of the deadband around zero
	 */
	public void setZeroTolerance(double zeroTolerance) {
		this.zeroTolerance = zeroTolerance;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	@Override
	public void setSetpoint(double setpoint) {
		if (rampEnabled) {
			change = setpoint - getSetpoint();
			change = Math.max(-rampRate * DELTAT, Math.min(rampRate * DELTAT, change));
			setpoint = getSetpoint() + change;
		} else {
			change = 0;
		}
		SmartDashboard.putNumber(velName + " passed setp", setpoint - change);
		SmartDashboard.putNumber(velName + " set setp", setpoint);
		super.setSetpoint(setpoint);
	}

	public void setRampRate(double rampRate) {
		this.rampRate = rampRate;
	}

	public void setRampRateEnabled(boolean rampRateEnabled) {
		this.rampEnabled = rampRateEnabled;
	}

	/**
	 * @return whether or not the pid subsystem is enabled
	 */
	public boolean getEnabled() {
		return this.getPIDController().isEnabled();
	}

	/**
	 * Sets the setpoint for this
	 *
	 * @param v the target velocity
	 */
	@Override
	public void set(double v) {
		this.setSetpoint(v * speed);
	}

	@Override
	public void pidWrite(double output) {
		this.set(output);
	}

	@Override
	public double get() {
		return this.getSetpoint() / speed;
	}

	@Override
	public void set(double speed, byte syncGroup) {
		set(speed);
	}

	@Override
	public void setInverted(boolean isInverted) {
		boolean changed = inverted != isInverted;
		inverted = isInverted;
		if (changed) {
			this.motor.setInverted(!motor.getInverted());
		}
	}

	@Override
	public boolean getInverted() {
		return this.inverted;
	}

	@Override
	public void stopMotor() {
		this.set(0);
	}

	public double getIntegratedVelocity() {
		return integratedVelocity;
	}
	public double getV() {
		return v;
	}
}
