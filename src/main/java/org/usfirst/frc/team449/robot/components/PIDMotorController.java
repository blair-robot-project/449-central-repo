package org.usfirst.frc.team449.robot.components;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SpeedController;
import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.RobotMap;

/**
 * Abstract class for PID controlled <code>SpeedController</code>s (motors).
 * <p>
 * <code>PIDMotorController</code> objects contain a <code>PIDController</code> object that handles PID
 * calculations. The <code>PIDController</code> object runs in its own thread in the scheduler, independent of other
 * commands.
 * </p>
 * <p>
 * <code>PIDMotorController</code>s can be used with both absolute and relative setpoints.
 * </p>
 * <p>
 * <code>PIDMotorController</code>s contain abstract methods to be overloaded to set the PID loop type
 * ({@link #setPIDSourceType()}) and to write directly to the motor object ({@link #motorWrite(double)}).
 * </p>
 */
public abstract class PIDMotorController extends MappedSubsystem {
    /**
     * <code>PIDController</code> that calculates pidWrite values
     */
    private PIDController pidController;
    /**
     * The pidOutputDevice device (a motor)
     */
    private PIDOutput pidOutputDevice;
    /**
     * The input device (an encoder)
     */
    private PIDSource pidSourceDevice;
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
     * @param robotMap            robot configuration map
     * @param p                   {@link #pidController}'s proportional term
     * @param i                   {@link #pidController}'s integral term
     * @param d                   {@link #pidController}'s derivative term
     * @param f                   {@link #pidController}'s feedforward term
     * @param period              {@link #pidController}'s thread period
     * @param maxAbsoluteSetpoint {@link #pidController}'s input range
     * @param zeroTolerance       built-in zero tolerance ({@link #zeroTolerance})
     * @param inverted            whether the motor is inverted
     * @param useAbsolute         whether {@link #set(double)} should use interpret its input as absolute (instead of relative)
     * @param pidOutputDevice     the motor controller's pidOutputDevice device (a motor)
     * @param source              the motor controller's input device (an encoder)
     */
    public PIDMotorController(RobotMap robotMap, double p, double i, double d, double f, double period, double maxAbsoluteSetpoint,
                              double zeroTolerance, boolean inverted, boolean useAbsolute, PIDOutput pidOutputDevice,
                              PIDSource source) {
        super(robotMap);
        this.maxAbsoluteSetpoint = maxAbsoluteSetpoint;
        this.zeroTolerance = zeroTolerance;
        this.inverted = inverted;
        this.pidOutputDevice = pidOutputDevice;
        this.pidSourceDevice = source;
        this.pidSourceDevice.setPIDSourceType(setPIDSourceType());
        this.useAbsolute = useAbsolute;
        ((SpeedController) pidOutputDevice).disable();
        pidController = new PIDController(p, i, d, f, this.pidSourceDevice, this.pidOutputDevice, period);
        pidController.setOutputRange(-maxAbsoluteSetpoint, maxAbsoluteSetpoint);
        pidController.disable();
        pidController.enable();
        pidController.setSetpoint(0);
        System.out.println("Source Type:" + pidSourceDevice.getPIDSourceType());
    }

    /**
     * This method is run in the constructor to set the {@link #pidSourceDevice}'s {@link PIDSourceType}. This method should be
     * overrided in velocity and displacement controllers with their respective values.
     *
     * @return motor controller's {@link PIDSourceType}
     */
    protected abstract PIDSourceType setPIDSourceType();

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
    public double returnPIDInput() {
        return pidSourceDevice.pidGet();
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

        System.out.println("Set Relative Setpoint: " + String.valueOf(setpoint * maxAbsoluteSetpoint));
    }

    /**
     * Set the PID setpoint, either using an absolute setpoint or a relative setpoint, depending on the value of
     * {@link #useAbsolute}.
     *
     * @param setpoint absolute or relative setpoint
     */
    public void set(double setpoint) {
        if (useAbsolute) {
            setAbsoluteSetpoint(setpoint);
        } else {
            setRelativeSetpoint(setpoint);
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

//    /**
//     * Run by the {@link #pidController} to write to the pidOutputDevice device (motor). Do not call this method in outside of
//     * {@link #pidController}! Checks if <code>pidOutputDevice</code> is within zero tolerance: if so, writes zero to the pidOutputDevice
//     * device, if not, writes <code>pidOutputDevice</code> to the pidOutputDevice device.
//     *
//     * @param output pidOutputDevice to write to the pidOutputDevice device
//     */
//    public void usePIDOutput(double output) {
//        if (getAbsoluteSetpoint() == 0 && Math.abs(output) < zeroTolerance) {
//            output = 0;
//        }
//
//        motorWrite(output);
//        SmartDashboard.putNumber("pidOutputDevice: ", output);
//        System.out.println("use pid output");
//    }

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
     * Set whether the motor is inverted
     *
     * @param isInverted whether the motor is inverted ({@link #inverted}
     */
    public void setInverted(boolean isInverted) {
        inverted = isInverted;
    }

    /**
     * Get whether the motor is inverted
     *
     * @return whether the motor is inverted ({@link #inverted}
     */
    public boolean getInverted() {
        return inverted;
    }

    /**
     * Turn the motor controller off
     */
    public void reset() {
        setAbsoluteSetpoint(0);
        pidController.reset();
        pidController.enable();
        System.out.println("reset");
    }

    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(new ZeroPIDMotorController(this));
    }
}
