package org.usfirst.frc.team449.robot.subsystem.interfaces.analogMotor.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.analogMotor.SubsystemAnalogMotor;

/**
 * Disables the motor of the subsystem, but does so while using requires() to interrupt any other commands currently
 * controlling the subsystem.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class DisableAnalogMotorWithRequires<T extends YamlSubsystem & SubsystemAnalogMotor> extends DisableAnalogMotor {

    /**
     * Default constructor
     *
     * @param subsystem The subsystem to execute this command on.
     */
    @JsonCreator
    public DisableAnalogMotorWithRequires(@NotNull @JsonProperty(required = true) T subsystem) {
        super(subsystem);
        requires(subsystem);
    }

    /**
     * Log when this command is initialized
     */
    @Override
    protected void initialize() {
        Logger.addEvent("DisableAnalogMotorWithRequires init.", this.getClass());
    }

    /**
     * Log when this command ends
     */
    @Override
    protected void end() {
        Logger.addEvent("DisableAnalogMotorWithRequires end.", this.getClass());
    }

    /**
     * Log when this command is interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("DisableAnalogMotorWithRequires Interrupted!", this.getClass());
    }
}