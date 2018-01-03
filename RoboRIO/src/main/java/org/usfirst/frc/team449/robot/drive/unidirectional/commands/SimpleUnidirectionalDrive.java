package org.usfirst.frc.team449.robot.drive.unidirectional.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectional;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.oi.unidirectional.OIUnidirectional;
import org.usfirst.frc.team449.robot.other.Logger;

/**
 * Very simple unidirectional drive control.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SimpleUnidirectionalDrive<T extends YamlSubsystem & DriveUnidirectional> extends Command {

    /**
     * The OI used for input.
     */
    @NotNull
    public final OIUnidirectional oi;

    /**
     * The subsystem to execute this command on.
     */
    @NotNull
    private final T subsystem;

    /**
     * Default constructor
     *
     * @param subsystem The subsystem to execute this command on
     * @param oi        The OI that gives the input to this command.
     */
    @JsonCreator
    public SimpleUnidirectionalDrive(@NotNull @JsonProperty(required = true) T subsystem,
                                     @NotNull @JsonProperty(required = true) OIUnidirectional oi) {
        this.oi = oi;
        this.subsystem = subsystem;
        //Default commands need to require their subsystems.
        requires(subsystem);
    }

    /**
     * Stop the drive for safety reasons.
     */
    @Override
    protected void initialize() {
        subsystem.fullStop();
    }

    /**
     * Give output to the motors based on the stick inputs.
     */
    @Override
    protected void execute() {
        subsystem.setOutput(oi.getLeftOutputCached(), oi.getRightOutputCached());
    }

    /**
     * Run constantly because this is a default drive
     *
     * @return false
     */
    @Override
    protected boolean isFinished() {
        return false;
    }

    /**
     * Log and brake when interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("SimpleUnidirectionalDrive Interrupted! Stopping the robot.", this.getClass());
        //Brake for safety!
        subsystem.fullStop();
    }
}
