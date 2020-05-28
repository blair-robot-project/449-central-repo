package org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.FPSTalon;
import org.usfirst.frc.team449.robot.other.MotionProfileData;

/**
 * A simple subsystem that uses a Talon for motion profiling.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SubsystemMPSimple implements Subsystem, SubsystemMP {

    /**
     * The motor this subsystem controls.
     */
    private final FPSTalon motor;

    /**
     * Default constructor.
     *
     * @param motor The motor this subsystem controls.
     */
    @JsonCreator
    public SubsystemMPSimple(@NotNull @JsonProperty(required = true) FPSTalon motor) {
        this.motor = motor;
    }

    /**
     * Loads a profile into the MP buffer.
     *
     * @param profile The profile to be loaded.
     */
    @Override
    public void loadMotionProfile(@NotNull MotionProfileData profile) {
        motor.loadProfile(profile);
    }

    /**
     * Start running the profile that's currently loaded into the MP buffer.
     */
    @Override
    public void startRunningLoadedProfile() {
        motor.startRunningMP();
    }

    /**
     * Get whether this subsystem has finished running the profile loaded in it.
     *
     * @return true if there's no profile loaded and no profile running, false otherwise.
     */
    @Override
    public boolean profileFinished() {
        return motor.MPIsFinished();
    }

    /**
     * Disable the motors.
     */
    @Override
    public void disable() {
        motor.disable();
    }

    /**
     * Hold the current position.
     */
    @Override
    public void holdPosition() {
        motor.holdPositionMP();
    }

    /**
     * Get whether the subsystem is ready to run the loaded profile.
     *
     * @return true if a profile is loaded and ready to run, false otherwise.
     */
    @Override
    public boolean readyToRunProfile() {
        return motor.readyForMP();
    }
}
