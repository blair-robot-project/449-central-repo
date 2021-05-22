package org.usfirst.frc.team449.robot.climber.climberWithArm2020;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/** A climber subsystem that has an arm that can be raised and lowered. */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "@class")
public interface SubsystemClimberWithArm {
  /** Raises the climber arm. */
  void raise();

  /** Lowers the climber arm. */
  void lower();

  /** Turns off all devices associated with the climber arm. */
  void off();

  enum ArmState {
    UP,
    DOWN
  }
}
