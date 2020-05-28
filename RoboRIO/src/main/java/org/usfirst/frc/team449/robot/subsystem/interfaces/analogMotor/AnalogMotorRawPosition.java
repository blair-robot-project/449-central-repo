package org.usfirst.frc.team449.robot.subsystem.interfaces.analogMotor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedTalon;

/** An analogMotor that uses position instead of velocity. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class AnalogMotorRawPosition extends SubsystemBase implements SubsystemAnalogMotor {

  /** The motor this subsystem controls. */
  @NotNull private final MappedTalon motor;

  /**
   * Default constructor.
   *
   * @param motor The motor this subsystem controls.
   */
  @JsonCreator
  public AnalogMotorRawPosition(@NotNull @JsonProperty(required = true) MappedTalon motor) {
    this.motor = motor;
  }

  /**
   * Set the setpoint to a given position.
   *
   * @param input The setpoint to give to the motor, in feet.
   */
  @Override
  public void set(double input) {
    motor.setPositionSetpoint(input);
  }

  /** Disable the motor. */
  @Override
  public void disable() {
    motor.disable();
  }
}
