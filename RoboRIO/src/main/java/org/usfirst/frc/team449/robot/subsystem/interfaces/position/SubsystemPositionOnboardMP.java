package org.usfirst.frc.team449.robot.subsystem.interfaces.position;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.components.PathGenerator;
import org.usfirst.frc.team449.robot.generalInterfaces.updatable.Updatable;
import org.usfirst.frc.team449.robot.jacksonWrappers.FPSTalon;
import org.usfirst.frc.team449.robot.other.MotionProfileData;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.SubsystemMP;

/**
 * A SubsystemPosition that moves using motion profiles.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SubsystemPositionOnboardMP implements Subsystem, SubsystemPosition, Updatable, SubsystemMP {

    /**
     * The Talon SRX this subsystem controls.
     */
    protected final FPSTalon talon;

    /**
     * The object for generating the paths for the Talon to run.
     */
    private final PathGenerator pathGenerator;
    /**
     * Whether or not to start running the profile loaded into the Talon.
     */
    protected boolean shouldStartProfile;
    /**
     * The previously observed Talon velocity. Used for calculating acceleration.
     */
    private double lastVel;
    /**
     * The acceleration of the Talon.
     */
    private double accel;

    /**
     * Default constructor.
     *
     * @param talon         The Talon SRX this subsystem controls.
     * @param pathGenerator The object for generating the paths for the Talon to run.
     */
    @JsonCreator
    public SubsystemPositionOnboardMP(@NotNull @JsonProperty(required = true) FPSTalon talon,
                                      @NotNull @JsonProperty(required = true) PathGenerator pathGenerator) {
        this.talon = talon;
        this.pathGenerator = pathGenerator;
        shouldStartProfile = false;
    }

    /**
     * Set the position setpoint
     *
     * @param feet Setpoint in feet from the limit switch used to zero
     */
    @Override
    public void setPositionSetpoint(double feet) {
        disableMotor();
        loadMotionProfile(pathGenerator.generateProfile(talon.getPositionFeet(), talon.getVelocity(), accel, feet));
        shouldStartProfile = true;
    }

    /**
     * Set a % output setpoint for the motor.
     *
     * @param output The speed for the motor to run at, on [-1, 1]
     */
    @Override
    public void setMotorOutput(double output) {
        talon.setVelocity(output);
    }

    /**
     * Get the state of the reverse limit switch.
     *
     * @return True if the reverse limit switch is triggered, false otherwise.
     */
    @Override
    public boolean getReverseLimit() {
        return talon.getRevLimitSwitch();
    }

    /**
     * Get the state of the forwards limit switch.
     *
     * @return True if the forwards limit switch is triggered, false otherwise.
     */
    @Override
    public boolean getForwardLimit() {
        return talon.getFwdLimitSwitch();
    }

    /**
     * Set the position to 0.
     */
    @Override
    public void resetPosition() {
        talon.resetPosition();
    }

    /**
     * Check if the mechanism has reached the setpoint.
     *
     * @return True if the setpoint has been reached, false otherwise.
     */
    @Override
    public boolean onTarget() {
        //Don't stop before we start the profile
        if (profileFinished() && !shouldStartProfile) {
            talon.holdPositionMP();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Enable the motors of this subsystem.
     */
    @Override
    public void enableMotor() {
        talon.enable();
    }

    /**
     * Disable the motors of this subsystem.
     */
    @Override
    public void disableMotor() {
        talon.disable();
    }

    /**
     * Updates all cached values with current ones.
     */
    @Override
    public void update() {
        //Update acceleration
        accel = talon.getVelocity() - lastVel;
        //Do clever math to get the talon velocity back out
        lastVel = accel + lastVel;
    }

    /**
     * When the run method of the scheduler is called this method will be called.
     * <p>
     * Starts running the Talon profile if it's ready.
     */
    @Override
    public void periodic() {
        //Start the profile if it's ready
        if (shouldStartProfile && readyToRunProfile()) {
            startRunningLoadedProfile();
            shouldStartProfile = false;
        }
    }

    /**
     * Loads a profile into the MP buffer.
     *
     * @param profile The profile to be loaded.
     */
    @Override
    public void loadMotionProfile(@NotNull MotionProfileData profile) {
        talon.loadProfile(profile);
    }

    /**
     * Start running the profile that's currently loaded into the MP buffer.
     */
    @Override
    public void startRunningLoadedProfile() {
        talon.startRunningMP();
    }

    /**
     * Get whether this subsystem has finished running the profile loaded in it.
     *
     * @return true if there's no profile loaded and no profile running, false otherwise.
     */
    @Override
    public boolean profileFinished() {
        return talon.MPIsFinished();
    }

    /**
     * Disable the motors.
     */
    @Override
    public void disable() {
        disableMotor();
    }

    /**
     * Hold the current position.
     */
    @Override
    public void holdPosition() {
        talon.holdPositionMP();
    }

    /**
     * Get whether the subsystem is ready to run the loaded profile.
     *
     * @return true if a profile is loaded and ready to run, false otherwise.
     */
    @Override
    public boolean readyToRunProfile() {
        return talon.readyForMP();
    }
}
