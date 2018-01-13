package org.usfirst.frc.team449.robot.subsystem.interfaces.analogMotor.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.oi.throttles.Throttle;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.analogMotor.SubsystemAnalogMotor;

/**
 * A command to control an analog motor with a throttle.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ControlAnalogMotorWithThrottle<T extends YamlSubsystem & SubsystemAnalogMotor> extends Command {

    /**
     * The subsystem to execute this command on
     */
    @NotNull
    private final T subsystem;

    /**
     * The throttle that controls the motor.
     */
    @NotNull
    private final Throttle throttle;

    /**
     * Default constructor
     *
     * @param subsystem The subsystem to execute this command on.
     * @param throttle  The throttle that controls the motor.
     */
    @JsonCreator
    public ControlAnalogMotorWithThrottle(@NotNull @JsonProperty(required = true) T subsystem,
                                          @NotNull @JsonProperty(required = true) Throttle throttle) {
        this.subsystem = subsystem;
        requires(subsystem);
        this.throttle = throttle;
    }

    /**
     * Log when this command is initialized
     */
    @Override
    protected void initialize() {
        Logger.addEvent("ControlAnalogMotorWithThrottle init", this.getClass());
    }

    /**
     * Set the motor output to the throttle output.
     */
    @Override
    protected void execute() {
        subsystem.set(throttle.getValueCached());
    }

    /**
     * Stop when finished.
     *
     * @return true if finished, false otherwise.
     */
    @Override
    protected boolean isFinished() {
        return false;
    }

    /**
     * Log that the command has ended.
     */
    @Override
    protected void end() {
        Logger.addEvent("ControlAnalogMotorWithThrottle end", this.getClass());
    }

    /**
     * Log that the command has been interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("ControlAnalogMotorWithThrottle interrupted!", this.getClass());
    }

}
