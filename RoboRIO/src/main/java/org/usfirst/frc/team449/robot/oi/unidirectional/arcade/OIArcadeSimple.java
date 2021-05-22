package org.usfirst.frc.team449.robot.oi.unidirectional.arcade;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.oi.throttles.Throttle;

/** A simple, two-stick arcade drive OI. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class OIArcadeSimple extends OIArcade {

  /** Left (rotation control) stick's throttle */
  @NotNull private final Throttle rotThrottle;

  /** Right (fwd/rev control) stick's throttle */
  @NotNull private final Throttle velThrottle;

  /**
   * Default constructor
   *
   * @param rotThrottle The throttle for rotating the robot.
   * @param velThrottle The throttle for driving straight.
   * @param rescaleOutputs Whether or not to scale the left and right outputs so the max output is
   *     1. Defaults to false.
   */
  @JsonCreator
  public OIArcadeSimple(
      @NotNull @JsonProperty(required = true) final Throttle rotThrottle,
      @NotNull @JsonProperty(required = true) final Throttle velThrottle,
      final boolean rescaleOutputs) {
    super(rescaleOutputs);
    this.rotThrottle = rotThrottle;
    this.velThrottle = velThrottle;
  }

  /**
   * The forwards and rotational movement given to the drive.
   *
   * @return An array of length 2, where the first element is the forwards output and the second is
   *     the rotational, both from [-1, 1]
   */
  @Override
  @Log
  public double[] getFwdRotOutput() {
    return new double[] {velThrottle.getValue(), rotThrottle.getValue()};
  }

}
