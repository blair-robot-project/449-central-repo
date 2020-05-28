package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.revrobotics.CANDigitalInput;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.ControlType;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import io.github.oblarg.oblog.annotations.Log;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.SmartMotor;
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable;

@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MappedSparkMax implements SmartMotor {
  /** The PDP this Spark is connected to. */
  @Nullable @Log.Exclude protected final org.usfirst.frc.team449.robot.jacksonWrappers.PDP PDP;
  /** The counts per rotation of the encoder being used, or null if there is no encoder. */
  @Nullable private final Integer encoderCPR;
  /**
   * The coefficient the output changes by after being measured by the encoder, e.g. this would be
   * 1/70 if there was a 70:1 gearing between the encoder and the final output.
   */
  private final double postEncoderGearing;
  /**
   * The number of feet travelled per rotation of the motor this is attached to, or null if there is
   * no encoder.
   */
  private final double feetPerRotation;
  /** A list of all the gears this robot has and their settings. */
  @NotNull private final Map<Integer, PerGearSettings> perGearSettings;
  /** Forward limit switch object */
  private final CANDigitalInput forwardLimitSwitch;
  /** Reverse limit switch object */
  private final CANDigitalInput reverseLimitSwitch;
  /** The Spark's name, used for logging purposes. */
  @NotNull private final String name;
  /** Whether the forwards or reverse limit switches are normally open or closed, respectively. */
  private final boolean fwdLimitSwitchNormallyOpen, revLimitSwitchNormallyOpen;
  /** The settings currently being used by this Spark. */
  @NotNull protected Shiftable.PerGearSettings currentGearSettings;
  /** REV brushless controller object */
  private CANSparkMax spark;
  /** REV provided encoder object */
  private CANEncoder canEncoder;
  /** REV provided PID Controller */
  private CANPIDController pidController;
  /** The control mode of the motor */
  private ControlType currentControlMode;
  /** The most recently set setpoint. */
  private double setpoint;

  /** RPS as used in a unit conversion method. Field to avoid garbage collection. */
  private Double RPS;

  /** The setpoint in native units. Field to avoid garbage collection. */
  @Log private double nativeSetpoint;

  /**
   * Create a new SPARK MAX Controller
   *
   * @param port CAN port of this Spark.
   * @param name The Spark's name, used for logging purposes. Defaults to "spark_&gt;port&lt;"
   * @param reverseOutput Whether to reverse the output.
   * @param enableBrakeMode Whether to brake or coast when stopped.
   * @param PDP The PDP this Spark is connected to.
   * @param fwdLimitSwitchNormallyOpen Whether the forward limit switch is normally open or closed.
   *     If this is null, the forward limit switch is disabled.
   * @param revLimitSwitchNormallyOpen Whether the reverse limit switch is normally open or closed.
   *     If this is null, the reverse limit switch is disabled.
   * @param remoteLimitSwitchID The CAN ID the limit switch to use for this Spark is plugged into,
   *     or null to not use a limit switch.
   * @param fwdSoftLimit The forward software limit, in feet. If this is null, the forward software
   *     limit is disabled. Ignored if there's no encoder.
   * @param revSoftLimit The reverse software limit, in feet. If this is null, the reverse software
   *     limit is disabled. Ignored if there's no encoder.
   * @param postEncoderGearing The coefficient the output changes by after being measured by the
   *     encoder, e.g. this would be 1/70 if there was a 70:1 gearing between the encoder and the
   *     final output. Defaults to 1.
   * @param feetPerRotation The number of feet travelled per rotation of the motor this is attached
   *     to. Defaults to 1.
   * @param currentLimit The max amps this device can draw. If this is null, no current limit is
   *     used.
   * @param enableVoltageComp Whether or not to use voltage compensation. Defaults to false.
   * @param perGearSettings The settings for each gear this motor has. Can be null to use default
   *     values and gear # of zero. Gear numbers can't be repeated.
   * @param startingGear The gear to start in. Can be null to use startingGearNum instead.
   * @param startingGearNum The number of the gear to start in. Ignored if startingGear isn't null.
   *     Defaults to the lowest gear.
   * @param statusFrameRatesMillis The update rates, in millis, for each of the status frames.
   * @param controlFrameRateMillis The update rate, in milliseconds, for each control frame.
   */
  @JsonCreator
  public MappedSparkMax(
      @JsonProperty(required = true) final int port,
      @Nullable final String name,
      final boolean reverseOutput,
      @JsonProperty(required = true) final boolean enableBrakeMode,
      @Nullable final org.usfirst.frc.team449.robot.jacksonWrappers.PDP PDP,
      @Nullable final Boolean fwdLimitSwitchNormallyOpen,
      @Nullable final Boolean revLimitSwitchNormallyOpen,
      @Nullable final Integer remoteLimitSwitchID,
      @Nullable final Double fwdSoftLimit,
      @Nullable final Double revSoftLimit,
      @Nullable final Double postEncoderGearing,
      @Nullable final Double feetPerRotation,
      @Nullable final Integer currentLimit,
      final boolean enableVoltageComp,
      @Nullable final List<PerGearSettings> perGearSettings,
      @Nullable final Shiftable.gear startingGear,
      @Nullable final Integer startingGearNum,
      @Nullable final Map<CANSparkMax.PeriodicFrame, Integer> statusFrameRatesMillis,
      @Nullable final Integer controlFrameRateMillis,
      @Nullable final List<SlaveSparkMax> slaveSparks) {
    this.spark = new CANSparkMax(port, CANSparkMaxLowLevel.MotorType.kBrushless);
    this.spark.restoreFactoryDefaults();
    this.canEncoder = this.spark.getEncoder();
    this.pidController = this.spark.getPIDController();

    // Set the name to the given one or to spark_<portnum>
    this.name = name != null ? name : ("spark_" + port);
    // Set this to false because we only use reverseOutput for slaves.
    this.spark.setInverted(reverseOutput);
    // Set brake mode
    this.spark.setIdleMode(
        enableBrakeMode ? CANSparkMax.IdleMode.kBrake : CANSparkMax.IdleMode.kCoast);
    // Reset the position
    this.resetPosition();

    // Set frame rates
    if (controlFrameRateMillis != null) {
      // Must be between 1 and 100 ms.
      this.spark.setControlFramePeriodMs(controlFrameRateMillis);
    }

    if (statusFrameRatesMillis != null) {
      for (final CANSparkMaxLowLevel.PeriodicFrame frame : statusFrameRatesMillis.keySet()) {
        this.spark.setPeriodicFramePeriod(frame, statusFrameRatesMillis.get(frame));
      }
    }

    this.PDP = PDP;

    this.feetPerRotation = feetPerRotation != null ? feetPerRotation : 1;

    // Initialize
    this.perGearSettings = new HashMap<>();

    // If given no gear settings, use the default values.
    if (perGearSettings == null || perGearSettings.size() == 0) {
      this.perGearSettings.put(0, new PerGearSettings());
    }
    // Otherwise, map the settings to the gear they are.
    else {
      for (final PerGearSettings settings : perGearSettings) {
        this.perGearSettings.put(settings.gear, settings);
      }
    }
    int currentGear;
    // If the starting gear isn't given, assume we start in low gear.
    if (startingGear == null) {
      if (startingGearNum == null) {
        currentGear = Integer.MAX_VALUE;
        for (final Integer gear : this.perGearSettings.keySet()) {
          if (gear < currentGear) {
            currentGear = gear;
          }
        }
      } else {
        currentGear = startingGearNum;
      }
    } else {
      currentGear = startingGear.getNumVal();
    }
    this.currentGearSettings = this.perGearSettings.get(currentGear);
    // Set up gear-based settings.
    this.setGear(currentGear);
    // postEncoderGearing defaults to 1
    this.postEncoderGearing = postEncoderGearing != null ? postEncoderGearing : 1.;

    this.encoderCPR = this.canEncoder.getCountsPerRevolution();

    // Only enable the limit switches if it was specified if they're normally open or closed.
    if (fwdLimitSwitchNormallyOpen != null) {
      if (remoteLimitSwitchID != null) {
        // set CANDigitalInput to other limit switch
        this.forwardLimitSwitch =
            new CANSparkMax(remoteLimitSwitchID, CANSparkMaxLowLevel.MotorType.kBrushless)
                .getForwardLimitSwitch(CANDigitalInput.LimitSwitchPolarity.kNormallyOpen);
      } else {
        this.forwardLimitSwitch =
            this.spark.getForwardLimitSwitch(CANDigitalInput.LimitSwitchPolarity.kNormallyOpen);
      }
      this.fwdLimitSwitchNormallyOpen = fwdLimitSwitchNormallyOpen;
    } else {
      this.forwardLimitSwitch =
          this.spark.getForwardLimitSwitch(CANDigitalInput.LimitSwitchPolarity.kNormallyOpen);
      this.forwardLimitSwitch.enableLimitSwitch(false);
      this.fwdLimitSwitchNormallyOpen = true;
    }
    if (revLimitSwitchNormallyOpen != null) {
      if (remoteLimitSwitchID != null) {
        this.reverseLimitSwitch =
            new CANSparkMax(remoteLimitSwitchID, CANSparkMaxLowLevel.MotorType.kBrushless)
                .getReverseLimitSwitch(CANDigitalInput.LimitSwitchPolarity.kNormallyClosed);
      } else {
        this.reverseLimitSwitch =
            this.spark.getReverseLimitSwitch(CANDigitalInput.LimitSwitchPolarity.kNormallyClosed);
      }
      this.revLimitSwitchNormallyOpen = revLimitSwitchNormallyOpen;
    } else {
      this.reverseLimitSwitch =
          this.spark.getReverseLimitSwitch(CANDigitalInput.LimitSwitchPolarity.kNormallyOpen);
      this.reverseLimitSwitch.enableLimitSwitch(false);
      this.revLimitSwitchNormallyOpen = true;
    }

    if (fwdSoftLimit != null) {
      this.spark.setSoftLimit(CANSparkMax.SoftLimitDirection.kForward, fwdSoftLimit.floatValue());
    }
    if (revSoftLimit != null) {
      this.spark.setSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, revSoftLimit.floatValue());
    }

    // Set the current limit if it was given
    if (currentLimit != null) {
      this.spark.setSmartCurrentLimit(currentLimit);
    }

    if (enableVoltageComp) {
      this.spark.enableVoltageCompensation(12);
    } else {
      this.spark.disableVoltageCompensation();
    }

    if (slaveSparks != null) {
      // Set up slaves.
      for (final SlaveSparkMax slave : slaveSparks) {
        slave.setMasterSpark(this.spark, enableBrakeMode);
      }
    }

    this.spark.burnFlash();
  }

  @Override
  public void disable() {
    this.spark.disable();
  }

  @Override
  public void setPercentVoltage(double percentVoltage) {
    this.currentControlMode = ControlType.kVoltage;
    // Warn the user if they're setting Vbus to a number that's outside the range of values.
    if (Math.abs(percentVoltage) > 1.0) {
      Shuffleboard.addEventMarker(
          "WARNING: YOU ARE CLIPPING MAX PERCENT VBUS AT " + percentVoltage,
          this.getClass().getSimpleName(),
          EventImportance.kNormal);
      // Logger.addEvent("WARNING: YOU ARE CLIPPING MAX PERCENT VBUS AT " + percentVoltage,
      // this.getClass());
      percentVoltage = Math.signum(percentVoltage);
    }

    this.setpoint = percentVoltage;

    this.spark.set(percentVoltage);
  }

  @Override
  @Log
  public int getGear() {
    return this.currentGearSettings.gear;
  }

  @Override
  public void setGear(final int gear) {
    // Set the current gear
    this.currentGearSettings = this.perGearSettings.get(gear);

    // note, no current limiting

    if (this.currentGearSettings.rampRate != null) {
      // Set ramp rate, converting from volts/sec to seconds until 12 volts.
      this.spark.setClosedLoopRampRate(1 / (this.currentGearSettings.rampRate / 12.));
      this.spark.setOpenLoopRampRate(1 / (this.currentGearSettings.rampRate / 12.));
    } else {
      this.spark.setClosedLoopRampRate(0);
      this.spark.setOpenLoopRampRate(0);
    }

    this.pidController.setP(this.currentGearSettings.kP, 0);
    this.pidController.setI(this.currentGearSettings.kI, 0);
    this.pidController.setD(this.currentGearSettings.kD, 0);
  }

  /**
   * Convert from native units read by an encoder to feet moved. Note this DOES account for
   * post-encoder gearing.
   *
   * @param revs revolutions measured by the encoder
   * @return That distance in feet, or null if no encoder CPR was given.
   */
  @Override
  public double encoderToUnit(double revs) {
    return revs * feetPerRotation * postEncoderGearing;
  }

  /**
   * Convert a distance from feet to encoder reading in native units. Note this DOES account for
   * post-encoder gearing.
   *
   * @param feet A distance in feet.
   * @return That distance in native units as measured by the encoder, or null if no encoder CPR was
   *     given.
   */
  @Override
  public double unitToEncoder(double feet) {
    return feet / feetPerRotation / postEncoderGearing;
  }

  /**
   * Converts the velocity read by the getVelocity() method to the FPS of the output shaft. Note
   * this DOES account for post-encoder gearing.
   *
   * @param encoderReading The velocity read from the encoder with no conversions.
   * @return The velocity of the output shaft, in FPS, when the encoder has that reading, or null if
   *     no encoder CPR was given.
   */
  @Override
  public double encoderToUPS(double encoderReading) {
    RPS = nativeToRPS(encoderReading);
    return RPS * postEncoderGearing * feetPerRotation;
  }

  /**
   * Converts from the velocity of the output shaft to what the getVelocity() method would read at
   * that velocity. Note this DOES account for post-encoder gearing.
   *
   * @param FPS The velocity of the output shaft, in FPS.
   * @return What the raw encoder reading would be at that velocity, or null if no encoder CPR was
   *     given.
   */
  @Override
  public double UPSToEncoder(double FPS) {
    return RPSToNative((FPS / postEncoderGearing) / feetPerRotation);
  }

  /**
   * Convert from native velocity units to output rotations per second. Note this DOES NOT account
   * for post-encoder gearing.
   *
   * @param nat A velocity in RPM
   * @return That velocity in RPS
   */
  @Contract(pure = true)
  @Override
  public Double nativeToRPS(final double nat) {
    return nat / 60.;
  }

  /**
   * Convert from output RPS to native velocity units. Note this DOES NOT account for post-encoder
   * gearing.
   *
   * @param RPS The RPS velocity you want to convert.
   * @return That velocity in RPM
   */
  @Contract(pure = true)
  @Override
  public double RPSToNative(final double RPS) {
    return RPS * 60.;
  }

  /** @return Total revolutions for debug purposes */
  @Override
  public double encoderPosition() {
    return this.canEncoder.getPosition();
  }

  /**
   * Set a position setpoint for the Spark.
   *
   * @param feet An absolute position setpoint, in feet.
   */
  @Override
  public void setPositionSetpoint(final double feet) {
    this.setpoint = feet;
    this.nativeSetpoint = this.unitToEncoder(feet);
    this.pidController.setFF(this.currentGearSettings.feedForwardCalculator.ks / 12.);
    this.pidController.setReference(
        this.nativeSetpoint,
        ControlType.kPosition,
        0,
        this.currentGearSettings.feedForwardCalculator.ks,
        CANPIDController.ArbFFUnits.kVoltage);
  }

  /** @return Current RPM for debug purposes */
  @Override
  @Log
  public double encoderVelocity() {
    return this.canEncoder.getVelocity();
  }

  /**
   * Get the velocity of the CANTalon in FPS.
   *
   * @return The CANTalon's velocity in FPS, or null if no encoder CPR was given.
   */
  @Override
  @Log
  public Double getVelocity() {
    return this.encoderToUPS(canEncoder.getVelocity());
  }

  /**
   * Set the velocity for the motor to go at.
   *
   * @param velocity the desired velocity, on [-1, 1].
   */
  @Override
  public void setVelocity(double velocity) {
    if (currentGearSettings.maxSpeed != null) {
      setVelocityUPS(velocity * currentGearSettings.maxSpeed);
    } else {
      this.setPercentVoltage(velocity);
    }
  }

  /**
   * Give a velocity closed loop setpoint in FPS.
   *
   * @param velocity velocity setpoint in FPS.
   */
  @Override
  public void setVelocityUPS(double velocity) {
    this.currentControlMode = ControlType.kVelocity;
    this.nativeSetpoint = UPSToEncoder(velocity);
    this.setpoint = velocity;
    this.pidController.setFF(0);
    this.pidController.setReference(
        nativeSetpoint,
        ControlType.kVelocity,
        0,
        this.currentGearSettings.feedForwardCalculator.calculate(velocity),
        CANPIDController.ArbFFUnits.kVoltage);
  }

  @Override
  @Log
  public double getError() {
    return this.getSetpoint() - this.getVelocity();
  }

  @Nullable
  @Override
  @Log
  public Double getSetpoint() {
    return this.setpoint;
  }

  @Override
  @Log
  public double getOutputVoltage() {
    return this.spark.getAppliedOutput() * this.spark.getBusVoltage();
  }

  @Override
  @Log
  public double getBatteryVoltage() {
    return this.spark.getBusVoltage();
  }

  @Override
  @Log
  public double getOutputCurrent() {
    return this.spark.getOutputCurrent();
  }

  @Override
  public String getControlMode() {
    return this.currentControlMode.name();
  }

  @Override
  public void setGearScaledVelocity(double velocity, int gear) {
    if (currentGearSettings.maxSpeed != null) {
      setVelocityUPS(currentGearSettings.maxSpeed * velocity);
    } else {
      this.setPercentVoltage(velocity);
    }
  }

  @Override
  public void setGearScaledVelocity(final double velocity, final gear gear) {
    this.setGearScaledVelocity(velocity, gear.getNumVal());
  }

  @Override
  public SimpleMotorFeedforward getCurrentGearFeedForward() {
    return this.currentGearSettings.feedForwardCalculator;
  }

  @Override
  public Double getPositionUnits() {
    return encoderToUnit(canEncoder.getPosition());
  }

  @Override
  public void resetPosition() {
    this.canEncoder.setPosition(0);
  }

  @Override
  public boolean getFwdLimitSwitch() {
    return this.forwardLimitSwitch.get();
  }

  @Override
  public boolean getRevLimitSwitch() {
    return this.reverseLimitSwitch.get();
  }

  @Override
  public boolean isInhibitedForward() {
    return this.spark.getFault(CANSparkMax.FaultID.kHardLimitFwd);
  }

  @Override
  public boolean isInhibitedReverse() {
    return this.spark.getFault(CANSparkMax.FaultID.kHardLimitRev);
  }

  @Override
  public int getPort() {
    return this.spark.getDeviceId();
  }

  @Override
  public String configureLogName() {
    return this.name;
  }
}
