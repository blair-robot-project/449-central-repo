package org.usfirst.frc.team449.robot.drive.tank.components;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.SpeedController;
import org.usfirst.frc.team449.robot.components.PIDVelocityController;

/**
 * A PID velocity controlled motor cluster that writes to a group of motors (a motor cluster driving a gearbox). Extends
 * {@link PIDVelocityController}.
 */
public class PIDVelocityMotorCluster extends PIDVelocityController {
    /**
     * Array of {@link SpeedController}s (motors) in the motor cluster
     */
    private final SpeedController[] controllerList;
    /**
     * The encoder used as the {@link PIDSource}
     */
    private Encoder encoder;

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
     * @param numMotors           the number of motors in the motor cluster
     */

    public PIDVelocityMotorCluster(double p, double i, double d, double f, double period, double maxAbsoluteSetpoint,
                                   double zeroTolerance, boolean inverted, boolean useAbsolute, PIDOutput output,
                                   Encoder encoder, int numMotors) {
        super(p, i, d, f, period, maxAbsoluteSetpoint, zeroTolerance, inverted, useAbsolute, output, encoder);
        this.controllerList = new SpeedController[numMotors];
        this.encoder = encoder;
    }

    /**
     * add a motor that will be considered a part of the cluster
     *
     * @param controller the motorController
     */
    public void addSlave(SpeedController controller) {
        for (int i = 0; i < controllerList.length; i++) {
            if (controllerList[i] == null) {
                controllerList[i] = controller;
                return;
            }
        }
        System.err.println("Motor cluster over capacity, not adding a new motor! (" + controllerList.length + ")");
    }

    @Override
    public double returnPIDInput() {
        return encoder.pidGet();
    }

    @Override
    protected void motorWrite(double output) {
        for (int i = 0; i < this.controllerList.length; i++) {
            controllerList[i].pidWrite(output);
        }
    }

    @Override
    public void pidWrite(double output) {
        for (int i = 0; i < this.controllerList.length; i++) {
            controllerList[i].pidWrite(output);
        }
    }
}
