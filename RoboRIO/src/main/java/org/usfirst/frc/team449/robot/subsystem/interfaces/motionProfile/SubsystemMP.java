package org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.other.MotionProfileData;

/**
 * A subsystem that can have motion profiles run on it.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface SubsystemMP {

    /**
     * Loads a profile into the MP buffer.
     *
     * @param profile The profile to be loaded.
     */
    void loadMotionProfile(@NotNull MotionProfileData profile);

    /**
     * Start running the profile that's currently loaded into the MP buffer.
     */
    void startRunningLoadedProfile();

    /**
     * Get whether this subsystem has finished running the profile loaded in it.
     *
     * @return true if there's no profile loaded and no profile running, false otherwise.
     */
    boolean profileFinished();

    /**
     * Disable the motors.
     */
    void disable();

    /**
     * Hold the current position.
     */
    void holdPosition();

    /**
     * Get whether the subsystem is ready to run the loaded profile.
     *
     * @return true if a profile is loaded and ready to run, false otherwise.
     */
    boolean readyToRunProfile();

    /**
     * Stops any MP-related threads currently running. Normally called at the start of teleop.
     */
    void stopMPProcesses();
}
