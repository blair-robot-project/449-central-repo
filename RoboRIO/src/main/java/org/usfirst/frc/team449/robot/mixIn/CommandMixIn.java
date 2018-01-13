package org.usfirst.frc.team449.robot.mixIn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.wpi.first.wpilibj.command.CommandGroup;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public abstract class CommandMixIn {

    @JsonIgnore
    abstract void setTimeout(double seconds);

    @JsonIgnore
    abstract void setParent(CommandGroup parent);

    @JsonIgnore
    abstract void setInterruptible(boolean interruptible);

    @JsonIgnore
    abstract void setRunWhenDisabled(boolean run);
}
