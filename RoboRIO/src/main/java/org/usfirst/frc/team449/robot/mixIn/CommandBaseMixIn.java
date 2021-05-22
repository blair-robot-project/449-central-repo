package org.usfirst.frc.team449.robot.mixIn;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import edu.wpi.first.wpilibj2.command.Subsystem;

/**
 * A mix-in for {@link edu.wpi.first.wpilibj2.command.CommandBase}. Don't make subclasses of this.
 */
public abstract class CommandBaseMixIn {
  /** @see edu.wpi.first.wpilibj2.command.CommandBase#addRequirements(Subsystem...) */
  @JsonSetter(value = "requiredSubsystems", nulls = Nulls.SKIP, contentNulls = Nulls.FAIL)
  @JsonAlias("requirements")
  abstract void addRequirements(Subsystem... requirements);
}
