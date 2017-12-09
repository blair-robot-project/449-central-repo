package org.usfirst.frc.team449.robot.generalInterfaces.simpleMotor;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface SimpleMotor {

    /**
     * Set the velocity for the motor to go at.
     *
     * @param velocity the desired velocity, on [-1, 1].
     */
    void setVelocity(double velocity);

    /**
     * Enables the motor, if applicable.
     */
    void enable();

    /**
     * Disables the motor, if applicable.
     */
    void disable();
}
