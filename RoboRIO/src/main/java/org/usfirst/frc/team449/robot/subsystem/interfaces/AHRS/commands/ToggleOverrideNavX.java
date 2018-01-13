package org.usfirst.frc.team449.robot.subsystem.interfaces.AHRS.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.AHRS.SubsystemAHRS;

/**
 * Toggle whether or not to override the AHRS.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ToggleOverrideNavX extends Command {

    /**
     * The subsystem to execute this command on.
     */
    @NotNull
    private final SubsystemAHRS subsystem;

    /**
     * Default constructor.
     *
     * @param subsystem The subsystem to execute this command on
     */
    @JsonCreator
    public ToggleOverrideNavX(@NotNull @JsonProperty(required = true) SubsystemAHRS subsystem) {
        this.subsystem = subsystem;
    }

    /**
     * Log when this command is initialized
     */
    @Override
    protected void initialize() {
        Logger.addEvent("OverrideNavX init", this.getClass());
    }

    /**
     * Toggle whether or not we're overriding the AHRS
     */
    @Override
    protected void execute() {
        subsystem.setOverrideGyro(!subsystem.getOverrideGyro());
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
        Logger.addEvent("OverrideNavX end", this.getClass());
    }

    /**
     * Log when this command is interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("OverrideNavX Interrupted!", this.getClass());
    }
}

