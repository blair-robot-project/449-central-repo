package org.usfirst.frc.team449.robot.subsystem.complex.shooter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.SmartMotor;
import org.usfirst.frc.team449.robot.generalInterfaces.simpleMotor.SimpleMotor;
import org.usfirst.frc.team449.robot.other.Clock;
import org.usfirst.frc.team449.robot.subsystem.interfaces.conditional.SubsystemConditional;
import org.usfirst.frc.team449.robot.subsystem.interfaces.flywheel.SubsystemFlywheel;

/** A flywheel multiSubsystem with a single flywheel and a single-motor feeder system. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class LoggingFlywheel extends SubsystemBase
    implements SubsystemFlywheel, SubsystemConditional, io.github.oblarg.oblog.Loggable {

  /** The flywheel's Talon */
  @NotNull private final SmartMotor shooterMotor;

  @NotNull private final SmartMotor otherShooterMotor;

  /** The feeder's motor */
  @NotNull private final SimpleMotor kickerMotor;

  /** How fast to run the feeder, from [-1, 1] */
  private final double kickerThrottle;

  /** Throttle at which to run the multiSubsystem, from [-1, 1] */
  private final double shooterThrottle;

  /** Time from giving the multiSubsystem voltage to being ready to fire, in seconds. */
  private final double spinUpTimeoutSecs;

  @Nullable private final Double minShootingSpeedFPS;

  /** Whether the flywheel is currently commanded to spin */
  @NotNull private SubsystemFlywheel.FlywheelState state;

  /** Whether the condition was met last time caching was done. */
  private boolean conditionMetCached;

  private double lastSpinUpTimeMS;

  /**
   * Default constructor
   *
   * @param shooterMotor The motor controlling the flywheel.
   * @param shooterThrottle The throttle, from [-1, 1], at which to run the multiSubsystem.
   * @param kickerMotor The motor controlling the feeder.
   * @param kickerThrottle The throttle, from [-1, 1], at which to run the feeder.
   * @param spinUpTimeoutSecs The amount of time, in seconds, it takes for the multiSubsystem to get
   *     up to speed. Defaults to {@literal 0}.
   * @param minShootingSpeedFPS The speed, in feet per second, at which the flywheel nominally
   *     shoots. Defaults to {@literal null}, meaning that there is no speed requirement.
   */
  @JsonCreator
  public LoggingFlywheel(
      @NotNull @JsonProperty(required = true) SmartMotor shooterMotor,
      @NotNull @JsonProperty(required = true) SmartMotor otherShooterMotor,
      @JsonProperty(required = true) double shooterThrottle,
      @NotNull @JsonProperty(required = true) SimpleMotor kickerMotor,
      @JsonProperty(required = true) double kickerThrottle,
      @JsonProperty(required = true) double spinUpTimeoutSecs,
      @Nullable Double minShootingSpeedFPS) {
    this.shooterMotor = shooterMotor;
    this.otherShooterMotor = otherShooterMotor;
    this.shooterThrottle = shooterThrottle;
    this.kickerMotor = kickerMotor;
    this.kickerThrottle = kickerThrottle;
    this.spinUpTimeoutSecs = spinUpTimeoutSecs;
    this.minShootingSpeedFPS = minShootingSpeedFPS;

    this.state = FlywheelState.OFF;
  }

  /** Turn the multiSubsystem on to a map-specified speed. */
  @Override
  public void turnFlywheelOn() {
    this.shooterMotor.enable();
    this.otherShooterMotor.enable();
    this.shooterMotor.setVelocity(this.shooterThrottle);
    this.otherShooterMotor.setVelocity(this.shooterThrottle);
  }

  /** Turn the multiSubsystem off. */
  @Override
  public void turnFlywheelOff() {
    this.shooterMotor.disable();
    this.otherShooterMotor.disable();
  }

  /** Start feeding balls into the multiSubsystem. */
  @Override
  public void turnFeederOn() {
    this.kickerMotor.enable();
    this.kickerMotor.setVelocity(this.kickerThrottle);
  }

  /** Stop feeding balls into the multiSubsystem. */
  @Override
  public void turnFeederOff() {
    this.kickerMotor.disable();
  }

  /** @return The current state of the multiSubsystem. */
  @NotNull
  @Override
  public SubsystemFlywheel.FlywheelState getFlywheelState() {
    return this.state;
  }

  /** @param state The state to switch the multiSubsystem to. */
  @Override
  public void setFlywheelState(@NotNull final SubsystemFlywheel.FlywheelState state) {
    this.state = state;
    if (state == FlywheelState.SPINNING_UP) this.lastSpinUpTimeMS = Clock.currentTimeMillis();
  }

  @Log
  public String state() {
    return this.state.name();
  }

  /**
   * @return Expected time from giving the multiSubsystem voltage to being ready to fire, in
   *     seconds.
   */
  @Override
  @Log
  public double getSpinUpTimeoutSecs() {
    return this.spinUpTimeoutSecs;
  }

  // TODO: Also account for speed difference between flywheels?
  // TODO: Split into FlywheelTwoSides like how intake does it?
  @Override
  @Log
  public boolean isAtShootingSpeed() {
    if (this.state == FlywheelState.OFF) return false;

    final double timeSinceLastSpinUp = Clock.currentTimeMillis() - this.lastSpinUpTimeMS;
    final boolean timeoutExceeded = timeSinceLastSpinUp > 1000 * this.spinUpTimeoutSecs;
    if (timeoutExceeded) return true;

    if (this.minShootingSpeedFPS == null) return false;

    final Double actualVelocity = this.shooterMotor.getVelocity();
    // TODO: Should we be looking at velocity or speed?
    return !Double.isNaN(actualVelocity) && Math.abs(actualVelocity) > this.minShootingSpeedFPS;
  }

  /** @return true if the condition is met, false otherwise */
  @Override
  public boolean isConditionTrue() {
    return this.isAtShootingSpeed();
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
    this.conditionMetCached = this.isConditionTrue();
  }
}
