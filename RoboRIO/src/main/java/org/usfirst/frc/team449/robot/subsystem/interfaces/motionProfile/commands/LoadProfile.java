package org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.other.MotionProfileData;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.SubsystemMP;

/**
 * Loads the given profile into the subsystem, but doesn't run it.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class LoadProfile extends Command {

    /**
     * The subsystem to execute this command on.
     */
    @NotNull
    private final SubsystemMP subsystem;

    /**
     * The profile to execute.
     */
    @NotNull
    private final MotionProfileData profile;

    /**
     * Default constructor
     *
     * @param subsystem The subsystem to execute this command on.
     * @param profile   The profile to run.
     */
    @JsonCreator
    public LoadProfile(@NotNull @JsonProperty(required = true) SubsystemMP subsystem,
                       @NotNull @JsonProperty(required = true) MotionProfileData profile) {
        this.subsystem = subsystem;
        this.profile = profile;
    }

    /**
     * Log when this command is initialized
     */
    @Override
    protected void initialize() {
        Logger.addEvent("LoadProfile init.", this.getClass());
    }

    /**
     * Load the profile.
     */
    @Override
    protected void execute() {
        subsystem.loadMotionProfile(profile);
    }

    /**
     * Finish immediately because this is a state-change command.
     *
     * @return true
     */
    @Override
    protected boolean isFinished() {
        return true;
    }

    /**
     * Log when this command ends
     */
    @Override
    protected void end() {
        Logger.addEvent("LoadProfile end.", this.getClass());
    }

    /**
     * Log when this command is interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("LoadProfile Interrupted!", this.getClass());
    }
}