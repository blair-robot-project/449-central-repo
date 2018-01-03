package org.usfirst.frc.team449.robot.drive.shifting.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.drive.shifting.DriveShiftable;
import org.usfirst.frc.team449.robot.other.Logger;

/**
 * Override or unoverride whether we're autoshifting. Used to stay in low gear for pushing matches and more!
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class OverrideAutoShift extends Command {

    /**
     * Whether or not to override.
     */
    private final boolean override;

    /**
     * The drive subsystem to execute this command on.
     */
    @NotNull
    private final DriveShiftable subsystem;

    /**
     * Default constructor
     *
     * @param drive    The drive subsystem to execute this command on.
     * @param override Whether or not to override autoshifting.
     */
    @JsonCreator
    public OverrideAutoShift(@NotNull @JsonProperty(required = true) DriveShiftable drive,
                             @JsonProperty(required = true) boolean override) {
        subsystem = drive;
        this.override = override;
    }

    /**
     * Log on initialization
     */
    @Override
    protected void initialize() {
        Logger.addEvent("OverrideAutoShift init", this.getClass());
    }

    /**
     * Override autoshifting.
     */
    @Override
    protected void execute() {
        //Set whether or not we're overriding
        subsystem.setOverrideAutoshift(override);
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
     * Log when this command ends.
     */
    @Override
    protected void end() {
        Logger.addEvent("OverrideAutoShift end", this.getClass());
    }

    /**
     * Log when interrupted
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("OverrideAutoShift Interrupted!", this.getClass());
    }
}

