package org.usfirst.frc.team449.robot.subsystem.interfaces.position;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * A subsystem controlled based on position, like a turret or elevator.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
public interface SubsystemPosition {

    /**
     * Set the position setpoint
     *
     * @param feet Setpoint in feet from the limit switch used to zero
     */
    void setPositionSetpoint(double feet);

    /**
     * Set a % output setpoint for the motor.
     *
     * @param output The speed for the motor to run at, on [-1, 1]
     */
    void setMotorOutput(double output);

    /**
     * Get the state of the reverse limit switch.
     *
     * @return True if the reverse limit switch is triggered, false otherwise.
     */
    boolean getReverseLimit();

    /**
     * Get the state of the forwards limit switch.
     *
     * @return True if the forwards limit switch is triggered, false otherwise.
     */
    boolean getForwardLimit();

    /**
     * Set the position to 0.
     */
    void resetPosition();

    /**
     * Check if the mechanism has reached the setpoint.
     *
     * @return True if the setpoint has been reached, false otherwise.
     */
    boolean onTarget();

    /**
     * Enable the motors of this subsystem.
     */
    void enableMotor();

    /**
     * Disable the motors of this subsystem.
     */
    void disableMotor();
}
