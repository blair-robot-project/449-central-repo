package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * A Jackson-compatible wrapper on {@link Subsystem}.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public abstract class YamlSubsystem extends Subsystem {

    /**
     * Set the default command.
     *
     * @param defaultCommand The command to have run by default. Must require this subsystem.
     */
    public void setDefaultCommandManual(Command defaultCommand) {
        setDefaultCommand(defaultCommand);
    }
}
