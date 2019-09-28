package org.usfirst.frc.team449.robot.commands.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.WaitUntilCommand;

/**
 * Waits until a certain in-game time.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MappedWaitUntilCommand extends WaitUntilCommand {

    /**
     * Default constructor.
     *
     * @param time The time, in seconds after auto starts, to wait until.
     */
    @JsonCreator
    public MappedWaitUntilCommand(@JsonProperty(required = true) double time) {
        super(time);
    }
}
