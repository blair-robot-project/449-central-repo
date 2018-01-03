package org.usfirst.frc.team449.robot.drive.unidirectional.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectional;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.other.Clock;
import org.usfirst.frc.team449.robot.other.Logger;

/**
 * Go at a certain velocity for a set number of seconds
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class DriveAtSpeed<T extends YamlSubsystem & DriveUnidirectional> extends Command {

    /**
     * Speed to go at
     */
    private final double velocity;

    /**
     * How long to run for
     */
    private final double seconds;

    /**
     * The drive subsystem to execute this command on.
     */
    @NotNull
    private final T subsystem;

    /**
     * When this command was initialized.
     */
    private long startTime;

    /**
     * Default constructor
     *
     * @param subsystem The drive to execute this command on
     * @param velocity  How fast to go, in RPS
     * @param seconds   How long to drive for.
     */
    @JsonCreator
    public DriveAtSpeed(@NotNull @JsonProperty(required = true) T subsystem,
                        @JsonProperty(required = true) double velocity,
                        @JsonProperty(required = true) double seconds) {
        //Initialize stuff
        this.subsystem = subsystem;
        this.velocity = velocity;
        this.seconds = seconds;
        requires(subsystem);
        Logger.addEvent("Drive Robot bueno", this.getClass());
    }

    /**
     * Set up start time.
     */
    @Override
    protected void initialize() {
        //Set up start time
        startTime = Clock.currentTimeMillis();
        //Reset drive velocity (for safety reasons)
        subsystem.fullStop();
        Logger.addEvent("DriveAtSpeed init", this.getClass());
    }

    /**
     * Send output to motors and log data
     */
    @Override
    protected void execute() {
        //Set the velocity
        subsystem.setOutput(velocity, velocity);
    }

    /**
     * Exit after the command's been running for long enough
     *
     * @return True if timeout has been reached, false otherwise
     */
    @Override
    protected boolean isFinished() {
        return (Clock.currentTimeMillis() - startTime) * 1e-3 > seconds;
    }

    /**
     * Stop the drive when the command ends.
     */
    @Override
    protected void end() {
        //Brake on exit. Yes this should be setOutput because often we'll be testing how well the PID loop handles a full stop.
        subsystem.setOutput(0, 0);
        Logger.addEvent("DriveAtSpeed end.", this.getClass());
    }

    /**
     * Log and stop the drive when the command is interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("DriveAtSpeed Interrupted! Stopping the robot.", this.getClass());
        //Brake if we're interrupted
        subsystem.fullStop();
    }
}
