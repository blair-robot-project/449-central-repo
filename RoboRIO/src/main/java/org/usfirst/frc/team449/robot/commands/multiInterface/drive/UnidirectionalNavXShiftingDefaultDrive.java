package org.usfirst.frc.team449.robot.commands.multiInterface.drive;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Subsystem;
import io.github.oblarg.oblog.annotations.Log;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.components.AutoshiftComponent;
import org.usfirst.frc.team449.robot.drive.shifting.DriveShiftable;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectional;
import org.usfirst.frc.team449.robot.generalInterfaces.doubleUnaryOperator.RampComponent;
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable;
import org.usfirst.frc.team449.robot.oi.unidirectional.OIUnidirectional;
import org.usfirst.frc.team449.robot.other.BufferTimer;
import org.usfirst.frc.team449.robot.subsystem.interfaces.AHRS.SubsystemAHRS;

/**
 * Drive with arcade drive setup, autoshift, and when the driver isn't turning, use a NavX to
 * stabilize the robot's alignment.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class UnidirectionalNavXShiftingDefaultDrive<
        T extends Subsystem & DriveUnidirectional & SubsystemAHRS & DriveShiftable>
    extends UnidirectionalNavXDefaultDrive<T> {

  /** The drive to execute this command on. */
  @NotNull @Log.Exclude protected final T subsystem;

  /** The helper object for autoshifting. */
  @NotNull protected final AutoshiftComponent autoshiftComponent;

  /** The coefficient to multiply the loop output by in high gear. */
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
   * @param maxAngularVelToEnterLoop The maximum angular velocity, in degrees/sec, at which the loop
   *     will be entered. Defaults to 180.
   * @param inverted Whether the loop is inverted. Defaults to false.
   * @param kP Proportional gain. Defaults to zero.
   * @param kI Integral gain. Defaults to zero.
   * @param kD Derivative gain. Defaults to zero.
   * @param driveStraightLoopEntryTimer The buffer timer for starting to drive straight.
   * @param subsystem The drive to execute this command on.
   * @param oi The OI controlling the robot.
   * @param rampComponent The acceleration-limiting ramp for the output to the drive. Defaults to no
   *     ramp.
   * @param autoshiftComponent The helper object for autoshifting.
   * @param highGearAngularCoefficient The coefficient to multiply the loop output by in high gear.
   *     Defaults to 1.
   */
  @JsonCreator
  public UnidirectionalNavXShiftingDefaultDrive(
      @JsonProperty(required = true) final double absoluteTolerance,
      @Nullable final BufferTimer onTargetBuffer,
      final double minimumOutput,
      @Nullable final Double maximumOutput,
      @Nullable final Integer loopTimeMillis,
      final double deadband,
      @Nullable final Double maxAngularVelToEnterLoop,
      final boolean inverted,
      final double kP,
      final double kI,
      final double kD,
      @NotNull @JsonProperty(required = true) final BufferTimer driveStraightLoopEntryTimer,
      @NotNull @JsonProperty(required = true) final T subsystem,
      @NotNull @JsonProperty(required = true) final OIUnidirectional oi,
      @Nullable final RampComponent rampComponent,
      @NotNull @JsonProperty(required = true) final AutoshiftComponent autoshiftComponent,
      @Nullable final Double highGearAngularCoefficient) {
    super(
        absoluteTolerance,
        onTargetBuffer,
        minimumOutput,
        maximumOutput,
        loopTimeMillis,
        deadband,
        maxAngularVelToEnterLoop,
        inverted,
        kP,
        kI,
        kD,
        driveStraightLoopEntryTimer,
        subsystem,
        oi,
        rampComponent);
    this.kP = kP;
    this.kI = kI;
    this.kD = kD;
    this.autoshiftComponent = autoshiftComponent;
    this.subsystem = subsystem;
    this.highGearAngularCoefficient =
        highGearAngularCoefficient != null ? highGearAngularCoefficient : 1;
    this.lastGear = this.subsystem.getGear();
  }

  /** Autoshift and decide whether or not we should be in free drive or straight drive */
  @Override
  public void execute() {
    // Auto-shifting
    if (!this.subsystem.getOverrideAutoshift()) {
      this.autoshiftComponent.autoshift(
          this.oi.getFwdRotOutputCached()[0],
          Objects.requireNonNullElse(this.subsystem.getLeftVelCached(), 0.0),
          Objects.requireNonNullElse(this.subsystem.getRightVelCached(), 0.0),
          this.subsystem::setGear);
    }

    // Gain schedule the loop if we shifted
    if (this.lastGear != this.subsystem.getGear()) {
      if (this.subsystem.getGear() == Shiftable.gear.LOW.getNumVal()) {
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

    // Actually do stuff
    super.execute();
  }

  /** Log when this command ends */
  @Override
  public void end(final boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "ShiftingUnidirectionalNavXArcadeDrive Interrupted! Stopping the robot.",
          this.getClass().getSimpleName(),
          EventImportance.kNormal);
    }
    this.subsystem.fullStop();
    Shuffleboard.addEventMarker(
        "ShiftingUnidirectionalNavXArcadeDrive End.",
        this.getClass().getSimpleName(),
        EventImportance.kNormal);
    // Logger.addEvent("ShiftingUnidirectionalNavXArcadeDrive End.", this.getClass());
  }
}
