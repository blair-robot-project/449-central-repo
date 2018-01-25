package org.usfirst.frc.team449.robot.drive.unidirectional;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.drive.DriveSubsystem;
import org.usfirst.frc.team449.robot.generalInterfaces.updatable.Updatable;

/**
 * A drive with a left side and a right side. "Unidirectional" because it can only move forwards or backwards, not
 * sideways.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface DriveUnidirectional extends DriveSubsystem, Updatable {

    /**
     * Set the output of each side of the drive.
     *
     * @param left  The output for the left side of the drive, from [-1, 1]
     * @param right the output for the right side of the drive, from [-1, 1]
     */
    void setOutput(double left, double right);

    /**
     * Get the velocity of the left side of the drive.
     *
     * @return The signed velocity in feet per second, or null if the drive doesn't have encoders.
     */
    @Nullable
    Double getLeftVel();

    /**
     * Get the velocity of the right side of the drive.
     *
     * @return The signed velocity in feet per second, or null if the drive doesn't have encoders.
     */
    @Nullable
    Double getRightVel();

    /**
     * Get the position of the left side of the drive.
     *
     * @return The signed position in feet, or null if the drive doesn't have encoders.
     */
    @Nullable
    Double getLeftPos();

    /**
     * Get the position of the right side of the drive.
     *
     * @return The signed position in feet, or null if the drive doesn't have encoders.
     */
    @Nullable
    Double getRightPos();

    /**
     * Get the cached velocity of the left side of the drive.
     *
     * @return The signed velocity in feet per second, or null if the drive doesn't have encoders.
     */
    @Nullable
    Double getLeftVelCached();

    /**
     * Get the cached velocity of the right side of the drive.
     *
     * @return The signed velocity in feet per second, or null if the drive doesn't have encoders.
     */
    @Nullable
    Double getRightVelCached();

    /**
     * Get the cached position of the left side of the drive.
     *
     * @return The signed position in feet, or null if the drive doesn't have encoders.
     */
    @Nullable
    Double getLeftPosCached();

    /**
     * Get the cached position of the right side of the drive.
     *
     * @return The signed position in feet, or null if the drive doesn't have encoders.
     */
    @Nullable
    Double getRightPosCached();
}
