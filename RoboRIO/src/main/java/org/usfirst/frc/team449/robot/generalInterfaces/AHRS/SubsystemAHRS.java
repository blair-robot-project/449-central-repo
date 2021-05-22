package org.usfirst.frc.team449.robot.generalInterfaces.AHRS;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/** A subsystem that has a AHRS on it. */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
public interface SubsystemAHRS {

  /**
   * Get the robot's heading.
   *
   * @return robot heading, in degrees, on [-180, 180].
   */
  double getHeading();

  /**
   * Set the robot's heading.
   *
   * @param heading The heading to set to, in degrees on [-180, 180].
   */
  void setHeading(double heading);

  /**
   * Get the robot's cached heading.
   *
   * @return robot heading, in degrees, on [-180, 180].
   */
  double getHeadingCached();

  /**
   * Get the robot's angular velocity.
   *
   * @return Angular velocity in degrees/sec
   */
  double getAngularVel();

  /**
   * Get the robot's cached angular velocity.
   *
   * @return Angular velocity in degrees/sec
   */
  double getAngularVelCached();

  /**
   * Get the robot's angular displacement since being turned on.
   *
   * @return Angular displacement in degrees.
   */
  double getAngularDisplacement();

  /**
   * Get the robot's cached angular displacement since being turned on.
   *
   * @return Angular displacement in degrees.
   */
  double getAngularDisplacementCached();

  /**
   * Get the pitch value.
   *
   * @return The pitch, in degrees from [-180, 180]
   */
  double getPitch();

  /**
   * Get the cached pitch value.
   *
   * @return The pitch, in degrees from [-180, 180]
   */
  double getCachedPitch();

  /** @return true if the gyroscope is currently overriden, false otherwise. */
  boolean getOverrideGyro();

  /** @param override true to override the gyro, false to un-override it. */
  void setOverrideGyro(boolean override);
}
