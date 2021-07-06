package org.usfirst.frc.team449.robot.drive.unidirectional.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.components.AutoshiftComponent;
import org.usfirst.frc.team449.robot.drive.shifting.DriveShiftable;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectionalWithGyro;
import org.usfirst.frc.team449.robot.generalInterfaces.AHRS.SubsystemAHRS;
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable;
import org.usfirst.frc.team449.robot.oi.fieldoriented.OIFieldOriented;
import org.usfirst.frc.team449.robot.other.Debouncer;

/** Unidirectional drive with field-oriented control and autoshifting. */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class FieldOrientedUnidirectionalDriveCommandShifting<
    T extends DriveUnidirectionalWithGyro & Subsystem & SubsystemAHRS & DriveShiftable>
    extends FieldOrientedUnidirectionalDriveCommand<T> {

  /** The drive to execute this command on. */
  @NotNull protected final T subsystem;

  /** The helper object for autoshifting. */
  @NotNull private final AutoshiftComponent autoshiftComponent;

  /** The coefficient to multiply the loop output by in high gear. Defaults to 1. */
  private final double highGearAngularCoefficient;

  /** PID loop coefficients. */
  private final double kP, kI, kD;

  /** The gear the subsystem was in the last time execute() ran. */
  private int lastGear;

  /**
   * Default constructor
   *
   * @param onTargetBuffer A buffer timer for having the loop be on target before it stops running.
   *     Can be null for no buffer.
   * @param absoluteTolerance The maximum number of degrees off from the target at which we can be
   *     considered within tolerance.
   * @param minimumOutput The minimum output of the loop. Defaults to zero.
   * @param maximumOutput The maximum output of the loop. Can be null, and if it is, no maximum
   *     output is used.
   * @param loopTimeMillis The time, in milliseconds, between each loop iteration. Defaults to 20
   *     ms.
   * @param deadband The deadband around the setpoint, in degrees, within which no output is given
   *     to the motors. Defaults to zero.
   * @param inverted Whether the loop is inverted. Defaults to false.
   * @param kP Proportional gain. Defaults to zero.
   * @param kI Integral gain. Defaults to zero.
   * @param kD Derivative gain. Defaults to zero.
   * @param subsystem The drive to execute this command on.
   * @param oi The OI controlling the robot.
   * @param snapPoints The points to snap the PID controller input to.
   * @param autoshiftComponent The helper object for autoshifting.
   * @param highGearAngularCoefficient The coefficient to multiply the loop output by in high gear.
   *     Defaults to 1.
   */
  @JsonCreator
  public FieldOrientedUnidirectionalDriveCommandShifting(
      @JsonProperty(required = true) final double absoluteTolerance,
      @Nullable final Debouncer onTargetBuffer,
      final double minimumOutput,
      @Nullable final Double maximumOutput,
      @Nullable final Integer loopTimeMillis,
      final double deadband,
      final boolean inverted,
      final double kP,
      final double kI,
      final double kD,
      @NotNull @JsonProperty(required = true) final T subsystem,
      @NotNull @JsonProperty(required = true) final OIFieldOriented oi,
      @Nullable final List<AngularSnapPoint> snapPoints,
      @NotNull @JsonProperty(required = true) final AutoshiftComponent autoshiftComponent,
      @Nullable final Double highGearAngularCoefficient) {
    // Assign stuff
    super(
        absoluteTolerance,
        onTargetBuffer,
        minimumOutput,
        maximumOutput,
        loopTimeMillis,
        deadband,
        inverted,
        kP,
        kI,
        kD,
        subsystem,
        oi,
        snapPoints);
    this.kP = kP;
    this.kI = kI;
    this.kD = kD;
    this.subsystem = subsystem;
    this.autoshiftComponent = autoshiftComponent;
    this.highGearAngularCoefficient =
        highGearAngularCoefficient != null ? highGearAngularCoefficient : 1;
    this.lastGear = this.subsystem.getGear();
  }

  /** Set PID setpoint to processed controller setpoint and autoshift. */
  @Override
  public void execute() {
    if (!this.subsystem.getOverrideAutoshift()) {
      this.autoshiftComponent.autoshift(
          this.oi.getVelCached(),
          this.subsystem.getLeftVelCached(),
          this.subsystem.getRightVelCached(),
          this.subsystem::setGear);
    }

    // Gain schedule the loop if we shifted
    if (this.lastGear != this.subsystem.getGear()) {
      if (this.subsystem.getGear() == Shiftable.Gear.LOW.getNumVal()) {
        this.getController().setP(this.kP);
        this.getController().setI(this.kI);
        this.getController().setD(this.kD);
      } else {
        this.getController().setP(this.kP * this.highGearAngularCoefficient);
        this.getController().setI(this.kI * this.highGearAngularCoefficient);
        this.getController().setD(this.kD * this.highGearAngularCoefficient);
      }
      this.lastGear = this.subsystem.getGear();
    }

    super.execute();
  }

  /** Log when this command ends */
  @Override
  public void end(final boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "FieldOrientedUnidirectionalDriveCommandShifting Interrupted!",
          this.getClass().getSimpleName(),
          EventImportance.kNormal);
    }
    Shuffleboard.addEventMarker(
        "FieldOrientedUnidirectionalDriveCommandShifting End.",
        this.getClass().getSimpleName(),
        EventImportance.kNormal);
    // Logger.addEvent("FieldOrientedUnidirectionalDriveCommandShifting End.", this.getClass());
  }
}
