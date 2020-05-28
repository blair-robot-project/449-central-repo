package templates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.InstantCommand;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.other.Logger;

/**
 * A command that does an instantaneous change (extend a piston, turn on a motor, etc.)
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class StateChangeCommand extends InstantCommand {

    /**
     * The subsystem to execute this command on.
     */
    @NotNull
    private final GenericSubsystemInterface subsystem;

    /**
     * Default constructor
     *
     * @param subsystem The subsystem to execute this command on.
     */
    @JsonCreator
    public StateChangeCommand(@NotNull @JsonProperty(required = true) GenericSubsystemInterface subsystem) {
        this.subsystem = subsystem;
    }

    /**
     * Log when this command is initialized
     */
    @Override
    public void initialize() {
        Logger.addEvent("StateChangeCommand init.", this.getClass());
    }

    /**
     * Do the state change.
     */
    @Override
    public void execute() {
        subsystem.doAThing();
    }

    /**
     * Log when this command ends
     */
    @Override
    public void end(boolean interrupted) {
        Logger.addEvent("StateChangeCommand end.", this.getClass());
    }

    /**
     * Log when this command is interrupted.
     */
    //TODO Remove this! @Override
    protected void interrupted() {
        Logger.addEvent("StateChangeCommand Interrupted!", this.getClass());
    }
}