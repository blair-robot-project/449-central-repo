package templates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandWrapper;
import org.usfirst.frc.team449.robot.other.Logger;

/**
 * A command that does an instantaneous change (extend a piston, turn on a motor, etc.)
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class StateChangeCommand extends YamlCommandWrapper {

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
    protected void initialize() {
        Logger.addEvent("StateChangeCommand init.", this.getClass());
    }

    /**
     * Do the state change.
     */
    @Override
    protected void execute() {
        subsystem.doAThing();
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
        Logger.addEvent("StateChangeCommand end.", this.getClass());
    }

    /**
     * Log when this command is interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("StateChangeCommand Interrupted!", this.getClass());
    }
}