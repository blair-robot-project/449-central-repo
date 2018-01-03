package org.usfirst.frc.team449.robot.generalInterfaces.shiftable.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable;
import org.usfirst.frc.team449.robot.other.Logger;

/**
 * A command that switches to low gear.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SwitchToLowGear extends Command {

    /**
     * The drive subsystem to execute this command on.
     */
    @NotNull
    private final Shiftable subsystem;

    /**
     * Default constructor
     *
     * @param subsystem The drive subsystem to execute this command on
     */
    @JsonCreator
    public SwitchToLowGear(@NotNull @JsonProperty(required = true) Shiftable subsystem) {
        this.subsystem = subsystem;
    }

    /**
     * Log when this command is initialized
     */
    @Override
    protected void initialize() {
        Logger.addEvent("SwitchToLowGear init.", this.getClass());
    }

    /**
     * Switch to low gear
     */
    @Override
    protected void execute() {
        subsystem.setGear(Shiftable.gear.LOW.getNumVal());
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
        Logger.addEvent("SwitchToLowGear end.", this.getClass());
    }

    /**
     * Log when this command is interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("SwitchToLowGear Interrupted!", this.getClass());
    }
}