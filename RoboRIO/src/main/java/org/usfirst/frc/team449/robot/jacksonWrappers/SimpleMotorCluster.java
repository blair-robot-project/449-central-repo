package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.simpleMotor.SimpleMotor;

import java.util.List;

/**
 * A cluster of simple motors that act as a single simple motor. Don't use this for talons, use master-slave instead.
 */
public class SimpleMotorCluster implements SimpleMotor {

    /**
     * The motors in this cluster. Contains at least 1 element.
     */
    @NotNull
    private List<SimpleMotor> motors;

    /**
     * Default constructor
     *
     * @param motors The motors in this cluster. Must have at least 1 element.
     */
    @JsonCreator
    public SimpleMotorCluster(@JsonProperty(required = true) @NotNull List<SimpleMotor> motors) {
        if (motors.size() == 0) {
            throw new IllegalArgumentException("motors must have at least 1 element!");
        }
        this.motors = motors;
    }

    /**
     * Set the velocity for the motor to go at.
     *
     * @param velocity the desired velocity, on [-1, 1].
     */
    @Override
    public void setVelocity(double velocity) {
        for (SimpleMotor motor : motors) {
            motor.setVelocity(velocity);
        }
    }

    /**
     * Enables the motor, if applicable.
     */
    @Override
    public void enable() {
        for (SimpleMotor motor : motors) {
            motor.enable();
        }
    }

    /**
     * Disables the motor, if applicable.
     */
    @Override
    public void disable() {
        for (SimpleMotor motor : motors) {
            motor.disable();
        }
    }
}
