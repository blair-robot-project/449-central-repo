package org.usfirst.frc.team449.robot.analogMotor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.subsystem.binaryMotor.SubsystemBinaryMotor;

/**
 * Adapts a {@link SubsystemAnalogMotor} to be a {@link SubsystemBinaryMotor} by running at a
 * constructor-specified speed.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class AnalogMotorSingleSpeed extends SubsystemBase
    implements SubsystemBinaryMotor, Loggable {
  private final SubsystemAnalogMotor motor;
  private final double speed;
  @Log private boolean isMotorOn;

  @JsonCreator
  public AnalogMotorSingleSpeed(
      @NotNull @JsonProperty(required = true) final SubsystemAnalogMotor motor,
      @JsonProperty(required = true) final double speed) {
    this.motor = motor;
    this.speed = speed;
  }

  /** Turns the motor on, and sets it to a map-specified speed. */
  @Override
  public void turnMotorOn() {
    this.isMotorOn = true;
    this.motor.set(this.speed);
  }

  /** Turns the motor off. */
  @Override
  public void turnMotorOff() {
    this.isMotorOn = false;
    this.motor.disable();
  }

  /** @return true if the motor is on, false otherwise. */
  @Override
  public boolean isMotorOn() {
    return this.isMotorOn;
  }
}
