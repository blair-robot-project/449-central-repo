package org.usfirst.frc.team449.robot.commands.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitUntilCommand;
import org.jetbrains.annotations.NotNull;

/**
 * Runs a given command at a given in-game time.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class RunCommandAtTime extends CommandGroup {

    /**
     * Default command.
     *
     * @param time    The time, in seconds before the current period ends, to run the command at.
     * @param command The command to run.
     */
    @JsonCreator
    public RunCommandAtTime(@JsonProperty(required = true) double time,
                            @NotNull @JsonProperty(required = true) Command command) {
        addSequential(new WaitUntilCommand(time));
        addSequential(command);
    }
}
