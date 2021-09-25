package org.usfirst.frc.team449.robot.javaMaps.builders;

import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import org.usfirst.frc.team449.robot.components.RunningLinRegComponent;
import org.usfirst.frc.team449.robot.generalInterfaces.motors.smart.SmartMotor;
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable;
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable.PerGearSettings;
import org.usfirst.frc.team449.robot.jacksonWrappers.PDP;
import org.usfirst.frc.team449.robot.jacksonWrappers.SlaveSparkMax;
import org.usfirst.frc.team449.robot.jacksonWrappers.SlaveTalon;
import org.usfirst.frc.team449.robot.jacksonWrappers.SlaveVictor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SmartMotorBuilder {
  private SmartMotor.Type type;
  private int port;
  private boolean enableBrakeMode;
  private String name;
  private boolean reverseOutput;
  private PDP pdp;
  private Boolean fwdLimitSwitchNormallyOpen;
  private Boolean revLimitSwitchNormallyOpen;
  private Integer remoteLimitSwitchID;
  private Double fwdSoftLimit;
  private Double revSoftLimit;
  private Double postEncoderGearing;
  private Double unitPerRotation;
  private Integer currentLimit;
  private boolean enableVoltageComp;
  private List<PerGearSettings> perGearSettings = new ArrayList<>();
  private Shiftable.Gear startingGear;
  private Integer startingGearNum;
  // Spark-specific
  private Integer controlFrameRateMillis;
  // Talon-specific
  private Map<ControlFrame, Integer> controlFrameRatesMillis = new HashMap<>();
  private RunningLinRegComponent voltagePerCurrentLinReg;
  private Integer voltageCompSamples;
  private FeedbackDevice feedbackDevice;
  private Integer encoderCPR;
  private Boolean reverseSensor;
  private Double updaterProcessPeriodSecs;
  private List<SlaveTalon> slaveTalons = new ArrayList<>();
  private List<SlaveVictor> slaveVictors = new ArrayList<>();
  private List<SlaveSparkMax> slaveSparks = new ArrayList<>();
  // Handled specially.;
  private Map<?, Integer> statusFrameRatesMillis;

  private boolean portSet = false;
  private boolean enableBrakeModeSet = false;

  public SmartMotor build() {
    assert type != null : "SmartMotor type was not given";
    assert portSet : "SmartMotor port was not set";
    assert enableBrakeModeSet : "SmartMotor property enableBrakeMode was not given";

    return SmartMotor.create(
        type,
        port,
        enableBrakeMode,
        name,
        reverseOutput,
        pdp,
        fwdLimitSwitchNormallyOpen,
        revLimitSwitchNormallyOpen,
        remoteLimitSwitchID,
        fwdSoftLimit,
        revSoftLimit,
        postEncoderGearing,
        unitPerRotation,
        currentLimit,
        enableVoltageComp,
        perGearSettings,
        startingGear,
        startingGearNum,
        controlFrameRateMillis,
        controlFrameRatesMillis,
        voltagePerCurrentLinReg,
        voltageCompSamples,
        feedbackDevice,
        encoderCPR,
        reverseSensor,
        updaterProcessPeriodSecs,
        slaveTalons,
        slaveVictors,
        slaveSparks,
        statusFrameRatesMillis);
  }

  public SmartMotorBuilder copy() {
    var builder = new SmartMotorBuilder();

    if (type != null) builder.type(this.type);
    if (portSet) builder.port(this.port);
    if (enableBrakeModeSet) builder.enableBrakeMode(this.enableBrakeMode);

    return builder.name(this.name)
        .reverseOutput(this.reverseOutput)
        .pdp(this.pdp)
        .fwdLimitSwitchNormallyOpen(this.fwdLimitSwitchNormallyOpen)
        .revLimitSwitchNormallyOpen(this.revLimitSwitchNormallyOpen)
        .remoteLimitSwitchID(this.remoteLimitSwitchID)
        .fwdSoftLimit(this.fwdSoftLimit)
        .revSoftLimit(this.revSoftLimit)
        .postEncoderGearing(this.postEncoderGearing)
        .unitPerRotation(this.unitPerRotation)
        .currentLimit(this.currentLimit)
        .enableVoltageComp(this.enableVoltageComp)
        .perGearSettings(this.perGearSettings)
        .startingGear(this.startingGear)
        .startingGearNum(this.startingGearNum)
        .controlFrameRateMillis(this.controlFrameRateMillis)
        .controlFrameRatesMillis(this.controlFrameRatesMillis)
        .voltagePerCurrentLinReg(this.voltagePerCurrentLinReg)
        .voltageCompSamples(this.voltageCompSamples)
        .feedbackDevice(this.feedbackDevice)
        .encoderCPR(this.encoderCPR)
        .reverseSensor(this.reverseSensor)
        .updaterProcessPeriodSecs(this.updaterProcessPeriodSecs)
        .slaveTalons(this.slaveTalons)
        .slaveVictors(this.slaveVictors)
        .slaveSparks(this.slaveSparks)
        .statusFrameRatesMillis(this.statusFrameRatesMillis);
  }

  public SmartMotorBuilder type(SmartMotor.Type type) {
    this.type = type;
    return this;
  }

  public SmartMotorBuilder port(int port) {
    this.port = port;
    this.portSet = true;
    return this;
  }

  public SmartMotorBuilder enableBrakeMode(boolean enableBrakeMode) {
    this.enableBrakeMode = enableBrakeMode;
    this.enableBrakeModeSet = true;
    return this;
  }

  public SmartMotorBuilder name(String name) {
    this.name = name;
    return this;
  }

  public SmartMotorBuilder reverseOutput(boolean reverseOutput) {
    this.reverseOutput = reverseOutput;
    return this;
  }

  public SmartMotorBuilder pdp(PDP pdp) {
    this.pdp = pdp;
    return this;
  }

  public SmartMotorBuilder fwdLimitSwitchNormallyOpen(Boolean fwdLimitSwitchNormallyOpen) {
    this.fwdLimitSwitchNormallyOpen = fwdLimitSwitchNormallyOpen;
    return this;
  }

  public SmartMotorBuilder revLimitSwitchNormallyOpen(Boolean revLimitSwitchNormallyOpen) {
    this.revLimitSwitchNormallyOpen = revLimitSwitchNormallyOpen;
    return this;
  }

  public SmartMotorBuilder remoteLimitSwitchID(Integer remoteLimitSwitchID) {
    this.remoteLimitSwitchID = remoteLimitSwitchID;
    return this;
  }

  public SmartMotorBuilder fwdSoftLimit(Double fwdSoftLimit) {
    this.fwdSoftLimit = fwdSoftLimit;
    return this;
  }

  public SmartMotorBuilder revSoftLimit(Double revSoftLimit) {
    this.revSoftLimit = revSoftLimit;
    return this;
  }

  public SmartMotorBuilder postEncoderGearing(Double postEncoderGearing) {
    this.postEncoderGearing = postEncoderGearing;
    return this;
  }

  public SmartMotorBuilder unitPerRotation(Double unitPerRotation) {
    this.unitPerRotation = unitPerRotation;
    return this;
  }

  public SmartMotorBuilder currentLimit(Integer currentLimit) {
    this.currentLimit = currentLimit;
    return this;
  }

  public SmartMotorBuilder enableVoltageComp(boolean enableVoltageComp) {
    this.enableVoltageComp = enableVoltageComp;
    return this;
  }

  public SmartMotorBuilder perGearSettings(List<PerGearSettings> perGearSettings) {
    this.perGearSettings = perGearSettings;
    return this;
  }

  public SmartMotorBuilder startingGear(Shiftable.Gear startingGear) {
    this.startingGear = startingGear;
    return this;
  }

  public SmartMotorBuilder startingGearNum(Integer startingGearNum) {
    this.startingGearNum = startingGearNum;
    return this;
  }

  public SmartMotorBuilder controlFrameRateMillis(Integer controlFrameRateMillis) {
    this.controlFrameRateMillis = controlFrameRateMillis;
    return this;
  }

  public SmartMotorBuilder controlFrameRatesMillis(
      Map<ControlFrame, Integer> controlFrameRatesMillis) {
    this.controlFrameRatesMillis = controlFrameRatesMillis;
    return this;
  }

  public SmartMotorBuilder voltagePerCurrentLinReg(RunningLinRegComponent voltagePerCurrentLinReg) {
    this.voltagePerCurrentLinReg = voltagePerCurrentLinReg;
    return this;
  }

  public SmartMotorBuilder voltageCompSamples(Integer voltageCompSamples) {
    this.voltageCompSamples = voltageCompSamples;
    return this;
  }

  public SmartMotorBuilder feedbackDevice(FeedbackDevice feedbackDevice) {
    this.feedbackDevice = feedbackDevice;
    return this;
  }

  public SmartMotorBuilder encoderCPR(Integer encoderCPR) {
    this.encoderCPR = encoderCPR;
    return this;
  }

  public SmartMotorBuilder reverseSensor(Boolean reverseSensor) {
    this.reverseSensor = reverseSensor;
    return this;
  }

  public SmartMotorBuilder updaterProcessPeriodSecs(Double updaterProcessPeriodSecs) {
    this.updaterProcessPeriodSecs = updaterProcessPeriodSecs;
    return this;
  }

  public SmartMotorBuilder slaveTalons(List<SlaveTalon> slaveTalons) {
    this.slaveTalons = slaveTalons;
    return this;
  }

  public SmartMotorBuilder slaveVictors(List<SlaveVictor> slaveVictors) {
    this.slaveVictors = slaveVictors;
    return this;
  }

  public SmartMotorBuilder slaveSparks(List<SlaveSparkMax> slaveSparks) {
    this.slaveSparks = slaveSparks;
    return this;
  }

  public SmartMotorBuilder statusFrameRatesMillis(Map<?, Integer> statusFrameRatesMillis) {
    this.statusFrameRatesMillis = statusFrameRatesMillis;
    return this;
  }
}
