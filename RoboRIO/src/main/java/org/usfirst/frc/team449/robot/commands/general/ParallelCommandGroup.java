package org.usfirst.frc.team449.robot.commands.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommand;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandGroupWrapper;

import java.util.Set;

/**
 * A command group for running many commands in parallel.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ParallelCommandGroup extends YamlCommandGroupWrapper {

	/**
	 * Default constructor
	 *
	 * @param commandSet The commands to run.
	 */
	@JsonCreator
	public ParallelCommandGroup(@NotNull @JsonProperty(required = true) Set<YamlCommand> commandSet) {
		for (YamlCommand command : commandSet) {
			addParallel(command.getCommand());
		}
	}
}
