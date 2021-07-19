package org.usfirst.frc.team449.robot.javamaps.builders;

import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable;
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable.PerGearSettings;
import org.usfirst.frc.team449.robot.jacksonWrappers.FeedForwardCalculators.MappedFeedForwardCalculator;

public final class PerGearSettingsBuilder {
  private int gearNum;
  private Shiftable.Gear gear;
  private Double fwdPeakOutputVoltage;
  private Double revPeakOutputVoltage;
  private Double fwdNominalOutputVoltage;
  private Double revNominalOutputVoltage;
  private MappedFeedForwardCalculator feedForwardCalculator;
  private Double rampRate;
  private Double maxSpeed;
  private Double postEncoderGearing;
  private double kP;
  private double kI;
  private double kD;
  private double posKP;
  private double posKI;
  private double posKD;

  public PerGearSettingsBuilder copy() {
    return new PerGearSettingsBuilder()
        .gearNum(gearNum)
        .gear(gear)
        .fwdPeakOutputVoltage(fwdPeakOutputVoltage)
        .revPeakOutputVoltage(revPeakOutputVoltage)
        .fwdNominalOutputVoltage(fwdNominalOutputVoltage)
        .revNominalOutputVoltage(revNominalOutputVoltage)
        .feedForwardCalculator(feedForwardCalculator)
        .rampRate(rampRate)
        .maxSpeed(maxSpeed)
        .postEncoderGearing(postEncoderGearing)
        .kP(kP)
        .kI(kI)
        .kD(kD)
        .posKP(posKP)
        .posKI(posKI)
        .posKD(posKD);
  }

  public Shiftable.PerGearSettings build() {
    return new Shiftable.PerGearSettings(
        gearNum,
        gear,
        fwdPeakOutputVoltage,
        revPeakOutputVoltage,
        fwdNominalOutputVoltage,
        revNominalOutputVoltage,
        feedForwardCalculator,
        rampRate,
        maxSpeed,
        postEncoderGearing,
        kP,
        kI,
        kD,
        posKP,
        posKI,
        posKD);
  }

  public PerGearSettingsBuilder gearNum(int gearNum) {
    this.gearNum = gearNum;
    return this;
  }

  public PerGearSettingsBuilder gear(Shiftable.Gear gear) {
    this.gear = gear;
    return this;
  }

  public PerGearSettingsBuilder fwdPeakOutputVoltage(Double fwdPeakOutputVoltage) {
    this.fwdPeakOutputVoltage = fwdPeakOutputVoltage;
    return this;
  }

  public PerGearSettingsBuilder revPeakOutputVoltage(Double revPeakOutputVoltage) {
    this.revPeakOutputVoltage = revPeakOutputVoltage;
    return this;
  }

  public PerGearSettingsBuilder fwdNominalOutputVoltage(Double fwdNominalOutputVoltage) {
    this.fwdNominalOutputVoltage = fwdNominalOutputVoltage;
    return this;
  }

  public PerGearSettingsBuilder revNominalOutputVoltage(Double revNominalOutputVoltage) {
    this.revNominalOutputVoltage = revNominalOutputVoltage;
    return this;
  }

  public PerGearSettingsBuilder feedForwardCalculator(
      MappedFeedForwardCalculator feedForwardCalculator) {
    this.feedForwardCalculator = feedForwardCalculator;
    return this;
  }

  public PerGearSettingsBuilder rampRate(Double rampRate) {
    this.rampRate = rampRate;
    return this;
  }

  public PerGearSettingsBuilder maxSpeed(Double maxSpeed) {
    this.maxSpeed = maxSpeed;
    return this;
  }

  public PerGearSettingsBuilder postEncoderGearing(Double postEncoderGearing) {
    this.postEncoderGearing = postEncoderGearing;
    return this;
  }

  public PerGearSettingsBuilder kP(double kP) {
    this.kP = kP;
    return this;
  }

  public PerGearSettingsBuilder kI(double kI) {
    this.kI = kI;
    return this;
  }

  public PerGearSettingsBuilder kD(double kD) {
    this.kD = kD;
    return this;
  }

  public PerGearSettingsBuilder posKP(double posKP) {
    this.posKP = posKP;
    return this;
  }

  public PerGearSettingsBuilder posKI(double posKI) {
    this.posKI = posKI;
    return this;
  }

  public PerGearSettingsBuilder posKD(double posKD) {
    this.posKD = posKD;
    return this;
  }
}
