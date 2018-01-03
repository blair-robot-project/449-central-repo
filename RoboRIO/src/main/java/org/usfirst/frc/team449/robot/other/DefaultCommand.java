package org.usfirst.frc.team449.robot.other;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;

/**
 * A class that sets the default command for a subsystem when constructed.
 */
public class DefaultCommand {

    /**
     * Sets the given command as the default command for the given subsystem.
     *
     * @param subsystem The subsystem to set the default command for.
     * @param command   The command to set as the default.
     */
    @JsonCreator
    public DefaultCommand(@NotNull @JsonProperty(required = true) YamlSubsystem subsystem,
                          @NotNull @JsonProperty(required = true) Command command) {
        subsystem.setDefaultCommandManual(command);
    }
}
