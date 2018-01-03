package org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.other.Clock;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.SubsystemMP;

/**
 * Runs the command that is currently loaded in the given subsystem.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class RunLoadedProfile<T extends YamlSubsystem & SubsystemMP> extends Command {

    /**
     * The amount of time this command is allowed to run for, in milliseconds.
     */
    private final long timeout;

    /**
     * The subsystem to execute this command on.
     */
    @NotNull
    private final SubsystemMP subsystem;

    /**
     * The time this command started running at.
     */
    private long startTime;

    /**
     * Whether we're running a profile or waiting for the bottom-level buffer to fill.
     */
    private boolean runningProfile;

    /**
     * Default constructor.
     *
     * @param subsystem The subsystem to execute this command on.
     * @param timeout   The max amount of time this subsystem is allowed to run for, in seconds.
     * @param require   Whether or not to require the subsystem this command is running on.
     */
    @JsonCreator
    public RunLoadedProfile(@NotNull @JsonProperty(required = true) T subsystem,
                            @JsonProperty(required = true) double timeout,
                            @JsonProperty(required = true) boolean require) {
        this.subsystem = subsystem;
        //Require if specified.
        if (require) {
            requires(subsystem);
        }

        //Convert to milliseconds.
        this.timeout = (long) (timeout * 1000.);

        runningProfile = false;
    }

    /**
     * Record the start time.
     */
    @Override
    protected void initialize() {
        //Record the start time.
        startTime = Clock.currentTimeMillis();
        Logger.addEvent("RunLoadedProfile init", this.getClass());
        runningProfile = false;
    }

    /**
     * If the subsystem is ready to start running the profile and it's not running yet, start running it.
     */
    @Override
    protected void execute() {
        if (subsystem.readyToRunProfile() && !runningProfile) {
            subsystem.startRunningLoadedProfile();
            runningProfile = true;
        }
    }

    /**
     * Finish when the profile finishes or the timeout is reached.
     *
     * @return true if the profile is finished or the timeout has been exceeded, false otherwise.
     */
    @Override
    protected boolean isFinished() {
        if (Clock.currentTimeMillis() - startTime > timeout) {
            Logger.addEvent("Command timed out", this.getClass());
            System.out.println("RunLoadedProfile timed out!");
            return true;
        }
        return runningProfile && subsystem.profileFinished();
    }

    /**
     * Hold position and log on exit.
     */
    @Override
    protected void end() {
        subsystem.holdPosition();
        Logger.addEvent("RunLoadedProfile end.", this.getClass());
    }

    /**
     * Disable and log if interrupted.
     */
    @Override
    protected void interrupted() {
        subsystem.disable();
        Logger.addEvent("RunLoadedProfile interrupted!", this.getClass());
    }
}
