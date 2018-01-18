package org.usfirst.frc.team449.robot.subsystem.interfaces.binaryMotor;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * A subsystem with a motor that only needs to be run at one speed, e.g. a flywheel multiSubsystem or simple intake.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface SubsystemBinaryMotor {

    /**
     * Turns the motor on, and sets it to a map-specified speed.
     */
    void turnMotorOn();

    /**
     * Turns the motor off.
     */
    void turnMotorOff();

    /**
     * @return true if the motor is on, false otherwise.
     */
    boolean isMotorOn();
}
