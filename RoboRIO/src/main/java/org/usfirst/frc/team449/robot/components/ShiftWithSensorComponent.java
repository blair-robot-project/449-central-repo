package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.Notifier;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable;
import org.usfirst.frc.team449.robot.generalInterfaces.simpleMotor.SimpleMotor;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedDigitalInput;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedDoubleSolenoid;
import org.usfirst.frc.team449.robot.other.BufferTimer;

/**
 * A component that a subsystem can use for shifting when the pistons have sensor to detect
 * position.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ShiftWithSensorComponent extends ShiftComponent {

  /** The reed switches that detect if the shifter pistons are in high gear. */
  @NotNull private final List<MappedDigitalInput> highGearSensors;

  /** The reed switches that detect if the shifter pistons are in low gear. */
  @NotNull private final List<MappedDigitalInput> lowGearSensors;

  /** The motors that should be disabled while the piston is shifting. */
  @NotNull private final List<SimpleMotor> motorsToDisable;

  /**
   * The timer for how long the piston can be considered shifting before we ignore the sensors and
   * re-enable the motors.
   */
  @NotNull private final BufferTimer motorDisableTimer;

  /** The Notifier that runs checkToReenable periodically. */
  @NotNull private final Notifier sensorChecker;

  /**
   * The period for the loop that checks the sensors and enables/disables the motors, in seconds.
   */
  private final double sensorCheckerPeriodSecs;

  /** Whether the piston's position was correct the last time checkToReenable was run. */
  private boolean pistonWasCorrect;

  /** Whether the piston's position is currently correct. Field to avoid garbage collection. */
  private boolean pistonCorrect;

  /**
   * Default constructor.
   *
   * @param otherShiftables All objects that should be shifted when this component's piston is.
   * @param piston The piston that shifts.
   * @param startingGear The gear to start in. Can be null, and if it is, the starting gear is
   *     gotten from the piston's position.
   * @param highGearSensors The reed switches that detect if the shifter pistons are in high gear.
   * @param lowGearSensors The reed switches that detect if the shifter pistons are in low gear.
   * @param motorsToDisable The motors that should be disabled while the piston is shifting.
   * @param motorDisableTimer The timer for how long the piston can be considered shifting before we
   *     ignore the sensors and re-enable the motors.
   * @param sensorCheckerPeriodSecs The period for the loop that checks the sensors and
   *     enables/disables the motors, in seconds.
   */
  @JsonCreator
  public ShiftWithSensorComponent(
      @NotNull @JsonProperty(required = true) final List<Shiftable> otherShiftables,
      @NotNull @JsonProperty(required = true) final MappedDoubleSolenoid piston,
      @Nullable final Shiftable.gear startingGear,
      @NotNull @JsonProperty(required = true) final List<MappedDigitalInput> highGearSensors,
      @NotNull @JsonProperty(required = true) final List<MappedDigitalInput> lowGearSensors,
      @NotNull @JsonProperty(required = true) final List<SimpleMotor> motorsToDisable,
      @NotNull @JsonProperty(required = true) final BufferTimer motorDisableTimer,
      @JsonProperty(required = true) final double sensorCheckerPeriodSecs) {
    super(otherShiftables, piston, startingGear);
    this.highGearSensors = highGearSensors;
    this.lowGearSensors = lowGearSensors;
    this.motorsToDisable = motorsToDisable;
    this.motorDisableTimer = motorDisableTimer;
    this.sensorChecker = new Notifier(this::checkToReenable);
    this.sensorChecker.startPeriodic(sensorCheckerPeriodSecs);
    this.sensorCheckerPeriodSecs = sensorCheckerPeriodSecs;
  }

  /** Check the sensors and enable/disable the motors accordingly. */
  private void checkToReenable() {
    // Check if the piston is in correct position by making sure each sensor is reading correctly.
    this.pistonCorrect = true;
    if (this.currentGear == Shiftable.gear.HIGH.getNumVal()) {
      for (final MappedDigitalInput sensor : this.highGearSensors) {
        // The position is correct if all the sensors read true.
        this.pistonCorrect = this.pistonCorrect && sensor.get();
      }
    } else {
      for (final MappedDigitalInput sensor : this.lowGearSensors) {
        // The position is correct if all the sensors read true.
        this.pistonCorrect = this.pistonCorrect && sensor.get();
      }
    }

    // If the pistons haven't been correct for more than a certain amount of time, we assume
    // something went wrong and
    // keep the motors enabled (essentially ignoring the sensors) so the robot can still drive.
    if (this.motorDisableTimer.get(!this.pistonCorrect)) {
      // We set pistonCorrect here because we're basically just overriding what the sensors say.
      this.pistonCorrect = true;
      for (final SimpleMotor motor : this.motorsToDisable) {
        motor.enable();
      }
      this.sensorChecker.stop();
    }
    // Otherwise, if the piston is wrong, disable all the motors. We do this constantly in case some
    // other part of
    // the code tries to re-enable them.
    // TODO set up a lock system so no other part of the code can re-enable the motors.
    else if (!this.pistonCorrect) {
      for (final SimpleMotor motor : this.motorsToDisable) {
        motor.disable();
      }
    }
    // If the piston is correct, but wasn't the last time we checked, that means the piston has
    // finished shifting and
    // we should re-enable the motors.
    else if (!this.pistonWasCorrect) {
      for (final SimpleMotor motor : this.motorsToDisable) {
        motor.enable();
      }
      this.sensorChecker.stop();
    }

    // Record the piston position so we can have rising/falling edge detection
    this.pistonWasCorrect = this.pistonCorrect;
  }

  /**
   * Shifts to a given gear.
   *
   * @param gear The gear to shift to.
   */
  @Override
  public void shiftToGear(final int gear) {
    super.shiftToGear(gear);
    this.sensorChecker.startPeriodic(this.sensorCheckerPeriodSecs);
  }
}
