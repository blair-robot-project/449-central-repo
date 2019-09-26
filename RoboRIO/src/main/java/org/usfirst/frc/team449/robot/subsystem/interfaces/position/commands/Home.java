package org.usfirst.frc.team449.robot.subsystem.interfaces.position.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.position.SubsystemPosition;

/**
 * Move the motor until it hits a limit switch in order to "zero" it.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Home<T extends Subsystem & SubsystemPosition> extends Command {

    /**
     * The subsystem to execute this command on.
     */
    @NotNull
    private final T subsystem;

    /**
     * The speed to go at, on [0, 1].
     */
    private double speed;

    /**
     * Whether to use the forward or reverse limit switch.
     */
    private boolean useForward;

    /**
     * Default constructor
     *
     * @param subsystem  The subsystem to execute this command on.
     * @param speed      The speed to go at, on [0, 1]. The sign is automatically adjusted based on useForward.
     * @param useForward Whether to use the forward or reverse limit switch. Defaults to using reverse.
     */
    @JsonCreator
    public Home(@NotNull @JsonProperty(required = true) T subsystem,
                @JsonProperty(required = true) double speed,
                boolean useForward) {
        requires(subsystem);
        this.subsystem = subsystem;
        this.speed = speed;
        this.useForward = useForward;
    }

    /**
     * Log on init
     */
    @Override
    protected void initialize() {
        Logger.addEvent("Home init", this.getClass());
    }

    /**
     * Set the setpoint
     */
    @Override
    protected void execute() {
        if (useForward) {
            subsystem.setMotorOutput(speed);
        } else {
            subsystem.setMotorOutput(-speed);
        }
    }

    /**
     * Exit when we hit the limit switch.
     *
     * @return True if the given limit switch is triggered, false otherwise
     */
    @Override
    protected boolean isFinished() {
        if (useForward) {
            return subsystem.getForwardLimit();
        } else {
            return subsystem.getReverseLimit();
        }
    }

    /**
     * Disable the motor and set the position to zero on end.
     */
    @Override
    public void end() {
        subsystem.resetPosition();
        subsystem.disableMotor();
    }
}
