package org.usfirst.frc.team449.robot.components;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import org.usfirst.frc.team449.robot.RobotMap;

/**
 * Class for velocity controlling {@link PIDMotorController}s. Is identical to {@link PIDMotorController}
 * except {@link #setPIDSourceType()} is overrided to use {@link PIDSourceType}'s kRate to provide velocity control.
 */
public class PIDVelocityController extends PIDMotorController {
    /**
     * Instantiate a new <code>PIDVelocityController</code>
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
    public PIDVelocityController(RobotMap robotMap, double p, double i, double d, double f, double period,
                                 double maxAbsoluteSetpoint, double zeroTolerance, boolean inverted,
                                 boolean useAbsolute, PIDOutput pidOutputDevice, PIDSource source) {
        super(robotMap, p / maxAbsoluteSetpoint, i / maxAbsoluteSetpoint, d / maxAbsoluteSetpoint, f, period,
                maxAbsoluteSetpoint, zeroTolerance, inverted, useAbsolute, pidOutputDevice, source);
    }

    /**
     * Get the correct {@link PIDSourceType} to give to {@link #pidController} for a velocity controller.
     *
     * @return {@link PIDSourceType} <code>kRate</code>
     */
    @Override
    protected PIDSourceType setPIDSourceType() {
        System.out.println("KRATE");
        return PIDSourceType.kRate;
    }

    /**
     * {@link edu.wpi.first.wpilibj.command.Subsystem} method for setting a command that is run when a {@link PIDVelocityController}
     * is constructed. This method is unused.
     */
    @Override
    public void initDefaultCommand() {
    }
}
