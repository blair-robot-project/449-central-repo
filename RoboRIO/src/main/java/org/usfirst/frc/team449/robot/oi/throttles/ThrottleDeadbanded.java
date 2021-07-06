package org.usfirst.frc.team449.robot.oi.throttles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.LinearFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A throttle with a deadband and smoothing. */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ThrottleDeadbanded extends ThrottleBasic {

  /** The value below which the joystick input is considered 0. */
  protected final double deadband;
  /** The smoothing filter for this joystick. */
  private final LinearFilter filter;
  /** The input from the joystick. Declared outside of getValue to avoid garbage collection. */
  private double input;
  /**
   * The sign of the input from the joystick. Declared outside of getValue to avoid garbage
   * collection.
   */
  private double sign;

  /**
   * A basic constructor.
   *
   * @param stick The Joystick object being used
   * @param axis The axis being used. 0 is X, 1 is Y, 2 is Z.
   * @param deadband The deadband below which the input will be read as 0, on [0, 1]. Defaults to 0.
   * @param smoothingTimeSecs How many seconds of input to take into account when smoothing.
   *     Defaults to 0.02.
   * @param inverted Whether or not to invert the joystick input. Defaults to false.
   */
  @JsonCreator
  public ThrottleDeadbanded(
      @NotNull @JsonProperty(required = true) final MappedJoystick stick,
      @JsonProperty(required = true) final int axis,
      final double deadband,
      @Nullable final Double smoothingTimeSecs,
      final boolean inverted) {
    super(stick, axis, inverted);
    this.deadband = deadband;
    this.filter =
        LinearFilter.singlePoleIIR(smoothingTimeSecs != null ? smoothingTimeSecs : 0.02, 0.02);
  }

  /**
   * Gets the value from the joystick and deadbands it. The non-deadband values are scaled to avoid
   * a discontinuity.
   *
   * @return The joystick's value, after being deadbanded.
   */
  @Override
  public double getValue() {
    // Get the smoothed value
    this.input = this.filter.calculate(this.pidGet());

    this.sign = Math.signum(this.input);
    this.input = Math.abs(this.input);

    // apply the deadband.
    if (this.input < this.deadband) {
      return 0;
    }

    // scale so f(deadband) is 0 and f(1) is 1.
    this.input = (this.input - this.deadband) / (1. - this.deadband);

    return this.sign * this.input;
  }
}
