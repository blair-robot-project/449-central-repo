package org.usfirst.frc.team449.robot.subsystem.complex.binaryMotor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.simpleMotor.SimpleMotor;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedDoubleSolenoid;
import org.usfirst.frc.team449.robot.subsystem.interfaces.binaryMotor.SubsystemBinaryMotor;
import org.usfirst.frc.team449.robot.subsystem.interfaces.solenoid.SubsystemSolenoid;

/** A subsystem with a solenoid and a motor that can be turned on and off. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SubsystemBinaryMotorSolenoid extends SubsystemBase
    implements SubsystemBinaryMotor, SubsystemSolenoid, Loggable {

  /** The motor that this subsystem controls. */
  @NotNull private final SimpleMotor motor;

  /** The velocity to run the motor at, on [-1, 1]. */
  private final double velocity;
  /** The piston that this subsystem controls. */
  @NotNull private final DoubleSolenoid piston;
  /** Whether or not the motor is currently on. */
  private boolean motorOn;
  /** The current position of the piston. */
  @NotNull private DoubleSolenoid.Value pistonPos;

  /**
   * Default constructor.
   *
   * @param motor The motor that this subsystem controls.
   * @param velocity The velocity to run the motor at, on [-1, 1]. Defaults to 1.
   * @param piston The piston that this subsystem controls.
   */
  @JsonCreator
  public SubsystemBinaryMotorSolenoid(
      @NotNull @JsonProperty(required = true) SimpleMotor motor,
      @Nullable Double velocity,
      @NotNull @JsonProperty(required = true) MappedDoubleSolenoid piston) {
    this.motor = motor;
    this.velocity = velocity != null ? velocity : 1;
    this.piston = piston;
    motorOn = false;
    pistonPos = DoubleSolenoid.Value.kOff;
  }

  /** Turns the motor on, and sets it to a map-specified speed. */
  @Override
  public void turnMotorOn() {
    motor.setVelocity(velocity);
    motorOn = true;
  }

  /** Turns the motor off. */
  @Override
  public void turnMotorOff() {
    motor.setVelocity(0);
    motorOn = false;
  }

  /** @return true if the motor is on, false otherwise. */
  @Log
  @Override
  public boolean isMotorOn() {
    return motorOn;
  }

  /** @param value The position to set the solenoid to. */
  @Override
  public void setSolenoid(@NotNull DoubleSolenoid.Value value) {
    pistonPos = value;
    piston.set(value);
  }

  /** @return the current position of the solenoid. */
  @Override
  @NotNull
  @Log
  public DoubleSolenoid.Value getSolenoidPosition() {
    return pistonPos;
  }
}
