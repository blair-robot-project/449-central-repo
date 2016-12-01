package org.usfirst.frc.team449.robot.components;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * A cluster of motors, or SpeedControllers, used for tank drive generally.
 */
public class MotorCluster extends Component implements SpeedController {
    private final SpeedController[] controllerList;
    private double outputRange;
    private boolean inverted;
    private double lastSet = 0;

    /**
     * construct a MotorCluster to hold a total of <code>total</code>
     * SpeedControllers
     *
     * @param total the number of SpeedControllers to hold in this glorified array
     */
    public MotorCluster(int total, double outputRange) {
        controllerList = new SpeedController[total];
        lastSet = 0;
        this.outputRange = outputRange;
    }

    /**
     * construct a MotorCluster to control the given SpeedControllers
     *
     * @param controllers the SpeedControllers to control
     */
    public MotorCluster(SpeedController[] controllers, double outputRange) {
        controllerList = controllers;
        lastSet = 0;
        this.outputRange = outputRange;
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

    /**
     * Write to the motors
     *
     * @param output value to write to the motors
     */
    @Override
    public void pidWrite(double output) {
        output /= outputRange;
        for (SpeedController aControllerList : controllerList) {
            SmartDashboard.putNumber("MotorController PID Write Velocity", output);
            aControllerList.set(output);
        }
        lastSet = output;
    }

    /**
     * {@link SpeedController} method for getting the last value written to the motors
     *
     * @return last value written to the motors
     */
    @Override
    public double get() {
        return lastSet;
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
        System.out.println("Warning, you are using a deprecated method void set(double, double). You will use method " +
                "void set(double) instead.");
        set(velocity);
    }

    /**
     * Write a velocity to the motors
     * @param velocity velocity to write to the motors
     */
    @Override
    public void set(double velocity) {
//        velocity /= outputRange;
//        for (int i = 0; i < controllerList.length; i++) {
//            SmartDashboard.putNumber("MotorController Set Velocity", velocity * 130);
//            controllerList[i].set(velocity);
//            SmartDashboard.putBoolean("Motor Inverted", controllerList[i].getInverted());
//        }
////        lastSet = velocity;
//        SmartDashboard.putNumber("MotorCluster Set: ", lastSet);
//        SmartDashboard.putBoolean("MotorCluster Inverted", inverted);
    }

    /**
     * {@link SpeedController} method for setting whether the motor cluster is inverted. This method checks if the set value
     * is the same as the current value before setting each individual motor for efficiency sake since there are
     * multiple motors.
     *
     * @param isInverted whether the motor cluster is inverted ({@link #inverted}
     */
    @Override
    public void setInverted(boolean isInverted) {
        boolean changed = isInverted != inverted;
        if (!changed) {
            return;
        }
        this.inverted = isInverted;
        for (SpeedController aControllerList : controllerList) {
            aControllerList.setInverted(!aControllerList.getInverted());
        }
    }

    /**
     * {@link SpeedController} method for getting whether the motor cluster is inverted
     *
     * @return whether the motor cluster is inverted ({@link #inverted}
     */
    @Override
    public boolean getInverted() {
        return inverted;
    }

    /**
     * {@link SpeedController} method for disabling the motor cluster
     */
    @Override
    public void disable() {
        for (SpeedController aControllerList : controllerList) {
            aControllerList.disable();
        }
    }

    /**
     * {@link SpeedController} method for stopping motor movement. Motor can be moved again by calling set without having
     * to re-enable the motor.
     */
    @Override
    public void stopMotor() {
        for (SpeedController aControllerList : controllerList) {
            aControllerList.stopMotor();
        }
    }
}
