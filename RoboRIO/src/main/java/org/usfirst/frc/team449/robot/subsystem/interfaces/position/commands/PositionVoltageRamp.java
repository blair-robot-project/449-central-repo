package org.usfirst.frc.team449.robot.subsystem.interfaces.position.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.other.Clock;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.position.SubsystemPosition;

/**
 * Slowly increase the output to the motors in order to characterize the system.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class PositionVoltageRamp<T extends Subsystem & SubsystemPosition> extends Command {

    /**
     * The subsystem to execute this command on.
     */
    @NotNull
    private final T subsystem;

    /**
     * The number of percentage points to increase motor output by per millisecond.
     */
    private final double percentPerMillis;

    /**
     * The last time execute() was run.
     */
    private long lastTime;

    /**
     * The output to give to the motors.
     */
    private double output;

    /**
     * Default constructor
     *
     * @param subsystem       The subsystem to execute this command on
     * @param voltsPerSecond  How many volts to increase the output by per second.
     * @param startingVoltage The voltage to start the ramp at. Defaults to 0.
     */
    @JsonCreator
    public PositionVoltageRamp(@NotNull @JsonProperty(required = true) T subsystem,
                               @JsonProperty(required = true) double voltsPerSecond,
                               double startingVoltage) {
        requires(subsystem);
        this.subsystem = subsystem;
        this.percentPerMillis = voltsPerSecond / 12. / 1000.;
        this.output = startingVoltage;
    }

    /**
     * Reset the output
     */
    @Override
    protected void initialize() {
        Logger.addEvent("PositionVoltageRamp init.", this.getClass());
        lastTime = Clock.currentTimeMillis();
    }

    /**
     * Update the output based on how long it's been since execute() was last run.
     */
    @Override
    protected void execute() {
        output += percentPerMillis * (Clock.currentTimeMillis() - lastTime);
        subsystem.setMotorOutput(output);
        lastTime = Clock.currentTimeMillis();
    }

    /**
     * Exit if the output is greater than the motors can produce.
     *
     * @return true if the output is greater than or equal to 1, false otherwise.
     */
    @Override
    protected boolean isFinished() {
        return output >= 1.;
    }

    /**
     * Log and stop on end.
     */
    @Override
    protected void end() {
        subsystem.disableMotor();
        Logger.addEvent("PositionVoltageRamp end.", this.getClass());
    }

    /**
     * Log on interrupt.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("PositionVoltageRamp Interrupted!", this.getClass());
    }
}