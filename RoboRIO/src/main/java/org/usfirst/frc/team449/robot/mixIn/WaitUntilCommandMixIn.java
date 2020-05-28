package org.usfirst.frc.team449.robot.mixIn;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A mix-in for {@link edu.wpi.first.wpilibj2.command.WaitUntilCommand} that annotates its
 * constructor for use with Jackson. Don't make sublasses of this.
 */
public abstract class WaitUntilCommandMixIn {
  /** @see edu.wpi.first.wpilibj2.command.WaitUntilCommand#WaitUntilCommand(double) */
  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public WaitUntilCommandMixIn(
      @JsonProperty(value = "time", required = true) final double seconds) {}
}
