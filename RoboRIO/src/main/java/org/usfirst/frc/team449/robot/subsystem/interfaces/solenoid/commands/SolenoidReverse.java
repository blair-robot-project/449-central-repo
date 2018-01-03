package org.usfirst.frc.team449.robot.subsystem.interfaces.solenoid.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.solenoid.SubsystemSolenoid;

/**
 * A command that contracts a piston.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SolenoidReverse extends Command {

    /**
     * The subsystem to execute this command on.
     */
    @NotNull
    private final SubsystemSolenoid subsystem;

    /**
     * Default constructor
     *
     * @param subsystem The solenoid subsystem to execute this command on.
     */
    @JsonCreator
    public SolenoidReverse(@NotNull @JsonProperty(required = true) SubsystemSolenoid subsystem) {
        this.subsystem = subsystem;
    }

    /**
     * Log when this command is initialized
     */
    @Override
    protected void initialize() {
        Logger.addEvent("SolenoidForward init.", this.getClass());
    }

    /**
     * Retract the piston.
     */
    @Override
    protected void execute() {
        subsystem.setSolenoid(DoubleSolenoid.Value.kReverse);
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
        Logger.addEvent("SolenoidForward end.", this.getClass());
    }

    /**
     * Log when this command is interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("SolenoidForward Interrupted!", this.getClass());
    }
}