package org.usfirst.frc.team449.robot.subsystem.interfaces.position.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.position.SubsystemPosition;

/**
 * Go to a given position
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class GoToPosition extends Command {

    /**
     * The subsystem to execute this command on.
     */
    @NotNull
    private final SubsystemPosition subsystem;

    /**
     * The position to go to, in feet.
     */
    private double setpoint;

    /**
     * Default constructor
     *
     * @param subsystem The subsystem to execute this command on.
     * @param setpoint  The position to go to, in feet.
     */
    @JsonCreator
    public GoToPosition(@NotNull @JsonProperty(required = true) SubsystemPosition subsystem,
                        @JsonProperty(required = true) double setpoint) {
        this.subsystem = subsystem;
        this.setpoint = setpoint;
    }

    /**
     * Log when this command is initialized
     */
    @Override
    protected void initialize() {
        Logger.addEvent("GoToPosition init.", this.getClass());
    }

    /**
     * Sets position.
     */
    @Override
    protected void execute() {
        subsystem.setPositionSetpoint(setpoint);
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
        Logger.addEvent("GoToPosition ends.", this.getClass());
    }

    /**
     * Log when this command is interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("GoToPosition interrupted!", this.getClass());
    }
}
