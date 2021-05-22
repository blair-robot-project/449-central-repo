package org.usfirst.frc.team449.robot.subsystem.flywheel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.hal.SimBoolean;
import edu.wpi.first.hal.SimDevice;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.motors.smart.SmartMotor;
import org.usfirst.frc.team449.robot.generalInterfaces.subsystems.SubsystemConditional;
import org.usfirst.frc.team449.robot.other.DebouncerEx;
import org.usfirst.frc.team449.robot.other.SimUtil;

import java.util.Optional;

/** A flywheel multiSubsystem with a single flywheel and a single-motor feeder system. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class FlywheelSimple extends SubsystemBase
    implements SubsystemFlywheel, SubsystemConditional, Loggable {

  private static final int SPEED_CONDITION_BUFFER_SIZE = 30;

  /** The flywheel's motor */
  @NotNull private final SmartMotor shooterMotor;

  /**
   * Throttle at which to run the multiSubsystem; also whether the flywheel is currently commanded
   * to spin
   */
  @Log private double targetSpeed = Double.NaN;

  @Nullable private final Double maxAbsSpeedError;
  @Nullable private final Double maxRelSpeedError;
  private final double spinUpTimeoutSecs;

  @Nullable @Log.Exclude private final SimDevice simDevice;
  @Nullable private final SimBoolean sim_manualStates, sim_isAtSpeed;
  @NotNull private final DebouncerEx speedConditionDebouncer = new DebouncerEx(SPEED_CONDITION_BUFFER_SIZE);

  /** Whether the condition was met last time caching was done. */
  private boolean conditionMetCached;

  /**
   * Default constructor
   *
   * @param motor The motor controlling the flywheel.
   * @param maxAbsSpeedError The maximum difference from the target speed at which the flywheel will
   * signal that it is ready to shoot. Null means that the flywheel will always behave according to
   * the timeout specified by {@code spinUpTimeoutSecs}.
   * @param maxRelSpeedError Similar to {@code maxAbsSpeedDeviation}, but specified as a fraction of
   * the target speed. At most one of these two arguments can be non-null.
   */
  @JsonCreator
  public FlywheelSimple(
      @NotNull @JsonProperty(required = true) final SmartMotor motor,
      @JsonProperty(required = true) final double spinUpTimeoutSecs,
      @Nullable final Double maxAbsSpeedError,
      @Nullable final Double maxRelSpeedError) {

    if (maxAbsSpeedError != null && maxRelSpeedError != null)
      throw new IllegalArgumentException("Can't specify both absolute and relative max speed range");

    this.shooterMotor = motor;
    this.spinUpTimeoutSecs = spinUpTimeoutSecs;
    this.maxAbsSpeedError = maxAbsSpeedError;
    this.maxRelSpeedError = maxRelSpeedError;

    // Register variables with the WPILib simulation GUI.
    simDevice = SimDevice.create(this.getClass().getSimpleName(), motor.getPort());
    if (simDevice != null) {
      sim_manualStates = simDevice.createBoolean("ManualStates", false, false);
      sim_isAtSpeed = simDevice.createBoolean("IsAtSpeed", false, false);
    } else {
      // Nothing to see here.
      this.sim_manualStates = this.sim_isAtSpeed = null;
    }
  }

  /** Turn the flywheel on to the specified speed. */
  @Override
  public void turnFlywheelOn(final double speed) {
    this.targetSpeed = speed;

    this.shooterMotor.enable();
    this.shooterMotor.setVelocityUPS(this.targetSpeed);
  }

  /** Turn the flywheel off. */
  @Override
  public void turnFlywheelOff() {
    this.targetSpeed = Double.NaN;

    this.shooterMotor.disable();
  }

//  /** @return The current state of the flywheel. */
//  @NotNull
//  @Override
//  public SubsystemFlywheel.FlywheelState getFlywheelState() {
//    return this.state;
//  }
//
//  /** @param state The state to switch the flywheel to. */
//  @Override
//  public void setFlywheelState(@NotNull final SubsystemFlywheel.FlywheelState state) {
//    if (this.state != FlywheelState.SPINNING_UP && state == FlywheelState.SPINNING_UP)
//      this.lastSpinUpTimeMS = Clock.currentTimeMillis();
//    this.state = state;
//  }

  /**
   * @return Expected time from setting a shooting state to being ready to fire, in seconds.
   */
  @Override
  @Log
  public double getSpinUpTimeSecs() {
    return this.spinUpTimeoutSecs;
  }

  @Override
  @Log
  public boolean isReadyToShoot() {
    if (Double.isNaN(this.targetSpeed)) return false;
    return this.speedConditionDebouncer.get();
  }

  @Log
  private boolean isAtShootingSpeed() {
    return SimUtil.getWithSimHelper(
        this.sim_manualStates != null && this.sim_manualStates.get(),
        true,
        this.sim_isAtSpeed,
        () -> {
          if (this.maxAbsSpeedError == null && this.maxRelSpeedError == null) return false;

          final double actualVelocity = this.shooterMotor.getVelocity();
          final double absSpeedDifference = Math.abs(Math.abs(actualVelocity) - this.targetSpeed);

          // TODO: Should we be looking at velocity or speed?
          if (this.maxAbsSpeedError != null)
            return absSpeedDifference <= this.maxAbsSpeedError;
          else
            return absSpeedDifference / targetSpeed <= this.maxRelSpeedError;
        });
  }

  /** @return true if the condition was met when cached, false otherwise */
  @Override
  @Log
  public boolean isConditionTrueCached() {
    return this.conditionMetCached;
  }

  /** Updates all cached values with current ones. */
  @Override
  public void update() {
    this.speedConditionDebouncer.update(this.isAtShootingSpeed());

    this.conditionMetCached = this.isConditionTrue();
  }

  @Override
  public @NotNull Optional<Double> getSpeed() {
    return Optional.of(Math.abs(this.shooterMotor.getVelocity()));
  }

  @Override
  public String configureLogName() {
    return this.getClass().getSimpleName() + this.shooterMotor.getPort();
  }
}
