package org.usfirst.frc.team449.robot.drive;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Any locomotion device for the robot.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface DriveSubsystem {

    /**
     * Completely stop the robot by setting the voltage to each side to be 0.
     */
    void fullStop();

    /**
     * If this drive uses motors that can be disabled, enable them.
     */
    void enableMotors();

    /**
     * Reset the position of the drive if it has encoders.
     */
    void resetPosition();
}
