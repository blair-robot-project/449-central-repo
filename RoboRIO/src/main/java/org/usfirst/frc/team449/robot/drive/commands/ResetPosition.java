package org.usfirst.frc.team449.robot.drive.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.InstantCommand;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.drive.DriveSubsystem;
import org.usfirst.frc.team449.robot.other.Logger;

/**
 * Resets the positions of the motors of the given drive subsystem.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ResetPosition extends InstantCommand {

    /**
     * The subsystem to execute this command on.
     */
    @NotNull
    private final DriveSubsystem subsystem;

    /**
     * Default constructor
     *
     * @param subsystem The subsystem to execute this command on.
     */
    @JsonCreator
    public ResetPosition(@NotNull @JsonProperty(required = true) DriveSubsystem subsystem) {
        this.subsystem = subsystem;
    }

    /**
     * Log when this command is initialized
     */
    @Override
    protected void initialize() {
        Logger.addEvent("EnableMotors init.", this.getClass());
    }

    /**
     * Do the state change.
     */
    @Override
    protected void execute() {
        subsystem.resetPosition();
    }

    /**
     * Log when this command ends
     */
    @Override
    protected void end() {
        Logger.addEvent("EnableMotors end.", this.getClass());
    }

    /**
     * Log when this command is interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("EnableMotors Interrupted!", this.getClass());
    }
}