package org.usfirst.frc.team449.robot.components;

import edu.wpi.first.wpilibj.*;

/**
 * Class for PID controlled <code>SpeedController</code>s (motors).
 * <p>
 * <code>PIDSpeedController</code> objects contain a <code>PIDController</code> object that handles PID
 * calculations. The <code>PIDController</code> object runs in its own thread in the scheduler, independent of other
 * commands.
 * </p>
 * <p>
 * <code>PIDSpeedController</code>s can be used with both absolute and relative setpoints.
 * </p>
 */
public abstract class PIDSpeedController implements SpeedController {
	/**
	 * <code>PIDController</code> that calculates pidWrite values
	 */
	private PIDController pidController;
	/**
	 * The pidOutputDevice device (a motor)
	 */
	private PIDOutput pidOutputDevice;
	/**
	 * The input device (an encoder). This is public so that subsystems can directly access the output device to do
	 * things like reseting it, reading displacement/rate when the source type is rate/displacement, etc.
	 */
	public PIDSource pidSourceDevice;
	/**
	 * The maximmum input value (the input range)
	 */
	private double maxAbsoluteSetpoint;
	/**
	 * Whether the motor is inverted
	 */
	private boolean inverted;
	/**
	 * Whether {@link #set(double)} should use interpret its input as absolute (instead of relative)
	 */
	private boolean useAbsolute;

	private boolean pidEnabled = true;

	/**
	 * Instantiate a new <code>PIDSpeedController</code>
	 *
	 * @param p                   {@link #pidController}'s proportional term
	 * @param i                   {@link #pidController}'s integral term
	 * @param d                   {@link #pidController}'s derivative term
	 * @param f                   {@link #pidController}'s feedforward term
	 * @param period              {@link #pidController}'s thread period
	 * @param maxAbsoluteSetpoint {@link #pidController}'s input range
	 * @param inverted            whether the motor is inverted
	 * @param useAbsolute         whether {@link #set(double)} should use interpret its input as absolute (instead of
	 *                            relative)
	 */
	public PIDSpeedController(double p, double i, double d, double f, double period, double maxAbsoluteSetpoint,
	                          boolean inverted, boolean useAbsolute, PIDSourceType pidSourceType) {
		// Constants
		this.maxAbsoluteSetpoint = maxAbsoluteSetpoint;
		this.inverted = inverted;
		this.useAbsolute = useAbsolute;

		// Set up PID source device
		pidSourceDevice = constructPIDSourceDevice();
		pidSourceDevice.setPIDSourceType(pidSourceType);

		// Set up PID output device
		pidOutputDevice = constructPIDOutputDevice(maxAbsoluteSetpoint);
		((SpeedController) pidOutputDevice).setInverted(getOutputDeviceInverted());
		((SpeedController) pidOutputDevice).disable();

		if (pidEnabled) {
			// Set up PID controller
			pidController = new PIDController(p, i, d, f, this.pidSourceDevice, this.pidOutputDevice, period);
			pidController.setOutputRange(-maxAbsoluteSetpoint, maxAbsoluteSetpoint);
			pidController.setInputRange(-maxAbsoluteSetpoint, maxAbsoluteSetpoint);
			pidController.disable();
			pidController.enable();
			pidController.setSetpoint(0);
		}
	}

	/**
	 * Abstract method overrided in subclass to feed PID motor controller its source device
	 *
	 * @return constructed {@link PIDSource} device
	 */
	public abstract PIDSource constructPIDSourceDevice();

	/**
	 * Abstract method overrided in subclasses to feed PID motor controller its output device.
	 *
	 * @return constructed {@link PIDOutput} device
	 */
	public abstract PIDOutput constructPIDOutputDevice(double maxAbsoluteSetpoint);

	/**
	 * Abstract method overrided in annonymous inner class to tell the PID output device whether or not it is inverted
	 *
	 * @return whether {@link #pidOutputDevice} is inverted
	 */
	public abstract boolean getOutputDeviceInverted();

	/**
	 * Abstract method for getting the value being written to {@link #pidOutputDevice}.
	 *
	 * @return value written to {@link #pidOutputDevice}
	 */
	public abstract double getPIDOutput();

	public void noPIDWrite(double velocity) {
		pidOutputDevice.pidWrite(velocity);
	}

	/**
	 * Method that writes to {@link #pidOutputDevice}.
	 *
	 * @param output value to write to {@link #pidOutputDevice}
	 */
	protected void motorWrite(double output) {
		pidOutputDevice.pidWrite(output);
	}

	/**
	 * Method that returns PID {@link #pidSourceDevice}'s pidOutputDevice (e.g. encoder rate).
	 *
	 * @return PID {@link #pidSourceDevice}'s pidOutputDevice
	 */
	public double getPIDInput() {
		return pidSourceDevice.pidGet();
	}

	/**
	 * Method that gets the PID error term
	 *
	 * @return PID error term
	 */
	public double getPIDError() {
		return pidController.getError();
	}

	/**
	 * Sets the absolute PID setpoint. This method expects a setpoint in the same units as {@link #getPIDInput()}.
	 *
	 * @param setpoint absolute setpoint (within input range)
	 */
	public void setAbsoluteSetpoint(double setpoint) {
		// Set setpoint, inverting motor if necessary
		if (inverted) {
			pidController.setSetpoint(-setpoint);
		} else {
			pidController.setSetpoint(setpoint);
		}
	}

