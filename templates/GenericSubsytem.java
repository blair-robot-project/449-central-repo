package templates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;

/**
 * A generic example of a subsystem.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class GenericSubsystem extends YamlSubsystem implements GenericSubsystemInterface {

    /**
     * Default constructor
     */
    @JsonCreator
    public GenericSubsystem() {
    }

    /**
     * Initialize the default command for a subsystem. By default subsystems have no default command, but if they do,
     * the default command is set with this method. It is called on all Subsystems by CommandBase in the users program
     * after all the Subsystems are created.
     */
    @Override
    protected void initDefaultCommand() {
        //Do nothing!
    }
}
