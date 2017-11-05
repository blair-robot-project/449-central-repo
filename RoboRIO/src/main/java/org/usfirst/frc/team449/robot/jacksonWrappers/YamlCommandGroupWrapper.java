package org.usfirst.frc.team449.robot.jacksonWrappers;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import org.jetbrains.annotations.NotNull;

/**
 * A wrapper on {@link CommandGroup} with @JsonTypeInfo so we can use it in maps.
 */
public abstract class YamlCommandGroupWrapper extends CommandGroup implements YamlCommand {

	/**
	 * Return the Command this is a wrapper on.
	 *
	 * @return this.
	 */
	@NotNull
	@Override
	public Command getCommand() {
		return this;
	}
}
