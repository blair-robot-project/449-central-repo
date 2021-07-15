package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.revrobotics.CANDigitalInput;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.motors.SlaveMotor;

public class SlaveSparkMax implements SlaveMotor, Loggable {

  CANSparkMax slaveSpark;

  PDP PDP;

  boolean inverted;

  @JsonCreator
  public SlaveSparkMax(
      @JsonProperty(required = true) final int port,
      @Nullable final Boolean inverted,
      @Nullable final PDP PDP) {

    this.slaveSpark = new CANSparkMax(port, CANSparkMaxLowLevel.MotorType.kBrushless);

    this.inverted = inverted == null ? false : inverted;

    this.slaveSpark
        .getForwardLimitSwitch(CANDigitalInput.LimitSwitchPolarity.kNormallyOpen)
        .enableLimitSwitch(false);
    this.slaveSpark
        .getReverseLimitSwitch(CANDigitalInput.LimitSwitchPolarity.kNormallyOpen)
        .enableLimitSwitch(false);

    this.slaveSpark.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus0, 100);
    this.slaveSpark.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus1, 100);
    this.slaveSpark.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus2, 100);

    this.PDP = PDP;
  }

  public void setMasterSpark(final CANSparkMax masterController, final boolean brakeMode) {
    this.slaveSpark.follow(masterController, this.inverted);
    this.slaveSpark.setIdleMode(
        brakeMode ? CANSparkMax.IdleMode.kBrake : CANSparkMax.IdleMode.kCoast);
  }

  public void setMasterPhoenix(final int masterPort, final boolean brakeMode) {
    this.slaveSpark.follow(CANSparkMax.ExternalFollower.kFollowerPhoenix, masterPort);
    this.slaveSpark.setIdleMode(
        brakeMode ? CANSparkMax.IdleMode.kBrake : CANSparkMax.IdleMode.kCoast);
    this.slaveSpark.setInverted(this.inverted);
  }

  @Log
  public double getOutputCurrent() {
    return this.slaveSpark.getOutputCurrent();
  }

  @Log
  public double getMotorOutputVoltage() {
    return this.slaveSpark.getAppliedOutput();
  }
}
