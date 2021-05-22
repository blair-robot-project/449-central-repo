package org.usfirst.frc.team449.robot.subsystem.climber.climberWithArm2020;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.motors.simple.SimpleMotor;
import org.usfirst.frc.team449.robot.generalInterfaces.motors.smart.SmartMotor;
import org.usfirst.frc.team449.robot.generalInterfaces.subsystems.SubsystemConditional;
import org.usfirst.frc.team449.robot.other.Debouncer;
import org.usfirst.frc.team449.robot.subsystem.binaryMotor.SubsystemBinaryMotor;

/** A climber subsystem that uses power monitoring to stop climbing. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ClimberCurrentLimited extends SubsystemBase
    implements SubsystemBinaryMotor, SubsystemConditional, Loggable {

  /** The controller for one of the climber motors. */
  @NotNull private final SmartMotor smartMotor;

  /** The other climber motor. */
  @Nullable private final SimpleMotor simpleMotor;

  /** The maximum allowable power before we stop the motor. */
  private final double maxPower;

  /**
   * The bufferTimer controlling how long we can be above the power limit before we stop climbing.
   */
  @NotNull private final Debouncer powerLimitTimer;
  private final double percentVoltage;
  /** Whether or not the motor is currently spinning. */
  private boolean motorSpinning;
  /** Whether the condition was met last time caching was done. */
  private boolean conditionMetCached;

  /**
   * Default constructor
   *
   * @param smartMotor The controller for one of the climber motors.
   * @param maxPower The maximum power at which the motor won't shut off.
   * @param simpleMotor The other climber motor. Can be null.
   * @param powerLimitTimer The buffer timer for the power-limited shutoff.
   * @param percentVoltage The voltage setpoint to run the motors at.
   */
  @JsonCreator
  public ClimberCurrentLimited(
      @NotNull @JsonProperty(required = true) final SmartMotor smartMotor,
      @NotNull @JsonProperty(required = true) final Debouncer powerLimitTimer,
      @JsonProperty(required = true) final double maxPower,
      @Nullable final SimpleMotor simpleMotor,
      @Nullable final Double percentVoltage) {
    // Instantiate things
    this.smartMotor = smartMotor;
    this.maxPower = maxPower;
    this.powerLimitTimer = powerLimitTimer;
    this.simpleMotor = simpleMotor;
    this.motorSpinning = false;
    this.percentVoltage = percentVoltage != null ? percentVoltage : 1;
  }

  /**
   * Set the percent voltage to be given to the motor.
   *
   * @param percentVbus The voltage to give the motor, from -1 to 1.
   */
  private void setPercentVbus(final double percentVbus) {
    smartMotor.setPercentVoltage(percentVbus);
    if (simpleMotor != null) {
      simpleMotor.setVelocity(percentVbus);
    }
  }

  /** Turns the motor on, and sets it to a map-specified speed. */
  @Override
  public void turnMotorOn() {
    smartMotor.enable();
    setPercentVbus(percentVoltage);
    motorSpinning = true;
  }

  /** Turns the motor off. */
  @Override
  public void turnMotorOff() {
    setPercentVbus(0);
    smartMotor.disable();
    motorSpinning = false;
  }

  /** @return true if the motor is on, false otherwise. */
  @Override
  @Log
  public boolean isMotorOn() {
    return motorSpinning;
  }

  /** @return true if the condition is met, false otherwise */
  @Override
  public boolean isConditionTrue() {
    return powerLimitTimer.get(
        Math.abs(smartMotor.getOutputCurrent() * smartMotor.getOutputVoltage()) > maxPower);
  }

  /** @return true if the condition was met when cached, false otherwise */
  @Override
  @Log
  public boolean isConditionTrueCached() {
    return conditionMetCached;
  }

  /** Updates all cached values with current ones. */
  @Override
  public void update() {
    conditionMetCached = isConditionTrue();
  }
}
