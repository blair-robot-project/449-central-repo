package org.usfirst.frc.team449.robot.mixIn;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.PerpetualCommand;

/**
 * A mix-in for {@link PerpetualCommand} that annotates its constructor for use with Jackson. Don't
 * make subclasses of this.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public abstract class PerpetualCommandMixIn {
  /** @see PerpetualCommand#PerpetualCommand(Command) */
  @JsonCreator
  public PerpetualCommandMixIn(
      @JsonProperty(value = "command", required = true) final Command command) {}
}
