package org.usfirst.frc.team449.robot.subsystem.climber.climberWithArm2020;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.oblarg.oblog.Loggable;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.subsystem.binaryMotor.SubsystemBinaryMotor;
import org.usfirst.frc.team449.robot.subsystem.solenoid.SubsystemSolenoid;

import static org.usfirst.frc.team449.robot.other.Util.getLogPrefix;

/**
 * A climber subsystem that has an arm that is raised pneumatically and lowered with force by means
 * of a winch.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ClimberWinchingWithArm extends SubsystemBase
    implements SubsystemClimberWithArm, SubsystemBinaryMotor, SubsystemSolenoid, Loggable {
  @NotNull private final ClimberCurrentLimited motorSubsystem;

  @NotNull private final SubsystemSolenoid solenoidSubsystem;

  @NotNull public ArmState armState = ArmState.DOWN;

  private boolean manualControlEnabled = false;

  /**
   * Default constructor.
   *
   * @param motorSubsystem The motor used to winch when climbing.
   * @param solenoidSubsystem The piston used to raise the arm.
   */
  @JsonCreator
  public ClimberWinchingWithArm(
      @NotNull @JsonProperty(required = true) ClimberCurrentLimited motorSubsystem,
      @NotNull @JsonProperty(required = true) SubsystemSolenoid solenoidSubsystem) {
    this.motorSubsystem = motorSubsystem;
    this.solenoidSubsystem = solenoidSubsystem;
  }

  /** Raises the arm by means of the solenoid subsystem and turns off the motor subsystem. */
  @Override
  public void raise() {
    System.out.println(getLogPrefix(this) + "raise");

    this.turnMotorOff();
    this.setSolenoid(DoubleSolenoid.Value.kForward);
    armState = ArmState.UP;
  }

  /**
   * Turns off the solenoid subsystem and turns on the motor subsystem. The subsystem automatically
   * turns the motor off when the motor encounters resistance from the arm reaching its lowermost
   * position.
   */
  @Override
  public void lower() {
    System.out.println(getLogPrefix(this) + "lower");

    this.setSolenoid(DoubleSolenoid.Value.kOff);
    this.turnMotorOn();
    armState = ArmState.DOWN;
  }

  /** Turns both solenoid and motor subsystems off. */
  @Override
  public void off() {
    System.out.println(getLogPrefix(this) + "off");

    this.turnMotorOff();
    this.setSolenoid(DoubleSolenoid.Value.kOff);
  }

  /** Turns the motor subsystem on. */
  @Override
  public void turnMotorOn() {
    this.motorSubsystem.turnMotorOn();
  }

  /** Turns the motor subsystem off. */
  @Override
  public void turnMotorOff() {
    this.motorSubsystem.turnMotorOff();
  }

  /**
   * Turn the motor on if it is enabled
   *
   * @return Whether or not the winch up command is enabled
   */
  public boolean winchUpIfEnabled() {
    if (winchUpEnabled()) {
      turnMotorOn();
      return true;
    }
    return false;
  }

  /** @return true if the motor subsystem is on, false otherwise. */
  @Override
  public boolean isMotorOn() {
    return this.motorSubsystem.isMotorOn();
  }

  /** @param value The position to set the solenoid subsystem to. */
  @Override
  public void setSolenoid(DoubleSolenoid.@NotNull Value value) {
    this.solenoidSubsystem.setSolenoid(value);
  }

  /** @return the current position of the solenoid subsystem. */
  @Override
  public @NotNull DoubleSolenoid.Value getSolenoidPosition() {
    return this.solenoidSubsystem.getSolenoidPosition();
  }

  /** Whether or not the winch up command is enabled */
  public boolean winchUpEnabled() {
    return manualControlEnabled || armState == ArmState.UP;
  }

  /**
   * Set the manualControlEnabled field
   *
   * @param manualControlEnabled Whether or not manual control should be enabled
   */
  public void setManualControl(boolean manualControlEnabled) {
    this.manualControlEnabled = manualControlEnabled;
  }

  /** Whether or not manual control is enabled */
  public boolean isManualControlEnabled() {
    return manualControlEnabled;
  }
}
