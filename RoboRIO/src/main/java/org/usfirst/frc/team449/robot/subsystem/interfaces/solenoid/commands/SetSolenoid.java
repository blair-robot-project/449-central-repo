package org.usfirst.frc.team449.robot.subsystem.interfaces.solenoid.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.InstantCommand;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.solenoid.SubsystemSolenoid;

/**
 * A command that sets a piston to a given position.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SetSolenoid extends InstantCommand {

    /**
     * The subsystem to execute this command on.
     */
    @NotNull
    private final SubsystemSolenoid subsystem;

    /**
     * The value to set the solenoid to.
     */
    @NotNull
    private final DoubleSolenoid.Value pistonPos;

    /**
     * Default constructor
     *
     * @param subsystem The solenoid subsystem to execute this command on.
     * @param pistonPos The value to set the solenoid to.
     */
    @JsonCreator
    public SetSolenoid(@NotNull @JsonProperty(required = true) SubsystemSolenoid subsystem,
                       @NotNull @JsonProperty(required = true) DoubleSolenoid.Value pistonPos) {
        this.subsystem = subsystem;
        this.pistonPos = pistonPos;
    }

    /**
     * Log when this command is initialized
     */
    @Override
    protected void initialize() {
        Logger.addEvent("SetSolenoid init.", this.getClass());
    }

    /**
     * Retract the piston.
     */
    @Override
    protected void execute() {
        subsystem.setSolenoid(pistonPos);
    }

    /**
     * Log when this command ends
     */
    @Override
    protected void end() {
        Logger.addEvent("SetSolenoid end.", this.getClass());
    }

    /**
     * Log when this command is interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("SetSolenoid Interrupted!", this.getClass());
    }
}