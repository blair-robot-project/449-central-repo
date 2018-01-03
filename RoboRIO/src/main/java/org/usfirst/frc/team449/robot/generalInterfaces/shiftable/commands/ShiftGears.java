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
 * Shifts gears. Basically a "ToggleGear" command.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ShiftGears extends Command {

    /**
     * The drive to execute this command on
     */
    @NotNull
    private final Shiftable subsystem;

    /**
     * Default constructor
     *
     * @param subsystem The drive to execute this command on
     */
    @JsonCreator
    public ShiftGears(@NotNull @JsonProperty(required = true) Shiftable subsystem) {
        this.subsystem = subsystem;
    }

    /**
     * Log when this command is initialized
     */
    @Override
    protected void initialize() {
        Logger.addEvent("ShiftGears init.", this.getClass());
    }

    /**
     * Switch gears
     */
    @Override
    protected void execute() {
        subsystem.setGear(subsystem.getGear() == Shiftable.gear.LOW.getNumVal() ? Shiftable.gear.HIGH.getNumVal() : Shiftable.gear.LOW.getNumVal());
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
        Logger.addEvent("ShiftGears end.", this.getClass());
    }

    /**
     * Log when this command is interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("ShiftGears Interrupted!", this.getClass());
    }
}