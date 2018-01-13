package org.usfirst.frc.team449.robot.drive.unidirectional.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectional;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.other.Clock;
import org.usfirst.frc.team449.robot.other.Logger;

/**
 * A command to ramp up the motors to full power at a given voltage rate.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class VoltageRamp<T extends YamlSubsystem & DriveUnidirectional> extends Command {

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
     * @param subsystem      The subsystem to execute this command on
     * @param voltsPerSecond How many volts to increase the output by per second.
     */
    @JsonCreator
    public VoltageRamp(@NotNull @JsonProperty(required = true) T subsystem,
                       @JsonProperty(required = true) double voltsPerSecond) {
        requires(subsystem);
        this.subsystem = subsystem;
        this.percentPerMillis = voltsPerSecond / 12. / 1000.;
    }

    /**
     * Reset the output
     */
    @Override
    protected void initialize() {
        Logger.addEvent("VoltageRamp init.", this.getClass());
        lastTime = Clock.currentTimeMillis();
        output = 0;
    }

    /**
     * Update the output based on how long it's been since execute() was last run.
     */
    @Override
    protected void execute() {
        output += percentPerMillis * (Clock.currentTimeMillis() - lastTime);
        subsystem.setOutput(output, output);
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
        subsystem.setOutput(0, 0);
        Logger.addEvent("VoltageRamp end.", this.getClass());
    }

    /**
     * Log on interrupt.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("VoltageRamp Interrupted!", this.getClass());
    }
}