package org.usfirst.frc.team449.robot.generalInterfaces.simpleMotor;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/** A motor with velocity/voltage control and the ability to enable and disable. */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "@class")
public interface SimpleMotor {

  /**
   * Set the velocity for the motor to go at.
   *
   * @param velocity the desired velocity, on [-1, 1].
   */
  void setVelocity(double velocity);

  /** Enables the motor, if applicable. */
  default void enable() {}

  /** Disables the motor, if applicable. */
  default void disable() {}

  enum Type {
    FPSTalon,
    Victor,
    VictorSPX,
  }
}
