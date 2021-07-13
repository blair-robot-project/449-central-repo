package org.usfirst.frc.team449.robot.mixIn;

import com.fasterxml.jackson.annotation.*;
import edu.wpi.first.wpilibj.DoubleSolenoid;

/**
 * A mix-in for {@link DoubleSolenoid} that annotates its constructor for use with Jackson. Don't
 * make subclasses of this.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public abstract class DoubleSolenoidMixIn {
  /** @see DoubleSolenoid#DoubleSolenoid(int, int, int) */
  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public DoubleSolenoidMixIn(
      @JsonProperty(value = "moduleNumber") @JsonAlias("module") final int moduleNumber,
      @JsonProperty(value = "forwardChannel", required = true) @JsonAlias("forward")
          final int forwardChannel,
      @JsonProperty(value = "reverseChannel", required = true) @JsonAlias("reverse")
          final int reverseChannel) {}
}
