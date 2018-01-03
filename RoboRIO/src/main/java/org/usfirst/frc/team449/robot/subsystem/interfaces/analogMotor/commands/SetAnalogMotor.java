package org.usfirst.frc.team449.robot.subsystem.interfaces.analogMotor.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.analogMotor.SubsystemAnalogMotor;

/**
 * A command that runs an analog motor at the given setpoint.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SetAnalogMotor extends Command {

    /**
     * The subsystem to execute this command on.
     */
    @NotNull
    private final SubsystemAnalogMotor subsystem;

    /**
     * The setpoint to run the motor at.
     */
    private final double setpoint;

    /**
     * Default constructor
     *
     * @param subsystem The subsystem to execute this command on.
     * @param setpoint  The setpoint to run the motor at.
     */
    @JsonCreator
    public SetAnalogMotor(@NotNull @JsonProperty(required = true) SubsystemAnalogMotor subsystem,
                          @JsonProperty(required = true) double setpoint) {
        this.subsystem = subsystem;
        this.setpoint = setpoint;
    }

    /**
     * Log when this command is initialized
     */
    @Override
    protected void initialize() {
        Logger.addEvent("DisableAnalogMotor init.", this.getClass());
    }

    /**
     * Set the setpoint.
     */
    @Override
    protected void execute() {
        subsystem.set(setpoint);
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
        Logger.addEvent("SetAnalogMotor end.", this.getClass());
    }

    /**
     * Log when this command is interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("SetAnalogMotor Interrupted!", this.getClass());
    }
}