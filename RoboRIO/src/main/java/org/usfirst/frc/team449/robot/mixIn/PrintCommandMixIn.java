package org.usfirst.frc.team449.robot.mixIn;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.wpilibj2.command.PrintCommand;

/**
 * A mix-in for {@link PrintCommand} that annotates its constructor
 * for use with Jackson. Don't make sublasses of this.
 */
public abstract class PrintCommandMixIn {
  /** @see PrintCommand#PrintCommand(String) */
  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  public PrintCommandMixIn(@JsonProperty(required = true) final String message) {}
}
