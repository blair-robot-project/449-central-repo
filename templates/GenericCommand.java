package template;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandWrapper;
import org.usfirst.frc.team449.robot.other.Logger;

/**
 * Generic command.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class GenericCommand extends YamlCommandWrapper {

    /**
     * The subsystem to execute this command on
     */
    @NotNull
    private final GenericSubsytemInterface subsystem;

    /**
     * Default constructor
     *
     * @param subsystem The subsystem to execute this command on.
     */
    @JsonCreator
    public RunMotorWhileConditonMet(@NotNull @JsonProperty(required = true) GenericSubsytemInterface subsystem) {
        this.subsystem = subsystem;
    }

    /**
     * Log when this command is initialized
     */
    @Override
    protected void initialize() {
        Logger.addEvent("GenericCommand init", this.getClass());
    }

    /**
     * Do something
     */
    @Override
    protected void execute() {
        //Put something to do here
    }

    /**
     * Stop when finished.
     *
     * @return true if finished, false otherwise.
     */
    @Override
    protected boolean isFinished() {
        //Return whether something is true or not
    }

    /**
     * Log that the command has ended.
     */
    @Override
    protected void end() {
        Logger.addEvent("GenericSubsytemInterface end", this.getClass());
    }

    /**
     * Log that the command has been interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("GenericSubsytemInterface interrupted!", this.getClass());
    }

}
