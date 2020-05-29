package org.usfirst.frc.team449.robot.mixIn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;

/**
 * A mix-in for {@link Subsystem} that adds JsonTypeInfo and then ignores getters/setters. Don't
 * make sublasses of this.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "@class")
public abstract class SubsystemMixIn {

  /** @see Subsystem#getDefaultCommand() */
  @JsonIgnore
  abstract Command getDefaultCommand();

  /** @see Subsystem#setDefaultCommand(Command) */
  @JsonIgnore
  abstract void setDefaultCommand(Command command);

  /** @see Subsystem#getCurrentCommand() */
  @JsonIgnore
  abstract Command getCurrentCommand();
}
