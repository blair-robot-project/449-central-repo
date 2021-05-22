package org.usfirst.frc.team449.robot.generalInterfaces.motors.simple;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.usfirst.frc.team449.robot.subsystem.analogMotor.SubsystemAnalogMotor;

/** A motor with velocity/voltage control and the ability to enable and disable. */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "@class")
public interface SimpleMotor extends SubsystemAnalogMotor {
  /**
   * Set the velocity for the motor to go at.
   *
   * @param velocity the desired velocity, on [-1, 1].
   */
  void setVelocity(double velocity);

  /** Enables the motor, if applicable. */
  default void enable() {}

  /** Disables the motor, if applicable. */
  @Override
  default void disable() {}

  /**
   * Set output to a given input.
   *
   * @param input The input to give to the motor.
   * @deprecated use {@link SimpleMotor#setVelocity(double)} instead
   */
  @Override
  @Deprecated
  default void set(final double input) {
    this.setVelocity(input);
  }

  /** Unused. */
  enum Type {
    FPSTalon,
    Victor,
    VictorSPX,
  }
}
