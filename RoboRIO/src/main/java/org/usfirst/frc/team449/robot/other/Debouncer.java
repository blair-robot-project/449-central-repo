package org.usfirst.frc.team449.robot.other;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.Contract;
import org.usfirst.frc.team449.robot.components.ConditionTimingComponent;

/** A timer that checks if condition has been true for the past n seconds/milliseconds. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Debouncer extends ConditionTimingComponent {
  /** How long the condition has to be true for, in seconds. */
  private final double bufferTime;

  /**
   * Constructor for a time given in seconds.
   *
   * @param bufferTimeSeconds the amount of time the condition has to be true for, in seconds
   */
  @JsonCreator
  public Debouncer(final double bufferTimeSeconds) {
    super(false);
    this.bufferTime = bufferTimeSeconds;
  }

  /**
   * Constructor for a time given in milliseconds.
   *
   * @param bufferTimeMilliseconds the amount of time the condition has to be true for, in
   *     milliseconds
   */
  public Debouncer(final long bufferTimeMilliseconds) {
    this(bufferTimeMilliseconds * 0.001);
  }

  /**
   * Get whether the condition has been true for at least the specified amount of time.
   *
   * @param currentState the current state of the condition
   * @return {@code true} if the condition has been true for the specified amount of time, {@code
   *     false} otherwise
   */
  public boolean get(final boolean currentState) {
    this.update(Clock.currentTimeSeconds(), currentState);
    return this.hasBeenTrueForAtLeast(bufferTime);
  }

  @Log
  @Contract(pure = true)
  private boolean log_get() {
    return this.get(this.isTrue());
  }
}
