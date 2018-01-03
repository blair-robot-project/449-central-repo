package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;

/**
 * A command that's constructable from YAML.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface YamlCommand {

	/**
	 * Get the command object this object is.
	 *
	 * @return this.
	 */
	@NotNull
	Command getCommand();
}
