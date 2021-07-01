package org.usfirst.frc.team449.robot.drive.unidirectional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.components.ShiftComponent;
import org.usfirst.frc.team449.robot.drive.shifting.DriveShiftable;
import org.usfirst.frc.team449.robot.generalInterfaces.motors.smart.SmartMotor;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedAHRS;

/**
 * A drive with a cluster of any number of CANTalonSRX controlled motors on each side and a high and
 * low gear.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class DriveUnidirectionalWithGyroShiftable extends DriveUnidirectionalWithGyro
    implements DriveShiftable {

  /** The component that controls shifting. */
  @NotNull private final ShiftComponent shiftComponent;

  /** Whether not to override auto shifting */
  private boolean overrideAutoshift;

  /**
   * Default constructor.
   *
   * @param leftMaster The master talon on the left side of the drive.
   * @param rightMaster The master talon on the right side of the drive.
   * @param ahrs The NavX on this drive.
   * @param trackWidthMeters The width between the left and right wheels in meters
   * @param shiftComponent The component that controls shifting.
   * @param startingOverrideAutoshift Whether to start with autoshift disabled. Defaults to false.
   */
  @JsonCreator
  public DriveUnidirectionalWithGyroShiftable(
      @NotNull @JsonProperty(required = true) final SmartMotor leftMaster,
      @NotNull @JsonProperty(required = true) final SmartMotor rightMaster,
      @NotNull @JsonProperty(required = true) final MappedAHRS ahrs,
      @JsonProperty(required = true) final double trackWidthMeters,
      @NotNull @JsonProperty(required = true) final ShiftComponent shiftComponent,
      final boolean startingOverrideAutoshift) {
    super(leftMaster, rightMaster, ahrs, trackWidthMeters);
    // Initialize stuff
    this.shiftComponent = shiftComponent;

    // Initialize shifting constants, assuming robot is stationary.
    this.overrideAutoshift = startingOverrideAutoshift;
  }

  /** @return true if currently overriding autoshifting, false otherwise. */
  @Override
  public boolean getOverrideAutoshift() {
    return this.overrideAutoshift;
  }

  /** @param override Whether or not to override autoshifting. */
  @Override
  public void setOverrideAutoshift(final boolean override) {
    this.overrideAutoshift = override;
  }

  /**
   * Set the output of each side of the drive.
   *
   * @param left The output for the left side of the drive, from [-1, 1]
   * @param right the output for the right side of the drive, from [-1, 1]
   */
  @Override
  public void setOutput(final double left, final double right) {
    // If we're not shifting or using PID, or we're just turning in place, scale by the max speed in
    // the current
    // gear
    if (this.overrideAutoshift) {
      super.setOutput(left, right);
    }
    // If we are shifting, scale by the high gear max speed to make acceleration smoother and
    // faster.
    else {
      this.leftMaster.setGearScaledVelocity(left, Gear.HIGH);
      this.rightMaster.setGearScaledVelocity(right, Gear.HIGH);
    }
  }

  /** @return The gear this subsystem is currently in. */
  @Override
  @Log
  public int getGear() {
    return this.shiftComponent.getCurrentGear();
  }

  /**
   * Shift to a specific gear.
   *
   * @param gear Which gear to shift to.
   */
  @Override
  public void setGear(final int gear) {
    this.shiftComponent.shiftToGear(gear);
  }

}
