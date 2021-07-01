package org.usfirst.frc.team449.robot.generalInterfaces.shiftable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.jacksonWrappers.FeedForwardCalculators.MappedFeedForwardCalculator;

/** An interface for any object that different settings for different gears */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "@class")
public interface Shiftable {

  /** @return The gear this subsystem is currently in. */
  int getGear();

  /**
   * Shift to a specific gear.
   *
   * @param gear Which gear to shift to.
   */
  void setGear(int gear);

  enum Gear {
    LOW(1),
    HIGH(2);

    private final int numVal;

    Gear(int numVal) {
      this.numVal = numVal;
    }

    @Contract(pure = true)
    public int getNumVal() {
      return numVal;
    }
  }

  /** An object representing the settings that are different for each gear. */
  class PerGearSettings {

    /** The gear number this is the settings for. */
    public final int gear;

    /** The forwards and reverse peak output voltages. */
    public final double fwdPeakOutputVoltage, revPeakOutputVoltage;

    /** The forwards and reverse nominal output voltages. */
    public final double fwdNominalOutputVoltage, revNominalOutputVoltage;

    /** The ramp rate, in volts/sec. null means no ramp rate. */
    @Nullable public final Double rampRate;

    /** The maximum speed of the motor in this gear, in FPS. Used for throttle scaling. */
    @Nullable public final Double maxSpeed;

    /**
     * The coefficient the output changes by after being measured by the encoder, e.g. this would be
     * 1/70 if there was a 70:1 gearing between the encoder and the final output.
     */
    @Nullable public final Double postEncoderGearing;

    /** The PID constants for the motor in this gear. Ignored if maxSpeed is null. */
    public final double kP, kI, kD;

    /** The position PID constants for the motor in this gear. */
    public final double posKP, posKI, posKD;

    /**
     * WPI object for calculating feed forward constants given a max achievable velocity and
     * acceleration
     */
    public SimpleMotorFeedforward feedForwardCalculator;

    /**
     * Default constructor.
     *
     * @param gearNum The gear number this is the settings for. Ignored if gear isn't null.
     * @param gear The gear this is the settings for. Can be null.
     * @param fwdPeakOutputVoltage The peak output voltage for closed-loop modes in the forwards
     *     direction, in volts. Defaults to 12.
     * @param revPeakOutputVoltage The peak output voltage for closed-loop modes in the reverse
     *     direction, in volts. Defaults to -fwdPeakOutputVoltage.
     * @param fwdNominalOutputVoltage The minimum output voltage for closed-loop modes in the
     *     forwards direction. This does not rescale, it just sets any output below this voltage to
     *     this voltage. Defaults to 0.
     * @param revNominalOutputVoltage The minimum output voltage for closed-loop modes in the
     *     reverse direction. This does not rescale, it just sets any output below this voltage to
     *     this voltage. Defaults to -fwdNominalOutputVoltage.
     * @param feedForwardCalculator The component for calculating feedforwards in closed-loop
     *     control modes.
     * @param rampRate The ramp rate, in volts/sec. Can be null, and if it is, no ramp rate is used.
     * @param maxSpeed The maximum speed of the motor in this gear, in FPS. Used for throttle
     *     scaling. Ignored if kVFwd is null. Calculated from the drive characterization terms if
     *     null.
     * @param kP The proportional PID constant for the motor in this gear. Ignored if kVFwd is null.
     *     Defaults to 0.
     * @param kI The integral PID constant for the motor in this gear. Ignored if kVFwd is null.
     *     Defaults to 0.
     * @param kD The derivative PID constant for the motor in this gear. Ignored if kVFwd is null.
     *     Defaults to 0.
     * @param posKP The proportional PID constant for position control on the motor in this gear.
     *     Ignored if kVFwd is null. Defaults to 0.
     * @param posKI The integral PID constant for position control on the motor in this gear.
     *     Ignored if kVFwd is null. Defaults to 0.
     * @param posKD The derivative PID constant for position control on the motor in this gear.
     *     Ignored if kVFwd is null. Defaults to 0.
     */
    @JsonCreator
    public PerGearSettings(
        int gearNum,
        @Nullable Shiftable.Gear gear,
        @Nullable Double fwdPeakOutputVoltage,
        @Nullable Double revPeakOutputVoltage,
        @Nullable Double fwdNominalOutputVoltage,
        @Nullable Double revNominalOutputVoltage,
        @Nullable MappedFeedForwardCalculator feedForwardCalculator,
        @Nullable Double rampRate,
        @Nullable Double maxSpeed,
        @Nullable Double postEncoderGearing,
        double kP,
        double kI,
        double kD,
        double posKP,
        double posKI,
        double posKD) {
      this.gear = gear != null ? gear.getNumVal() : gearNum;
      this.fwdPeakOutputVoltage = fwdPeakOutputVoltage != null ? fwdPeakOutputVoltage : 12;
      this.revPeakOutputVoltage =
          revPeakOutputVoltage != null ? revPeakOutputVoltage : -this.fwdPeakOutputVoltage;
      this.fwdNominalOutputVoltage = fwdNominalOutputVoltage != null ? fwdNominalOutputVoltage : 0;
      this.revNominalOutputVoltage =
          revNominalOutputVoltage != null ? revNominalOutputVoltage : -this.fwdNominalOutputVoltage;
      this.feedForwardCalculator =
          feedForwardCalculator != null ? feedForwardCalculator : new SimpleMotorFeedforward(0, 0);
      this.rampRate = rampRate;
      this.postEncoderGearing = postEncoderGearing;
      this.kP = kP;
      this.kI = kI;
      this.kD = kD;
      this.posKP = posKP;
      this.posKI = posKI;
      this.posKD = posKD;
      this.maxSpeed = maxSpeed;
    }

    /** Empty constructor that uses all default options. */
    public PerGearSettings() {
      this(0, null, null, null, null, null, null, null, null, null, 0, 0, 0, 0, 0, 0);
    }
  }
}
