package org.usfirst.frc.team449.robot.subsystem.interfaces.intake.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.SubsystemIntake;

/**
 * Toggles whether the subsystem is off or set to a given mode.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ToggleIntaking extends Command {

    /**
     * The subsystem to execute this command on.
     */
    @NotNull
    private final SubsystemIntake subsystem;

    /**
     * The mode to set this subsystem to if it's currently off.
     */
    @NotNull
    private final SubsystemIntake.IntakeMode mode;

    /**
     * Default constructor
     *
     * @param subsystem The subsystem to execute this command on.
     * @param mode      The mode to set this subsystem to if it's currently off.
     */
    @JsonCreator
    public ToggleIntaking(@NotNull @JsonProperty(required = true) SubsystemIntake subsystem,
                          @NotNull @JsonProperty(required = true) SubsystemIntake.IntakeMode mode) {
        this.subsystem = subsystem;
        this.mode = mode;
    }

    /**
     * Log when this command is initialized
     */
    @Override
    protected void initialize() {
        Logger.addEvent("SetIntakeMode init.", this.getClass());
    }

    /**
     * Set the subsystem to the specified mode if it's off, and set it to off otherwise.
     */
    @Override
    protected void execute() {
        if (subsystem.getMode() == SubsystemIntake.IntakeMode.OFF) {
            subsystem.setMode(mode);
        } else {
            subsystem.setMode(SubsystemIntake.IntakeMode.OFF);
        }
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
        Logger.addEvent("SetIntakeMode end.", this.getClass());
    }

    /**
     * Log when this command is interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("SetIntakeMode Interrupted!", this.getClass());
    }
}