	/**
	 * Sets the relative PID setpoint. This method expects a percentage of the input range
	 * ({@link #maxAbsoluteSetpoint}) between -1.0 and 1.0. The absolute setpoint given to {@link #pidController} is
	 * calculated as {relative setpoint * {@link #maxAbsoluteSetpoint}}.
	 *
	 * @param setpoint relative setpoint (between -1.0 and 1.0)
	 */
	public void setRelativeSetpoint(double setpoint) {
		// Set setpoint, inverting motor if necessary
		if (inverted) {
			pidController.setSetpoint(setpoint * -maxAbsoluteSetpoint);
		} else {
			pidController.setSetpoint(setpoint * maxAbsoluteSetpoint);
		}
	}

	/**
	 * Gets current absolute setpoint
	 *
	 * @return current absolute setpoint
	 */
	public double getAbsoluteSetpoint() {
		return pidController.getSetpoint();
	}

	/**
	 * Gets current relative setpoint (a percentage of {@link #maxAbsoluteSetpoint}, between -1.0 and 1.0)
	 *
	 * @return current relative setpoint
	 */
	public double getRelativeSetpoint() {
		return pidController.getSetpoint() / maxAbsoluteSetpoint;
	}

	/**
	 * Gets the real world value measured by the PID source device
	 *
	 * @return value measured by {@link #pidSourceDevice}
	 */
	public double getSourceMeasuredValue() {
		return pidSourceDevice.pidGet();
	}

	/**
	 * Set the {@link #pidController}'s input range
	 *
	 * @param min minimum input
	 * @param max maximum input
	 */
	private void setInputRange(double min, double max) {
		pidController.setInputRange(min, max);
	}

	/**
	 * Set the {@link #pidController}'s pidOutputDevice range
	 *
	 * @param min minimum pidOutputDevice
	 * @param max maximum pidOutputDevice
	 */
	private void setOutputRange(double min, double max) {
		pidController.setOutputRange(min, max);
	}

	/**
	 * Get whether the {@link #pidController} thinks it is on target
	 *
	 * @return whether the {@link #pidController} thinks it is on target
	 */
	public boolean onTarget() {
		return pidController.onTarget();
	}

	/**
	 * Set whether to use absolute values (instead of relative values) when calling {@link #set(double)}. This directly
	 * sets {@link #useAbsolute}
	 *
	 * @param useAbsolute new {@link #useAbsolute}
	 */
	public void setUseAbsolute(boolean useAbsolute) {
		this.useAbsolute = useAbsolute;
	}

	/**
	 * Reset the controller
	 */
	public void reset() {
		setAbsoluteSetpoint(0);
		pidController.reset();
		pidController.enable();
		System.out.println("encoderReset");
	}

	/**
	 * {@link SpeedController} method for getting the relative set velocity. {@link SpeedController}'s expect a number
	 * between -1 and 1 so this method returns the relative setpoint (this is a wrapper on
	 * {@link #getRelativeSetpoint()}).
	 *
	 * @return relative setpoint
	 */
	@Override
	public double get() {
		return getRelativeSetpoint();
	}

	/**
	 * Deprecated {@link SpeedController} method for setting relative set velocity.
	 *
	 * @param velocity  set velocity
	 * @param syncGroup update group (not used)
	 * @deprecated use {@link #set(double)} instead
	 */
	@Deprecated
	public void set(double velocity, byte syncGroup) {
		System.out.println("Warning, you are using a deprecated method void set(double, double). You will use method" +
				" " +
				"void set(double) instead.");
		set(velocity);
	}

	/**
	 * {@link SpeedController} method for setting the relative set velocity. {@link SpeedController}'s expect a number
	 * between -1 and 1 so this method sets the relative setpoint (this is a wrapper on
	 * {@link #setRelativeSetpoint(double)}).
	 *
	 * @param velocity relative setpoint
	 */
	@Override
	public void set(double velocity) {
		setRelativeSetpoint(velocity);
	}

	/**
	 * {@link SpeedController} method for setting whether the controller is inverted
	 *
	 * @param isInverted whether the motor is inverted ({@link #inverted}
	 * @see #getOutputDeviceInverted()
	 */
	@Override
	public void setInverted(boolean isInverted) {
		inverted = isInverted;
	}

	/**
	 * {@link SpeedController} method for getting whether the controller is inverted
	 *
	 * @return whether the motor is inverted ({@link #inverted}
	 */
	@Override
	public boolean getInverted() {
		return inverted;
	}

	/**
	 * {@link SpeedController} method for disabling the speed controller
	 */
	@Override
	public void disable() {
		pidOutputDevice.pidWrite(0);
	}

	/**
	 * {@link SpeedController} method for stoping motor movement. Motor can be moved again by calling set without
	 * having
	 * to re-enable the motor.
	 */
	@Override
	public void stopMotor() {
		set(0);
	}

	/**
	 * {@link PIDOutput} method for writing a velocity to the output device
	 *
	 * @param velocity velocity to write to the output device
	 */
	@Override
	public void pidWrite(double velocity) {
		set(velocity);
	}
}
