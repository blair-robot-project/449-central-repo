package org.usfirst.frc.team449.robot.subsystem.interfaces.binaryMotor.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.binaryMotor.SubsystemBinaryMotor;

/**
 * Turns off the motor of the subsystem, but does so while using requires() to interrupt any other commands currently
 * controlling the subsystem.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class TurnMotorOffWithRequires<T extends YamlSubsystem & SubsystemBinaryMotor> extends TurnMotorOff {

    /**
     * Default constructor
     *
     * @param subsystem The subsystem to execute this command on.
     */
    @JsonCreator
    public TurnMotorOffWithRequires(@NotNull @JsonProperty(required = true) T subsystem) {
        super(subsystem);
        requires(subsystem);
    }

    /**
     * Log when this command is initialized
     */
    @Override
    protected void initialize() {
        Logger.addEvent("TurnMotorOffWithRequires init.", this.getClass());
    }

    /**
     * Log when this command ends
     */
    @Override
    protected void end() {
        Logger.addEvent("TurnMotorOffWithRequires end.", this.getClass());
    }

    /**
     * Log when this command is interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("TurnMotorOffWithRequires Interrupted!", this.getClass());
    }
}