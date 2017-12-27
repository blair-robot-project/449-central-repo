package org.usfirst.frc.team449.robot.commands.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * A command group for running many commands in parallel.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ParallelCommandGroup extends CommandGroup {

    /**
     * Default constructor
     *
     * @param commandSet The commands to run.
     */
    @JsonCreator
    public ParallelCommandGroup(@NotNull @JsonProperty(required = true) Set<Command> commandSet) {
        for (Command command : commandSet) {
            addParallel(command);
        }
    }
}
