package org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.TwoSideMPSubsystem;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.other.MotionProfileData;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.SubsystemMP;

/**
 * An MP subsystem with two sides that therefore needs two profiles at a time.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface SubsystemMPTwoSides extends SubsystemMP {

    /**
     * Loads given profiles into the left and right sides of the drive.
     *
     * @param left  The profile to load into the left side.
     * @param right The profile to load into the right side.
     */
    void loadMotionProfile(@NotNull MotionProfileData left, @NotNull MotionProfileData right);
}
