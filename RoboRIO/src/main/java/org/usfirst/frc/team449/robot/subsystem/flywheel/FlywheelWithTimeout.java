package org.usfirst.frc.team449.robot.subsystem.flywheel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.components.ConditionTimingComponentObserver;
import org.usfirst.frc.team449.robot.other.Clock;

import java.util.Objects;
import java.util.Optional;

/**
 * Flywheel that reports readiness to shoot based on both readiness of underlying implementation and
 * spin-up time.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class FlywheelWithTimeout extends SubsystemBase implements SubsystemFlywheel, Loggable {
  private final SubsystemFlywheel implementation;

  private final ConditionTimingComponentObserver speedConditionTimer;

  /** Time from giving the multiSubsystem voltage to being ready to fire, in seconds. */
  private final double timeout;

  private boolean isFlywheelOn;

  /**
   * @param implementation The actual flywheel that is being wrapped
   * @param timeoutOverride The override for the timeout value shooting condition to be reached
   *     before signalling that it is ready to shoot regardless.
   */
  @JsonCreator
  public FlywheelWithTimeout(
      @NotNull @JsonProperty(required = true) final SubsystemFlywheel implementation,
      @Nullable final Double timeoutOverride) {
    this.implementation = implementation;
    this.timeout =
        Objects.requireNonNullElse(timeoutOverride, this.implementation.getSpinUpTimeSecs());

    this.speedConditionTimer = new ConditionTimingComponentObserver(false);
  }

  @Override
  public void turnFlywheelOn(final double speed) {
    this.isFlywheelOn = true;
    this.implementation.turnFlywheelOn(speed);
  }

  @Override
  public void turnFlywheelOff() {
    this.isFlywheelOn = false;
    this.implementation.turnFlywheelOff();
  }

  public boolean isFlywheelOn() {
    return this.isFlywheelOn;
  }

  @Override
  public double getSpinUpTimeSecs() {
    return this.implementation.getSpinUpTimeSecs();
  }

  @Override
  public @NotNull Optional<Double> getSpeed() {
    return this.implementation.getSpeed();
  }

  @Log
  @Override
  public boolean isReadyToShoot() {
    return this.implementation.isConditionTrue() || this.spinUpHasTimedOut();
  }

  @Override
  public void update() {
    this.implementation.update();
    this.speedConditionTimer.update(
        Clock.currentTimeSeconds(),
        this.isFlywheelOn && !this.implementation.isConditionTrueCached());

    SubsystemFlywheel.super.update();
  }

  @Log
  private boolean spinUpHasTimedOut() {
    return this.speedConditionTimer.hasBeenTrueForAtLeast(this.timeout);
  }
}
