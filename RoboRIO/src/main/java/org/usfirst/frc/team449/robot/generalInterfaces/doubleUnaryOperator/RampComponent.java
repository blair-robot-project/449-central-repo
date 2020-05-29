package org.usfirst.frc.team449.robot.generalInterfaces.doubleUnaryOperator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.function.DoubleUnaryOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.other.Clock;

/** A component for limiting the rate of change of a value. */
public class RampComponent implements DoubleUnaryOperator, Cloneable {

  /** The maximum allowed change in the value per second. */
  private final double maxIncreasePerMillis, maxDecreasePerMillis;

  /** The value most recently returned. */
  private double lastValue;

  /** The time, in milliseconds, that the value most recently returned was returned at. */
  private long lastTime;

  /**
   * Default constructor.
   *
   * @param maxIncreasePerSecond The maximum allowed increase in the value per second.
   * @param maxDecreasePerSecond The maximum allowed decrease in the value per second. Should be
   *     positive. Defaults to maxIncreasePerSecond.
   */
  @JsonCreator
  public RampComponent(
      @JsonProperty(required = true) double maxIncreasePerSecond,
      @Nullable Double maxDecreasePerSecond) {
    this.maxIncreasePerMillis = maxIncreasePerSecond / 1000.;
    this.maxDecreasePerMillis =
        maxDecreasePerSecond != null ? maxDecreasePerSecond / 1000. : maxIncreasePerMillis;
  }

  /**
   * Ramp the given value.
   *
   * @param value The current value of whatever it is you're ramping
   * @return The ramped version of that value.
   */
  @Override
  public double applyAsDouble(double value) {
    if (value > lastValue) {
      lastValue =
          Math.min(
              value, lastValue + (Clock.currentTimeMillis() - lastTime) * maxIncreasePerMillis);
    } else {
      lastValue =
          Math.max(
              value, lastValue - (Clock.currentTimeMillis() - lastTime) * maxDecreasePerMillis);
    }
    lastTime = Clock.currentTimeMillis();
    return lastValue;
  }

  /**
   * Get an a copy of this object.
   *
   * @return a new {@link RampComponent} with the same max change per second
   */
  @Override
  @NotNull
  public RampComponent clone() {
    return new RampComponent(maxIncreasePerMillis * 1000., maxDecreasePerMillis * 1000.);
  }
}
