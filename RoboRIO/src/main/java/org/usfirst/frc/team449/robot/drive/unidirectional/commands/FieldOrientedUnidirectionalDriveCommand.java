package org.usfirst.frc.team449.robot.drive.unidirectional.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectional;
import org.usfirst.frc.team449.robot.generalInterfaces.AHRS.SubsystemAHRS;
import org.usfirst.frc.team449.robot.generalInterfaces.AHRS.commands.PIDAngleCommand;
import org.usfirst.frc.team449.robot.oi.fieldoriented.OIFieldOriented;
import org.usfirst.frc.team449.robot.other.Debouncer;

/** Unidirectional drive with field-oriented control */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class FieldOrientedUnidirectionalDriveCommand<
        T extends Subsystem & DriveUnidirectional & SubsystemAHRS>
    extends PIDAngleCommand {

  /** The drive this command is controlling. */
  @NotNull protected final T subsystem;

  /** The OI giving the input stick values. */
  @NotNull protected final OIFieldOriented oi;

  /** The points to snap the PID controller input to. */
  @NotNull private final List<AngularSnapPoint> snapPoints;

  /** The absolute angular setpoint for the robot to go to. Field to avoid garbage collection. */
  @Nullable private Double theta;

  /** The output of the PID loop. Field to avoid garbage collection. */
  private double output;

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
   */
  @JsonCreator
  public FieldOrientedUnidirectionalDriveCommand(
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
      @Nullable final List<AngularSnapPoint> snapPoints) {
    // Assign stuff
    super(
        absoluteTolerance,
        onTargetBuffer,
        minimumOutput,
        maximumOutput,
        loopTimeMillis,
        deadband,
        inverted,
        subsystem,
        kP,
        kI,
        kD);
    this.oi = oi;
    this.subsystem = subsystem;
    this.snapPoints = snapPoints != null ? snapPoints : new ArrayList<>();

    // Needs a requires because it's a default command.
    this.addRequirements(this.subsystem);

    // Logging, but in Spanish.
    Shuffleboard.addEventMarker(
        "Drive Robot bueno", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("Drive Robot bueno", this.getClass());
  }

  /** Initialize PIDController and variables. */
  @Override
  public void initialize() {
    // Reset all values of the PIDController and enable it.
    this.getController().reset();
    Shuffleboard.addEventMarker(
        "FieldOrientedUnidirectionalDriveCommand init.",
        this.getClass().getSimpleName(),
        EventImportance.kNormal);
    // Logger.addEvent("FieldOrientedUnidirectionalDriveCommand init.", this.getClass());
  }

  /** Set PID setpoint to processed controller setpoint. */
  @Override
  public void execute() {
    this.theta = this.oi.getThetaCached();

    if (this.theta != null) {
      for (final AngularSnapPoint snapPoint : this.snapPoints) {
        // See if we should snap
        if (snapPoint.getLowerBound() < this.theta && this.theta < snapPoint.getUpperBound()) {
          this.theta = snapPoint.getSnapTo();
          // Break to shorten runtime, we'll never snap twice.
          break;
        }
      }
      this.setSetpoint(this.theta);
    }

    // Process or zero the input depending on whether the NavX is being overriden.
    this.output = this.subsystem.getOverrideGyro() ? 0 : this.getOutput();

    // Adjust the heading according to the PID output, it'll be positive if we want to go right.
    this.subsystem.setOutput(
        this.oi.getVelCached() - this.output, this.oi.getVelCached() + this.output);
  }

  /**
   * Run constantly because this is a defaultDrive
   *
   * @return false
   */
  @Override
  public boolean isFinished() {
    return false;
  }

  /** Log when this command ends */
  @Override
  public void end(final boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "FieldOrientedUnidirectionalDriveCommand Interrupted!",
          this.getClass().getSimpleName(),
          EventImportance.kNormal);
    }
    Shuffleboard.addEventMarker(
        "FieldOrientedUnidirectionalDriveCommand End.",
        this.getClass().getSimpleName(),
        EventImportance.kNormal);
    // Logger.addEvent("FieldOrientedUnidirectionalDriveCommand End.", this.getClass());
  }

  /** A data-holding class representing an angular setpoint to "snap" the controller output to. */
  protected static class AngularSnapPoint {

    /** The angle to snap the setpoint to, in degrees. */
    private final double snapTo;

    /**
     * The upper bound, below which all angles above snapTo are changed to snapTo. Measured in
     * degrees.
     */
    private final double upperBound;

    /**
     * The lower bound, above which all angles below snapTo are changed to snapTo. Measured in
     * degrees.
     */
    private final double lowerBound;

    /**
     * Default constructor.
     *
     * @param snapTo The angle to snap the setpoint to, in degrees.
     * @param upperBound The upper bound, below which all angles above snapTo are changed to snapTo.
     *     Measured in degrees.
     * @param lowerBound The lower bound, above which all angles below snapTo are changed to snapTo.
     *     Measured in degrees.
     */
    @JsonCreator
    public AngularSnapPoint(
        @JsonProperty(required = true) final double snapTo,
        @JsonProperty(required = true) final double upperBound,
        @JsonProperty(required = true) final double lowerBound) {
      this.snapTo = snapTo;
      this.upperBound = upperBound;
      this.lowerBound = lowerBound;
    }

    /** @return The angle to snap the setpoint to, in degrees. */
    public double getSnapTo() {
      return this.snapTo;
    }

    /**
     * @return The upper bound, below which all angles above snapTo are changed to snapTo. Measured
     *     in degrees.
     */
    public double getUpperBound() {
      return this.upperBound;
    }

    /**
     * @return The lower bound, above which all angles below snapTo are changed to snapTo. Measured
     *     in degrees.
     */
    public double getLowerBound() {
      return this.lowerBound;
    }
  }
}
