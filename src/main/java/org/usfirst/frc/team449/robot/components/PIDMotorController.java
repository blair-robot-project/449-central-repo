package org.usfirst.frc.team449.robot.components;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Abstract class for PID controlled <code>SpeedController</code>s (motors).
 * <p>
 * <code>PIDMotorController</code> objects contain a <code>PIDController</code> object that handles PID
 * calculations. The <code>PIDController</code> object runs in its own thread in the scheduler, independent of other
 * commands.
 * </p>
 * <p>
 * <p>
 * <code>PIDMotorController</code>s can be used with both absolute and relative setpoints.
 * </p>
 * <p>
 * <p>
 * <code>PIDMotorController</code>s contain abstract methods to be overloaded to set the PID loop type
 * ({@link #setPIDSourceType()}) and to write directly to the motor object ({@link #motorWrite(double)}).
 * </p>
 */
public abstract class PIDMotorController implements SpeedController {
    /**
     * <code>PIDController</code> that calculates pidWrite values
     */
    private PIDController pidController;
    /**
     * The output device (a motor)
     */
    private PIDOutput output;
    /**
     * The input device (an encoder)
     */
    private PIDSource source;
    /**
     * The proportional term in the PID loop
     */
    private double kP;
    /**
     * The integral term in the PID loop
     */
    private double kI;
    /**
     * The derivative term in the PID loop
     */
    private double kD;
    /**
     * The period at which <code>pidController</code>'s thread should run
     */
    private double period;
    /**
     * The maximmum input value (the input range)
     */
    private double maxAbsoluteSetpoint;
    /**
     * The built-in zero tolerance (to stop {@link #usePIDOutput(double)} once it is on target; this is indepedent of
     * the <code>PIDController</code>'s zero tolerance that only specifies when the controller is
     * {@link PIDController#onTarget()}).
     */
    private double zeroTolerance;
    /**
     * Whether the motor is inverted
     */
    private boolean inverted;
    /**
     * Whether {@link #set(double)} should use interpret its input as absolute (instead of relative)
     */
    private boolean useAbsolute;

    /**
     * Instantiate a new <code>PIDMotorController</code>
     *
     * @param p                   {@link #pidController}'s proportional term
     * @param i                   {@link #pidController}'s integral term
     * @param d                   {@link #pidController}'s derivative term
     * @param f                   {@link #pidController}'s feedforward term
     * @param period              {@link #pidController}'s thread period
     * @param maxAbsoluteSetpoint {@link #pidController}'s input range
     * @param zeroTolerance       built-in zero tolerance ({@link #zeroTolerance})
     * @param inverted            whether the motor is inverted
     * @param useAbsolute         whether {@link #set(double)} should use interpret its input as absolute (instead of relative)
     * @param output              the motor controller's output device (a motor)
     * @param source              the motor controller's input device (an encoder)
     */
    public PIDMotorController(double p, double i, double d, double f, double period, double maxAbsoluteSetpoint,
                              double zeroTolerance, boolean inverted, boolean useAbsolute, PIDOutput output,
                              PIDSource source) {
        kP = p;
        kI = i;
        kD = d;
        this.period = period;
        this.maxAbsoluteSetpoint = maxAbsoluteSetpoint;
        this.zeroTolerance = zeroTolerance;
        this.inverted = inverted;
        this.output = output;
        this.source = source;
        this.source.setPIDSourceType(setPIDSourceType());
        this.useAbsolute = useAbsolute;
        pidController = new PIDController(p, i, d, f, this.source, this.output, period);
        pidController.setOutputRange(-maxAbsoluteSetpoint, maxAbsoluteSetpoint);
    }

    /**
     * This method is run in the constructor to set the {@link #source}'s {@link PIDSourceType}. This method should be
     * overrided in velocity and displacement controllers with their respective values.
     *
     * @return motor controller's {@link PIDSourceType}
     */
    protected abstract PIDSourceType setPIDSourceType();

    /**
     * Abstract method that should be overrided to write to {@link #output}.
     *
     * @param output value to write to {@link #output}
     */
    protected abstract void motorWrite(double output);

    /**
     * Method that returns PID {@link #source}'s output (e.g. encoder rate).
     *
     * @return PID {@link #source}'s output
     */
    public double returnPIDInput() {
        return source.pidGet();
    }

    /**
     * Sets the absolute PID setpoint. This method expects a setpoint in the same units as {@link #returnPIDInput()}. If
     * the given absolute setpoint exceeds the input range ({@link #maxAbsoluteSetpoint}), it will be clamped to
     * {@link #maxAbsoluteSetpoint}.
     *
     * @param setpoint absolute setpoint (within input range)
     */
    public void setAbsoluteSetpoint(double setpoint) {
        // Clamp to within input range
        setpoint = setpoint > maxAbsoluteSetpoint ? maxAbsoluteSetpoint : setpoint;
        setpoint = setpoint < -maxAbsoluteSetpoint ? -maxAbsoluteSetpoint : setpoint;

        // Set setpoing, inverting motor if necessary
        if (inverted) {
            pidController.setSetpoint(-setpoint);
        } else {
            pidController.setSetpoint(setpoint);
        }
    }

    /**
     * Sets the relative PID setpoint. This method expects a percentage of the input range
     * ({@link #maxAbsoluteSetpoint}) between -1.0 and 1.0. The absolute setpoint given to {@link #pidController} is
     * calculated as {relative setpoint * {@link #maxAbsoluteSetpoint}}. If a larger magnitude number is passed to this
     * method, it will be clamped to -1.0 to 1.0.
     *
     * @param setpoint relative setpoint (between -1.0 and 1.0)
     */
    public void setRelativeSetpoint(double setpoint) {
        // Clamp to -1 to 1
        setpoint = setpoint > 1.0 ? 1.0 : setpoint;
        setpoint = setpoint < -1.0 ? -1.0 : setpoint;

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
     * Run by the {@link #pidController} to write to the output device (motor). Do not call this method in outside of
     * {@link #pidController}! Checks if <code>output</code> is within zero tolerance: if so, writes zero to the output
     * device, if not, writes <code>output</code> to the output device.
     *
     * @param output output to write to the output device
     */
    public void usePIDOutput(double output) {
        if (getAbsoluteSetpoint() == 0 && Math.abs(output) < zeroTolerance) {
            output = 0;
        }

        motorWrite(output);
        SmartDashboard.putNumber("output: ", output);
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
     * Set the {@link #pidController}'s output range
     *
     * @param min minimum output
     * @param max maximum output
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
     * {@link SpeedController}'s method for getting velocity. Is a wrapper on {@link #returnPIDInput()}
     *
     * @return {@link #returnPIDInput()}
     */
    @Override
    public double get() {
        return returnPIDInput();
    }

    /**
     * Deprecated {@link SpeedController} method for setting velocity and syncGroup
     * @param velocity     velocity
     * @param syncGroup syncGroup bits
     * @deprecated Replaced with {@link #set(double)}
     */
    @Deprecated
    @Override
    public void set(double velocity, byte syncGroup) {
        System.out.println("This is deprecated. You should be using set(speed), not set(speed, syncGroup).");
    }

    /**
     * {@link SpeedController} method for setting velocity. Sets using {@link #setAbsoluteSetpoint(double)} if
     * {@link #useAbsolute} is <code>true</code> and {@link #setRelativeSetpoint(double)} otherwise.
     *
     * @param velocity absolute or relative velocity to write to {@link #output}
     */
    @Override
    public void set(double velocity) {
        if (useAbsolute) {
            setAbsoluteSetpoint(velocity);
        } else {
            setRelativeSetpoint(velocity);
        }
    }

    /**
     * {@link SpeedController} method for setting whether the motor is inverted
     * @param isInverted whether the motor is inverted ({@link #inverted}
     */
    @Override
    public void setInverted(boolean isInverted) {
        inverted = isInverted;
    }

    /**
     * {@link SpeedController} method for getting whether the motor is inverted
     * @return whether the motor is inverted ({@link #inverted}
     */
    @Override
    public boolean getInverted() {
        return inverted;
    }

    /**
     * {@link SpeedController} method for disabling the motor
     */
    @Override
    public void disable() {
        pidController.disable();
    }

    /**
     * {@link SpeedController} method for turning the motor to
     */
    @Override
    public void stopMotor() {
        set(0);
    }
}
