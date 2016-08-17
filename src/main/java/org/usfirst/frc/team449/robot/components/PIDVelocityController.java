package org.usfirst.frc.team449.robot.components;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

/**
 * Abstract class for velocity controlling {@link PIDMotorController}s. Is identical to {@link PIDMotorController}
 * except {@link #setPIDSourceType()} is overrided to use {@link PIDSourceType.kRate} to provide velocity control.
 */
public abstract class PIDVelocityController extends PIDMotorController {
    /**
     * Instantiate a new <code>PIDVelocityController</code>
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
    public PIDVelocityController(double p, double i, double d, double f, double period, double maxAbsoluteSetpoint,
                                 double zeroTolerance, boolean inverted, boolean useAbsolute, PIDOutput output,
                                 PIDSource source) {
        super(p, i, d, f, period, maxAbsoluteSetpoint, zeroTolerance, inverted, useAbsolute, output, source);
    }

    /**
     * Get the correct {@link PIDSourceType} to give to {@link #pidController} for a velocity controller.
     *
     * @return {@link PIDSourceType.kRate}
     */
    @Override
    protected PIDSourceType setPIDSourceType() {
        return PIDSourceType.kRate;
    }
}
