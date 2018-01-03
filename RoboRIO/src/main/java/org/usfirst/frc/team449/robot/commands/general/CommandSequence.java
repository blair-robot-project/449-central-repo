package org.usfirst.frc.team449.robot.commands.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommand;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandGroupWrapper;

import java.util.List;

/**
 * A command group that takes a list of commands and runs them in the order given.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class CommandSequence extends YamlCommandGroupWrapper {

	/**
	 * Default constructor
	 *
	 * @param commandList The commands to run, in order.
	 */
	@JsonCreator
	public CommandSequence(@NotNull @JsonProperty(required = true) List<YamlCommand> commandList) {
		for (YamlCommand command : commandList) {
			addSequential(command.getCommand());
		}
	}
}
