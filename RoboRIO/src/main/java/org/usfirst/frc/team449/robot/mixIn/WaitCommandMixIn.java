package org.usfirst.frc.team449.robot.mixIn;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.wpilibj2.command.WaitCommand;

/**
 * A mix-in for {@link WaitCommand} that annotates its constructor
 * for use with Jackson. Don't make sublasses of this.
 */
public abstract class WaitCommandMixIn {
  /** @see WaitCommand#WaitCommand(double) */
  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public WaitCommandMixIn(@JsonProperty(value = "seconds", required = true) final double seconds) {}
}
