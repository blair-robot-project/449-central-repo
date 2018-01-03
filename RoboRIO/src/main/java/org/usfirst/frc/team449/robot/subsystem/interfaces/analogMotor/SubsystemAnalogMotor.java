package org.usfirst.frc.team449.robot.subsystem.interfaces.analogMotor;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * An interface for a motor that has an infinite number of modes, whether those modes are velocities, voltages, or
 * positions.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface SubsystemAnalogMotor {

    /**
     * Set output to a given input.
     *
     * @param input The input to give to the motor.
     */
    void set(double input);

    /**
     * Disable the motor.
     */
    void disable();
}
