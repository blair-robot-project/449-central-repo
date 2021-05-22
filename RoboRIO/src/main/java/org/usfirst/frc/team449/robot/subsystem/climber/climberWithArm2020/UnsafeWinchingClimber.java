package org.usfirst.frc.team449.robot.subsystem.climber.climberWithArm2020;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.updatable.Updatable;
import org.usfirst.frc.team449.robot.subsystem.binaryMotor.SubsystemBinaryMotor;
import org.usfirst.frc.team449.robot.subsystem.solenoid.SubsystemSolenoid;

import static org.usfirst.frc.team449.robot.other.Util.getLogPrefix;

/**
 * Like {@link SafeWinchingClimber} but without safety features.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class UnsafeWinchingClimber extends SubsystemBase
    implements SubsystemClimberWithArm,
    SubsystemBinaryMotor,
    SubsystemSolenoid,
    Updatable,
    Loggable {
  private final SubsystemBinaryMotor motorSubsystem;
  private final SubsystemSolenoid solenoidSubsystem;

  @JsonCreator
  public UnsafeWinchingClimber(
      @NotNull @JsonProperty(required = true) final SubsystemBinaryMotor motorSubsystem,
      @NotNull @JsonProperty(required = true) final SubsystemSolenoid solenoidSubsystem,
      final long extensionTimeMillis) {
    this.motorSubsystem = motorSubsystem;
    this.solenoidSubsystem = solenoidSubsystem;
  }

  /** Raise arm only if it is enabled */
  @Override
  public void raise() {
    System.out.println(getLogPrefix(this) + "raise");

    this.setSolenoid(DoubleSolenoid.Value.kForward);
  }

  /** Lower arm, but only if it is enabled */
  @Override
  public void lower() {
    System.out.println(getLogPrefix(this) + "lower");

    this.setSolenoid(DoubleSolenoid.Value.kReverse);
  }

  @Override
  public void off() {
    System.out.println(getLogPrefix(this) + "off");

    this.setSolenoid(DoubleSolenoid.Value.kOff);
    this.turnMotorOff();
  }

  @Override
  public void setSolenoid(@NotNull final DoubleSolenoid.Value value) {
    this.solenoidSubsystem.setSolenoid(value);
  }

  @Override
  public @NotNull DoubleSolenoid.Value getSolenoidPosition() {
    return this.solenoidSubsystem.getSolenoidPosition();
  }

  /**
   * Move the winch if the arm is up. Has to be called twice (double button press) for it to work (I
   * think?)
   */
  @Override
  public void turnMotorOn() {
    this.motorSubsystem.turnMotorOn();
  }


  /** Turn off the winch */
  @Override
  public void turnMotorOff() {
    this.motorSubsystem.turnMotorOff();
  }

  @Override
  @Log
  public boolean isMotorOn() {
    return this.motorSubsystem.isMotorOn();
  }

  @Override
  public void update() { }
}
