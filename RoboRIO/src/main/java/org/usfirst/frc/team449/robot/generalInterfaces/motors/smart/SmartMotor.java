package org.usfirst.frc.team449.robot.generalInterfaces.motors.smart;

import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revrobotics.CANError;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.LayoutType;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.components.RunningLinRegComponent;
import org.usfirst.frc.team449.robot.generalInterfaces.motors.simple.SimpleMotor;
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable;
import org.usfirst.frc.team449.robot.jacksonWrappers.*;
import org.usfirst.frc.team449.robot.jacksonWrappers.simulated.FPSSmartMotorSimulated;
import org.usfirst.frc.team449.robot.other.Updater;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.usfirst.frc.team449.robot.other.Util.getLogPrefix;

/**
 * A motor with built in advanced capability featuring encoder, current limiting, and gear shifting
 * support. Also features built in FPS conversions.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public interface SmartMotor extends SimpleMotor, Shiftable, Loggable {
  /**
   * Whether to construct instances of {@link FPSSmartMotorSimulated} instead of the specified
   * controllers when the robot is running in a simulation.
   */
  boolean SIMULATE = true;
  /** Whether to simulate sparks if they cause a HAL error when constructed. */
  boolean SIMULATE_SPARKS_IF_ERR = true;

  int LOG_WIDTH = 4, LOG_HEIGHT = 3;

  /**
   * Creates a new <b>SPARK</b> or <b>Talon</b> motor controller.
   *
   * @param type The type of controller to create.
   * @param port CAN port of this controller.
   * @param name The controller's name, used for logging purposes. Defaults to
   *     &lt;type&gt;_&lt;port&gt;
   * @param reverseOutput Whether to reverse the output.
   * @param enableBrakeMode Whether to brake or coast when stopped.
   * @param voltagePerCurrentLinReg TALON-SPECIFIC. The component for doing linear regression to
   *     find the resistance.
   * @param PDP The PDP this controller is connected to.
   * @param fwdLimitSwitchNormallyOpen Whether the forward limit switch is normally open or closed.
   *     If this is null, the forward limit switch is disabled.
   * @param revLimitSwitchNormallyOpen Whether the reverse limit switch is normally open or closed.
   *     If this is null, the reverse limit switch is disabled.
   * @param remoteLimitSwitchID The CAN port that the limit switch to use for this controller is
   *     plugged into, or null to not use a limit switch or use the limit switch plugged directly
   *     into this controller (for some controllers).
   * @param fwdSoftLimit The forward software limit, in feet. If this is null, the forward software
   *     limit is disabled. Ignored if there's no encoder.
   * @param revSoftLimit The reverse software limit, in feet. If this is null, the reverse software
   *     limit is disabled. Ignored if there's no encoder.
   * @param postEncoderGearing The coefficient the output changes by after being measured by the
   *     encoder, e.g. this would be 1/70 if there was a 70:1 gearing between the encoder and the
   *     final output. Defaults to 1.
   * @param unitPerRotation The number of feet travelled per rotation of the motor this is attached
   *     to. Defaults to 1.
   * @param currentLimit The max amps this device can draw. If this is null, no current limit is
   *     used.
   * @param enableVoltageComp Whether or not to use voltage compensation. Defaults to false.
   * @param voltageCompSamples TALON-SPECIFIC. The number of 1-millisecond samples to use for
   *     voltage compensation. Defaults to 32.
   * @param feedbackDevice TALON-SPECIFIC. The type of encoder used to measure the output velocity
   *     of this motor. Can be null if there is no encoder attached to this controller.
   * @param encoderCPR TALON-SPECIFIC. The counts per rotation of the encoder on this controller.
   *     Can be null if feedbackDevice is, but otherwise must have a value.
   * @param reverseSensor TALON-SPECIFIC. Whether or not to reverse the reading from the encoder on
   *     this controller. Ignored if feedbackDevice is null. Defaults to false.
   * @param perGearSettings The settings for each gear this motor has. Can be null to use default
   *     values and gear # of zero. Gear numbers can't be repeated.
   * @param startingGear The gear to start in. Can be null to use startingGearNum instead.
   * @param startingGearNum The number of the gear to start in. Ignored if startingGear isn't null.
   *     Defaults to the lowest gear.
   * @param updaterProcessPeriodSecs TALON-SPECIFIC. The period for the {@link Notifier} that moves
   *     points between the MP buffers, in seconds. Defaults to 0.005.
   * @param controlFrameRateMillis SPARK-SPECIFIC. The update rate, in milliseconds, each control
   *     frame.
   * @param controlFrameRatesMillis TALON-SPECIFIC. The update rate, in milliseconds, for each of
   *     the control frame.
   * @param slaveTalons TALON-SPECIFIC. The {@link TalonSRX}s that are slaved to this controller.
   * @param slaveVictors TALON-SPECIFIC. The {@link com.ctre.phoenix.motorcontrol.can.VictorSPX}s
   *     that are slaved to this controller.
   * @param slaveSparks The {@link CANSparkMax}s that are slaved to this controller.
   * @param statusFrameRatesMillis The update rates, in millis, for each of the controller status
   *     frames. Each key can be an instance of {@link String}, {@link
   *     CANSparkMaxLowLevel.PeriodicFrame}, or {@link StatusFrameEnhanced}.
   */
  @JsonCreator
  static SmartMotor create(
      @JsonProperty(required = true) final Type type,
      @JsonProperty(required = true) final int port,
      @JsonProperty(required = true) final boolean enableBrakeMode,
      @Nullable final String name,
      final boolean reverseOutput,
      @Nullable final PDP PDP,
      @Nullable final Boolean fwdLimitSwitchNormallyOpen,
      @Nullable final Boolean revLimitSwitchNormallyOpen,
      @Nullable final Integer remoteLimitSwitchID,
      @Nullable final Double fwdSoftLimit,
      @Nullable final Double revSoftLimit,
      @Nullable final Double postEncoderGearing,
      @Nullable final Double unitPerRotation,
      @Nullable final Integer currentLimit,
      final boolean enableVoltageComp,
      @Nullable final List<PerGearSettings> perGearSettings,
      @Nullable final Shiftable.Gear startingGear,
      @Nullable final Integer startingGearNum,
      // Spark-specific
      @Nullable final Integer controlFrameRateMillis,
      // Talon-specific
      @Nullable final Map<ControlFrame, Integer> controlFrameRatesMillis,
      @Nullable final RunningLinRegComponent voltagePerCurrentLinReg,
      @Nullable final Integer voltageCompSamples,
      @Nullable final FeedbackDevice feedbackDevice,
      @Nullable final Integer encoderCPR,
      @Nullable final Boolean reverseSensor,
      @Nullable final Double updaterProcessPeriodSecs,
      @Nullable final List<SlaveTalon> slaveTalons,
      @Nullable final List<SlaveVictor> slaveVictors,
      @Nullable final List<SlaveSparkMax> slaveSparks,
      // Handled specially.
      @Nullable final Map<?, Integer> statusFrameRatesMillis) {
    final var logHelper =
        new Object() {
          public void warning(final String message) {
            this.log("Warning: " + message);
          }

          public void error(final String message) {
            this.log("ERROR: " + message);
          }

          public void log(final String message) {
            this.direct("       " + message);
          }

          public void direct(final String message) {
            System.out.print(getLogPrefix(SmartMotor.class));
            System.out.println(message);
          }
        };

    final String motorLogName = String.format("%s \"%s\" on port %d", type, name, port);

    logHelper.direct("Constructing " + motorLogName);

    final Type actualType;

    if (SIMULATE && RobotBase.isSimulation()) {
      actualType = Type.SIMULATED;
    } else if (SIMULATE_SPARKS_IF_ERR && type == Type.SPARK) {
      try (final var spark = new CANSparkMax(port, CANSparkMaxLowLevel.MotorType.kBrushless)) {
        spark.restoreFactoryDefaults();
        if (spark.getLastError() == CANError.kHALError) {
          actualType = Type.SIMULATED;
          logHelper.warning(
              "error for spark on port "
                  + port
                  + "; assuming nonexistent and replacing with simulated controller");
        } else {
          actualType = type;
        }
      }
    } else {
      actualType = type;
    }

    final var unsupportedHelper =
        new Object() {
          public void log(final String property) {
            logHelper.warning("Property " + property + " is not supported for " + actualType);
          }
        };

    // The status frame map must be dealt with manually because Jackson gives the frames as raw
    // strings due to the
    // type parameter being a wildcard (Object). The solution is to invoke Jackson again to parse
    // them.
    final var sparkStatusFramesMap = new HashMap<CANSparkMaxLowLevel.PeriodicFrame, Integer>();
    final var talonStatusFramesMap = new HashMap<StatusFrameEnhanced, Integer>();

    if (statusFrameRatesMillis != null) {
      for (final Object frame : statusFrameRatesMillis.keySet()) {
        if (frame instanceof String) {
          // Must put it in quotes so Jackson recognizes it as a string.
          final String toBeParsed = "\"" + frame.toString() + "\"";
          try {
            if (actualType == Type.TALON) {
              talonStatusFramesMap.put(
                  new ObjectMapper().readValue(toBeParsed, StatusFrameEnhanced.class),
                  statusFrameRatesMillis.get(frame));
            } else if (actualType == Type.SPARK) {
              sparkStatusFramesMap.put(
                  new ObjectMapper().readValue(toBeParsed, CANSparkMaxLowLevel.PeriodicFrame.class),
                  statusFrameRatesMillis.get(frame));
            }
          } catch (final Exception ex) {
            logHelper.error(" Could not parse status frame rate key value " + toBeParsed);
            throw new RuntimeException(ex);
          }

        } else if (frame instanceof CANSparkMaxLowLevel.PeriodicFrame) {
          if (type == Type.TALON)
            throw new IllegalArgumentException(
                "statusFrameRatesMillis contains key of type CANSparkMaxLowLevel.PeriodicFrame that will not work for FPSTalon");
          sparkStatusFramesMap.put(
              (CANSparkMaxLowLevel.PeriodicFrame) frame, statusFrameRatesMillis.get(frame));

        } else if (frame instanceof StatusFrameEnhanced) {
          if (actualType == Type.SPARK)
            throw new IllegalArgumentException(
                "statusFrameRatesMillis contains key of type StatusFrameEnhanced that will not work for FPSSparkMax");
          talonStatusFramesMap.put((StatusFrameEnhanced) frame, statusFrameRatesMillis.get(frame));

        } else {
          throw new IllegalArgumentException(
              "statusFrameRatesMillis contains key of unexpected type "
                  + frame.getClass().getName());
        }
      }
    }

    final SmartMotor result;

    switch (actualType) {
      case SPARK:
        if (slaveTalons != null) unsupportedHelper.log("slaveTalons");
        if (slaveVictors != null) unsupportedHelper.log("slaveTalons");
        if (voltagePerCurrentLinReg != null) unsupportedHelper.log("voltagePerCurrentLinReg");
        if (encoderCPR != null) unsupportedHelper.log("encoderCPR");
        if (reverseSensor != null) unsupportedHelper.log("reverseSensor");
        if (voltageCompSamples != null) unsupportedHelper.log("voltageCompSamples");
        if (updaterProcessPeriodSecs != null) unsupportedHelper.log("updaterProcessPeriodSecs");
        if (controlFrameRatesMillis != null)
          unsupportedHelper.log("controlFrameRatesMillis (RATESSSS--plural)");

        result =
            new MappedSparkMax(
                port,
                name,
                reverseOutput,
                enableBrakeMode,
                PDP,
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
                sparkStatusFramesMap,
                controlFrameRateMillis,
                slaveSparks);
        break;

      case TALON:
        if (controlFrameRateMillis != null)
          unsupportedHelper.log("controlFrameRatesMillis (RATE--singular)");

        result =
            new MappedTalon(
                port,
                name,
                reverseOutput,
                enableBrakeMode,
                voltagePerCurrentLinReg,
                PDP,
                fwdLimitSwitchNormallyOpen,
                revLimitSwitchNormallyOpen,
                remoteLimitSwitchID,
                fwdSoftLimit,
                revSoftLimit,
                postEncoderGearing,
                unitPerRotation,
                currentLimit,
                enableVoltageComp,
                voltageCompSamples,
                feedbackDevice,
                encoderCPR,
                reverseSensor != null ? reverseSensor : false,
                perGearSettings,
                startingGear,
                startingGearNum,
                talonStatusFramesMap,
                controlFrameRatesMillis,
                slaveTalons,
                slaveVictors,
                slaveSparks);
        break;

      case SIMULATED:
        logHelper.log("SIM:  " + motorLogName);
        final var simulated =
            new FPSSmartMotorSimulated(
                actualType,
                port,
                enableBrakeMode,
                name,
                reverseOutput,
                PDP,
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
                sparkStatusFramesMap,
                controlFrameRateMillis,
                talonStatusFramesMap,
                controlFrameRatesMillis,
                voltagePerCurrentLinReg,
                voltageCompSamples,
                feedbackDevice,
                encoderCPR,
                reverseSensor,
                updaterProcessPeriodSecs,
                slaveTalons,
                slaveVictors,
                slaveSparks);
        Updater.subscribe(simulated);
        result = simulated;
        break;

      default:
        throw new IllegalArgumentException("Unsupported motor type: " + actualType);
    }

    logHelper.direct("SUCCESS:     " + motorLogName);

    MotorContainer.register(result);
    return result;
  }

  /**
   * Set the motor output voltage to a given percent of available voltage.
   *
   * @param percentVoltage percent of total voltage from [-1, 1]
   */
  void setPercentVoltage(double percentVoltage);

  /**
   * Convert from native units read by an encoder to feet moved. Note this DOES account for
   * post-encoder gearing.
   *
   * @param nativeUnits A distance native units as measured by the encoder.
   * @return That distance in feet, or null if no encoder CPR was given.
   */
  double encoderToUnit(double nativeUnits);

  /**
   * Convert a distance from feet to encoder reading in native units. Note this DOES account for
   * post-encoder gearing.
   *
   * @param feet A distance in feet.
   * @return That distance in native units as measured by the encoder, or null if no encoder CPR was
   *     given.
   */
  double unitToEncoder(double feet);

  /**
   * Converts the velocity read by the controllers's getVelocity() method to the FPS of the output
   * shaft. Note this DOES account for post-encoder gearing.
   *
   * @param encoderReading The velocity read from the encoder with no conversions.
   * @return The velocity of the output shaft, in FPS, when the encoder has that reading, or null if
   *     no encoder CPR was given.
   */
  double encoderToUPS(double encoderReading);

  /**
   * Converts from the velocity of the output shaft to what the controllers's getVelocity() method
   * would read at that velocity. Note this DOES account for post-encoder gearing.
   *
   * @param FPS The velocity of the output shaft, in FPS.
   * @return What the raw encoder reading would be at that velocity, or null if no encoder CPR was
   *     given.
   */
  double UPSToEncoder(double FPS);

  /**
   * Convert from native velocity units to output rotations per second. Note this DOES NOT account
   * for post-encoder gearing.
   *
   * @param nat A velocity in native units.
   * @return That velocity in RPS, or null if no encoder CPR was given.
   */
  Double nativeToRPS(double nat);

  /**
   * Convert from output RPS to the native velocity. Note this DOES NOT account for post-encoder
   * gearing.
   *
   * @param RPS The RPS velocity you want to convert.
   * @return That velocity in native units, or null if no encoder CPR was given.
   */
  double RPSToNative(double RPS);

  /** @return Raw position units for debugging purposes */
  double encoderPosition();

  /** Set a position setpoint for the controller. */
  void setPositionSetpoint(double feet);

  /** @return Raw velocity units for debugging purposes */
  double encoderVelocity();

  /** Sets the output in volts. */
  void setVoltage(double volts);

  /**
   * Get the velocity of the controller in FPS.
   *
   * @return The controller's velocity in FPS, or null if no encoder CPR was given.
   */
  double getVelocity();

  /**
   * Set the velocity for the motor to go at.
   *
   * @param velocity the desired velocity, on [-1, 1].
   */
  @Override
  void setVelocity(double velocity);

  /**
   * Give a velocity closed loop setpoint in FPS.
   *
   * @param velocity velocity setpoint in FPS.
   */
  void setVelocityUPS(double velocity);

  /**
   * Get the current closed-loop velocity error in FPS. WARNING: will give garbage if not in
   * velocity mode.
   *
   * @return The closed-loop error in FPS, or null if no encoder CPR was given.
   */
  double getError();

  /**
   * Get the current velocity setpoint of the Talon in FPS, the position setpoint in feet
   *
   * @return The setpoint in sensible units for the current control mode.
   */
  double getSetpoint();

  /**
   * Get the voltage the Talon is currently drawing from the PDP.
   *
   * @return Voltage in volts.
   */
  double getOutputVoltage();

  /**
   * Get the voltage available for the Talon.
   *
   * @return Voltage in volts.
   */
  double getBatteryVoltage();

  /**
   * Get the current the Talon is currently drawing from the PDP.
   *
   * @return Current in amps.
   */
  double getOutputCurrent();

  /**
   * Get the current control mode of the Talon. Please don't use this for anything other than
   * logging.
   *
   * @return Control mode as a string.
   */
  String getControlMode();

  /**
   * Set the velocity scaled to a given gear's max velocity. Used mostly when autoshifting.
   *
   * @param velocity The velocity to go at, from [-1, 1], where 1 is the max speed of the given
   *     gear.
   * @param gear The number of the gear to use the max speed from to scale the velocity.
   */
  void setGearScaledVelocity(double velocity, int gear);

  /**
   * Set the velocity scaled to a given gear's max velocity. Used mostly when autoshifting.
   *
   * @param velocity The velocity to go at, from [-1, 1], where 1 is the max speed of the given
   *     gear.
   * @param gear The gear to use the max speed from to scale the velocity.
   */
  void setGearScaledVelocity(double velocity, Gear gear);

  /** @return Feedforward calculator for this gear */
  SimpleMotorFeedforward getCurrentGearFeedForward();

  /** @return the position of the talon in feet, or null of inches per rotation wasn't given. */
  double getPositionUnits();

  /** Resets the position of the Talon to 0. */
  void resetPosition();

  /**
   * Get the status of the forwards limit switch.
   *
   * @return True if the forwards limit switch is closed, false if it's open or doesn't exist.
   */
  boolean getFwdLimitSwitch();

  /**
   * Get the status of the reverse limit switch.
   *
   * @return True if the reverse limit switch is closed, false if it's open or doesn't exist.
   */
  boolean getRevLimitSwitch();

  boolean isInhibitedForward();

  boolean isInhibitedReverse();

  /**
   * Gets the CAN port of this controller.
   *
   * @return the CAN port of this controller
   */
  int getPort();

  @Override
  default LayoutType configureLayoutType() {
    return BuiltInLayouts.kGrid;
  }

  /**
   * Gets the name of this instance of the class.
   *
   * @return the name of this instance when logging
   */
  @Override
  String configureLogName();

  /**
   * Gets the default width and height of the layout of this instance of the class in Shuffleboard.
   * f
   *
   * @return an array of {width, height}.
   */
  @Override
  default int[] configureLayoutSize() {
    return new int[] {4, 3};
  }

  @Override
  default int[] configureLayoutPosition() {
    return new int[] {3, 3};
  }

  /**
   * Gets whether the motor is a simulated motor.
   *
   * @return whether the motor is a software simulation of a motor
   */
  @Log
  default boolean isSimulated() {
    return false;
  }

  enum Type {
    /** RevRobotics SPARK MAX */
    SPARK("SparkMax"),
    /** CTRE Talon SRX */
    TALON("Talon"),
    /**
     * Simulated motor
     *
     * @see FPSSmartMotorSimulated
     */
    SIMULATED("SIMULATED");

    public final String friendlyName;

    Type(final String friendlyName) {
      this.friendlyName = friendlyName;
    }

    @Override
    public String toString() {
      return this.friendlyName;
    }
  }
}
