package org.usfirst.frc.team449.robot.components;

import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.Contract;
import org.usfirst.frc.team449.robot.other.RegistrationOrderIDUtil;

/**
 * Utility class that records and provides methods for examining the times at which a condition
 * becomes true or false. The class is an abstract observer to allow subclasses to provide custom
 * implementations for updating the condition. This class is unit-agnostic.
 *
 * <p>Behavior may be unexpected if object is updated more frequently than values are examined.
 */
public abstract class ConditionTimingComponent implements Loggable {
  @Log private boolean current;
  @Log.ToString private double now = Double.NaN;
  @Log.ToString private double lastBecameTrue = Double.NaN;
  @Log.ToString private double lastBecameFalse = Double.NaN;

  public ConditionTimingComponent(final boolean initialValue) {
    this.current = initialValue;

    RegistrationOrderIDUtil.registerInstance(this);
  }

  /**
   * Updates the current state of the condition.
   *
   * @param now the current time
   * @param value the current value
   */
  @Contract(pure = false)
  public void update(final double now, final boolean value) {
    this.now = now;
    if (value != this.current) this.forceUpdate(now, value);
  }

  /**
   * Updates the current state of the condition and acts as if condition changed even if it did not.
   *
   * @param now the current time
   */
  @Contract(pure = false)
  public void forceUpdate(final double now, final boolean value) {
    this.now = now;

    if (value) {
      this.lastBecameTrue = now;
    } else {
      this.lastBecameFalse = now;
    }

    this.current = value;
  }

  /**
   * Gets the time at which the condition last became true.
   *
   * <p>Returns {@link Double#NaN} if the condition has never become true.
   *
   * @return the time supplied to the most recent call to {@link
   *     ConditionTimingComponent#update(double, boolean)} where the value supplied to the method
   *     was true and the previous state of the condition was false.
   */
  @Contract(pure = true)
  public double lastBecameTrueTime() {
    return this.lastBecameTrue;
  }

  @Contract(pure = true)
  public double becameTrueTime() {
    if (!this.current) return Double.NaN;
    return this.lastBecameTrueTime();
  }

  /**
   * Gets the time at which the condition last became false.
   *
   * <p>Returns {@link Double#NaN} if the condition has never become false.
   *
   * @return the time supplied to the most recent call to {@link
   *     ConditionTimingComponent#update(double, boolean)} where the value supplied to the method
   *     was false and the previous state of the condition was true.
   */
  @Contract(pure = true)
  public double lastBecameFalseTime() {
    return this.lastBecameFalse;
  }

  @Contract(pure = true)
  public double becameFalseTime() {
    if (this.current) return Double.NaN;
    return this.lastBecameFalseTime();
  }

  /** Returns {@link Double#NaN} if not currently true. */
  @Contract(pure = true)
  public double timeSinceBecameTrue() {
    if (!this.current) return Double.NaN;
    return this.timeSinceLastBecameTrue();
  }

  /** Returns {@link Double#NaN} if not currently false. */
  @Contract(pure = true)
  public double timeSinceBecameFalse() {
    if (this.current) return Double.NaN;
    return this.timeSinceLastBecameFalse();
  }

  @Contract(pure = true)
  public double timeSinceLastBecameTrue() {
    return this.now - this.lastBecameTrue;
  }

  @Contract(pure = true)
  public double timeSinceLastBecameFalse() {
    return this.now - this.lastBecameFalse;
  }

  /** @implNote returns min(lastBecameTrue(), lastBecameFalse()) */
  @Contract(pure = true)
  public double lastUpdateTime() {
    return this.now;
  }

  /** @implNote returns max(lastBecameTrue(), lastBecameFalse()) */
  @Contract(pure = true)
  public double lastChangeTime() {
    // Should be max, as time increases over time.
    return Math.max(this.lastBecameFalse, this.lastBecameTrue);
  }

  @Contract(pure = true)
  public boolean hasBeenFalseForAtLeast(final double duration) {
    return this.timeSinceBecameFalse() >= duration;
  }

  @Contract(pure = true)
  public boolean hasBeenTrueForAtLeast(final double duration) {
    return this.timeSinceBecameTrue() >= duration;
  }

  @Contract(pure = true)
  public boolean hasBeenFalseForAtMost(final double duration) {
    return this.timeSinceBecameFalse() <= duration;
  }

  @Contract(pure = true)
  public boolean hasBeenTrueForAtMost(final double duration) {
    return this.timeSinceBecameTrue() <= duration;
  }

  @Contract(pure = true)
  public boolean justBecameTrue() {
    return this.hasBeenTrueForAtMost(0);
  }

  @Contract(pure = true)
  public boolean justBecameFalse() {
    return this.hasBeenFalseForAtMost(0);
  }

  @Contract(pure = true)
  public boolean isTrue() {
    return this.current;
  }

  @Override
  @Contract(pure = true)
  public String configureLogName() {
    return this.getClass().getSimpleName() + RegistrationOrderIDUtil.getExistingID(this);
  }
}
