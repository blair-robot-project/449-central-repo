package org.usfirst.frc.team449.robot.generalInterfaces.shiftable.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable;
import org.usfirst.frc.team449.robot.other.Logger;

/**
 * Switches to a specified gear.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SwitchToGear extends Command {

    /**
     * The drive to execute this command on.
     */
    @NotNull
    private final Shiftable subsystem;

    /**
     * The gear to switch to.
     */
    private final int switchTo;

    /**
     * Default constructor
     *
     * @param subsystem   The drive to execute this command on.
     * @param switchToNum The number of the gear to switch to. Is ignored if switchTo isn't null.
     * @param switchTo    The gear to switch to. Can be null, and if it is, switchToNum is used instead.
     */
    @JsonCreator
    public SwitchToGear(@NotNull @JsonProperty(required = true) Shiftable subsystem,
                        int switchToNum,
                        @Nullable Shiftable.gear switchTo) {
        this.subsystem = subsystem;
        if (switchTo != null) {
            this.switchTo = switchTo.getNumVal();
        } else {
            this.switchTo = switchToNum;
        }
    }

    /**
     * Log when this command is initialized
     */
    @Override
    protected void initialize() {
        Logger.addEvent("SwitchToGear init.", this.getClass());
    }

    /**
     * Switch to the specified gear
     */
    @Override
    protected void execute() {
        subsystem.setGear(switchTo);
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
        Logger.addEvent("SwitchToGear end.", this.getClass());
    }

    /**
     * Log when this command is interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("SwitchToGear Interrupted!", this.getClass());
    }
}