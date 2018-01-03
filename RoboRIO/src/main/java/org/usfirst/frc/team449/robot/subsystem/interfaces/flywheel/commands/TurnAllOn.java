package org.usfirst.frc.team449.robot.subsystem.interfaces.flywheel.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.flywheel.SubsystemFlywheel;

/**
 * Turn on the flywheel and the feeder.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class TurnAllOn extends Command {

    /**
     * The subsystem to execute this command on.
     */
    @NotNull
    private final SubsystemFlywheel subsystem;

    /**
     * Default constructor
     *
     * @param subsystem The subsystem to execute this command on.
     */
    @JsonCreator
    public TurnAllOn(@NotNull @JsonProperty(required = true) SubsystemFlywheel subsystem) {
        this.subsystem = subsystem;
    }

    /**
     * Log when this command is initialized
     */
    @Override
    protected void initialize() {
        Logger.addEvent("TurnAllOn init.", this.getClass());
    }

    /**
     * Turn on the flywheel and feeder.
     */
    @Override
    protected void execute() {
        subsystem.turnFeederOn();
        subsystem.turnFlywheelOn();
        subsystem.setFlywheelState(SubsystemFlywheel.FlywheelState.SHOOTING);
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
        Logger.addEvent("TurnAllOn end.", this.getClass());
    }

    /**
     * Log when this command is interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("TurnAllOn Interrupted!", this.getClass());
    }
}