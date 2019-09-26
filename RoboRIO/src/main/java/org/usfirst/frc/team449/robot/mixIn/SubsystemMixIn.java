package org.usfirst.frc.team449.robot.mixIn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.wpi.first.wpilibj.command.Command;

/**
 * A mix-in for {@link edu.wpi.first.wpilibj.command.Subsystem} that adds JsonTypeInfo and then ignores any
 * getters/setters. Don't make sublasses of this.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public abstract class SubsystemMixIn {

    @JsonIgnore
    abstract Command getDefaultCommand();

    @JsonIgnore
    abstract void setDefaultCommand(Command command);

    @JsonIgnore
    abstract String getDefaultCommandName();

    @JsonIgnore
    abstract Command getCurrentCommand();

    @JsonIgnore
    abstract void setCurrentCommand(Command command);

    @JsonIgnore
    abstract String getCurrentCommandName();
}
