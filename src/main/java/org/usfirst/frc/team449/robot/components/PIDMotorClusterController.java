package org.usfirst.frc.team449.robot.components;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SpeedController;

public abstract class PIDMotorClusterController extends PIDSpeedController {
    /**
     * Motor cluster to write to
     */
    private MotorCluster motorCluster;

    /**
     * Instantiate a new <code>PIDMotorClusterController</code>
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
    public PIDMotorClusterController(double p, double i, double d, double f, double period, double maxAbsoluteSetpoint,
                                     boolean inverted, boolean useAbsolute, PIDSourceType pidSourceType) {
        super(p, i, d, f, period, maxAbsoluteSetpoint, inverted, useAbsolute, pidSourceType);
    }

    /**
     * Construct {@link #pidOutputDevice} (used in the super constructor)
     *
     * @return the {@link MotorCluster} to use as {@link #pidOutputDevice}
     */
    @Override
    public PIDOutput constructPIDOutputDevice() {
        motorCluster = new MotorCluster(getNumMotors());
        populateMotorCluster();
        System.out.println(motorCluster);
        return motorCluster;
    }

    /**
     * Abstract method intended to be overrided in annonymous inner class to get/set the number of motors in the
     * {@link MotorCluster}
     *
     * @return number of motors in the {@link MotorCluster}
     */
    public abstract int getNumMotors();

    /**
     * Abstract method intended to be overrided in anonymous inner class to construct and add the motors to the
     * {@link MotorCluster}
     */
    public abstract void populateMotorCluster();

    /**
     * Method for adding slaves to the {@link MotorCluster}
     *
     * @param motor motor to add as a slave device
     */
    public void addMotorClusterSlave(SpeedController motor) {
        motorCluster.addSlave(motor);
    }

    /**
     * Method for getting the value written to the motor cluster
     */
    @Override
    public double getPIDOutput() {
        return motorCluster.get();
    }
}
