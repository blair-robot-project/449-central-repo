package org.usfirst.frc.team449.robot.subsystem.interfaces.binaryMotor.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.binaryMotor.SubsystemBinaryMotor;

/**
 * A command that toggles the state of the motor between off and on.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ToggleMotor extends Command {

    /**
     * The subsystem to execute this command on.
     */
    @NotNull
    private final SubsystemBinaryMotor subsystem;

    /**
     * Default constructor
     *
     * @param subsystem The subsystem to execute this command on.
     */
    @JsonCreator
    public ToggleMotor(@NotNull @JsonProperty(required = true) SubsystemBinaryMotor subsystem) {
        this.subsystem = subsystem;
    }

    /**
     * Log when this command is initialized
     */
    @Override
    protected void initialize() {
        Logger.addEvent("ToggleMotor init.", this.getClass());
    }

    /**
     * Toggle the motor state.
     */
    @Override
    protected void execute() {
        if (subsystem.isMotorOn()) {
            subsystem.turnMotorOff();
        } else {
            subsystem.turnMotorOn();
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
        Logger.addEvent("ToggleMotor end.", this.getClass());
    }

    /**
     * Log when this command is interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("ToggleMotor Interrupted!", this.getClass());
    }
}