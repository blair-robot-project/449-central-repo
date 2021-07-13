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
import org.usfirst.frc.team449.robot.generalInterfaces.subsystems.SubsystemConditional;

import java.util.List;
import java.util.Optional;

/**
 * A cluster of flywheels that acts as a single flywheel. Use for systems with separate physical
 * flywheels that are controlled by separate motors.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class FlywheelCluster extends SubsystemBase implements SubsystemFlywheel, Loggable {
  @NotNull private final List<SubsystemFlywheel> flywheels;
  @Nullable private final Double maxAbsSpeedRange;
  @Nullable private final Double maxRelSpeedRange;
  @Log private double targetSpeed;
  private boolean conditionMetCached;

  /**
   * @param flywheels the flywheels that make up this cluster
   * @param maxAbsSpeedRange max range of speeds of flywheels in cluster (in the units of {@link
   *     SubsystemFlywheel#getSpeed()}) at which the cluster is ready to shoot; {@code null} to not
   *     impose such a requirement
   * @param maxRelSpeedRange Similar to {@code maxAbsSpeedRange}, but specified as a fraction of the
   *     mean speed. At most one of these two argumrelatients can be non-null.
   */
  @JsonCreator
  public FlywheelCluster(
      @NotNull @JsonProperty(required = true) final SubsystemFlywheel[] flywheels,
      @Nullable final Double maxAbsSpeedRange,
      @Nullable final Double maxRelSpeedRange) {

    if (maxAbsSpeedRange != null && maxRelSpeedRange != null)
      throw new IllegalArgumentException(
          "Can't specify both absolute and relative max speed range.");

    this.flywheels = List.of(flywheels);
    this.maxAbsSpeedRange = maxAbsSpeedRange;
    this.maxRelSpeedRange = maxRelSpeedRange;
  }

  /** Turn on each flywheel in the cluster to the speed passed to the constructor. */
  @Override
  public void turnFlywheelOn(final double speed) {
    this.targetSpeed = speed;

    this.flywheels.forEach(x -> x.turnFlywheelOn(speed));
  }

  /** Turn each flywheel in the cluster off. */
  @Override
  public void turnFlywheelOff() {
    this.targetSpeed = Double.NaN;

    this.flywheels.forEach(SubsystemFlywheel::turnFlywheelOff);
  }

  //  /** @return The current state of the cluster. */
  //  @Override
  //  public @NotNull FlywheelState getFlywheelState() {
  //    return this.state;
  //  }
  //
  //  /** @param state The state to switch the cluster to. */
  //  @Override
  //  public void setFlywheelState(@NotNull final FlywheelState state) {
  //    this.state = state;
  //    this.flywheels.forEach(x -> x.setFlywheelState(state));
  //  }

  /** @return Longest spin-up time of any flywheel in the cluster. */
  @Override
  public double getSpinUpTimeSecs() {
    return this.flywheels.stream()
        .mapToDouble(SubsystemFlywheel::getSpinUpTimeSecs)
        .max()
        .orElse(0);
  }

  /**
   * @return Whether all flywheels in the cluster individually are ready to shoot and the speed
   *     range requirement of the cluster, if active, is met.
   */
  @Override
  @Log
  public boolean isReadyToShoot() {
    if (!this.flywheels.stream().allMatch(SubsystemFlywheel::isReadyToShoot)) return false;
    return this.speedRangeRequirementMet();
  }

  @Log
  public boolean speedRangeRequirementMet() {
    if (this.maxRelSpeedRange == null && this.maxAbsSpeedRange == null) return true;

    double max = Double.NEGATIVE_INFINITY;
    double min = Double.POSITIVE_INFINITY;
    double sum = 0;
    int count = 0;

    for (final SubsystemFlywheel flywheel : this.flywheels) {
      final Optional<Double> speed = flywheel.getSpeed();
      if (speed.isPresent()) {
        final double value = speed.get();
        count++;
        sum += value;
        if (value > max) max = value;
        if (value < min) min = value;
      }
    }

    final double speedRange = max - min;
    if (this.maxAbsSpeedRange != null) {
      return speedRange <= this.maxAbsSpeedRange;
    } else {
      final double averageSpeed = sum / count;
      return speedRange / averageSpeed <= this.maxRelSpeedRange;
    }
  }

  /**
   * Returns mean speed of the flywheels in the cluster
   *
   * @return mean speed of the flywheels in the cluster
   */
  @Override
  public @NotNull Optional<Double> getSpeed() {
    double sum = 0;
    int count = 0;
    for (final SubsystemFlywheel flywheel : this.flywheels) {
      final Optional<Double> speed = flywheel.getSpeed();
      if (speed.isPresent()) {
        count++;
        sum += speed.get();
      }
    }
    return Optional.of(sum / count);
  }

  @Override
  public boolean isConditionTrue() {
    return this.isReadyToShoot();
  }

  @Override
  @Log
  public boolean isConditionTrueCached() {
    return this.conditionMetCached;
  }

  @Override
  public void update() {
    this.flywheels.forEach(SubsystemConditional::update);
    this.conditionMetCached = this.isConditionTrue();
  }
}
