package org.usfirst.frc.team449.robot.oi.fieldoriented;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.oi.OI;

/** An OI that gives an absolute heading, relative to the field, and a velocity. */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "@class")
public abstract class OIFieldOriented implements OI {

  /** The cached linear velocity. */
  private double cachedVel;

  /** The cached angular setpoint. */
  @Nullable private Double cachedTheta;

  /**
   * Get the absolute angle for the robot to move towards.
   *
   * @return An angular setpoint for the robot in degrees, where 0 is pointing at the other
   *     alliance's driver station and 90 is pointing at the left wall when looking out from the
   *     driver station. Returns null if vel is 0.
   */
  @Nullable
  public abstract Double getTheta();

  /**
   * Get the velocity for the robot to go at.
   *
   * @return A velocity from [-1, 1].
   */
  public abstract double getVel();

  /**
   * Get the cached absolute angle for the robot to move towards.
   *
   * @return An angular setpoint for the robot in degrees, where 0 is pointing at the other
   *     alliance's driver station and 90 is pointing at the left wall when looking out from the
   *     driver station. Returns null if vel is 0.
   */
  @Nullable
  @Log
  public Double getThetaCached() {
    return cachedTheta;
  }

  /**
   * Get the cached velocity for the robot to go at.
   *
   * @return A velocity from [-1, 1].
   */
  @Log
  public double getVelCached() {
    return cachedVel;
  }

  /** Updates all cached values with current ones. */
  @Override
  public void update() {
    cachedVel = getVel();
    cachedTheta = getTheta();
  }
}
