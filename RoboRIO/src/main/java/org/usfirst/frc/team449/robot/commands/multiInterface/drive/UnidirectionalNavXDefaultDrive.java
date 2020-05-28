package org.usfirst.frc.team449.robot.commands.multiInterface.drive;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Subsystem;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;
import java.util.function.DoubleUnaryOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectional;
import org.usfirst.frc.team449.robot.generalInterfaces.doubleUnaryOperator.RampComponent;
import org.usfirst.frc.team449.robot.oi.unidirectional.OIUnidirectional;
import org.usfirst.frc.team449.robot.other.BufferTimer;
import org.usfirst.frc.team449.robot.subsystem.interfaces.AHRS.SubsystemAHRS;
import org.usfirst.frc.team449.robot.subsystem.interfaces.AHRS.commands.PIDAngleCommand;

/**
 * Drive with arcade drive setup, and when the driver isn't turning, use a NavX to stabilize the
 * robot's alignment.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class UnidirectionalNavXDefaultDrive<
        T extends Subsystem & DriveUnidirectional & SubsystemAHRS>
    extends PIDAngleCommand implements Loggable {
  /** The drive this command is controlling. */
  @NotNull @Log.Exclude protected final T subsystem;
  /** The OI giving the input stick values. */
  @NotNull protected final OIUnidirectional oi;
  /**
   * The maximum velocity for the robot to be at in order to switch to driveStraight, in degrees/sec
   */
  private final double maxAngularVelToEnterLoop;
  /**
   * A bufferTimer so we only switch to driving straight when the conditions are met for a certain
   * period of time.
   */
  @NotNull private final BufferTimer driveStraightLoopEntryTimer;
  /**
   * Acceleration-limiting ramps for the left and right sides of the drive, respectively. Null for
   * no ramp.
   */
  @Nullable private final DoubleUnaryOperator leftRamp, rightRamp;
  /** Whether or not we should be using the NavX to drive straight stably. */
  private boolean drivingStraight;
  /** Logging variables. */
  private double rawOutput, processedOutput, finalOutput;
  /** Output to the left and right sides of the drive. Field to avoid garbage collection. */
  private double leftOutput, rightOutput;

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
   */
  @JsonCreator
  public UnidirectionalNavXDefaultDrive(
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
      @Nullable final RampComponent rampComponent) {
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
    this.leftRamp = rampComponent;
    this.rightRamp =
        rampComponent != null ? rampComponent.clone() : null; // We want the same settings but
    // different objects, so we clone

    this.driveStraightLoopEntryTimer = driveStraightLoopEntryTimer;
    this.maxAngularVelToEnterLoop =
        maxAngularVelToEnterLoop != null ? maxAngularVelToEnterLoop : 180;

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
        "UnidirectionalNavXArcadeDrive init.",
        this.getClass().getSimpleName(),
        EventImportance.kNormal);
    // Logger.addEvent("UnidirectionalNavXArcadeDrive init.", this.getClass());

    // Initial assignment
    this.drivingStraight = false;
  }

  /** Decide whether or not we should be in free drive or straight drive. */
  @Override
  public void execute() {
    // If we're driving straight but the driver tries to turn or overrides the AHRS:
    if (this.drivingStraight
        && (!this.oi.commandingStraight() || this.subsystem.getOverrideGyro())) {
      // Switch to free drive
      this.drivingStraight = false;
    }
    // If we're free driving and the driver stops turning:
    else if (this.driveStraightLoopEntryTimer.get(
        !(this.subsystem.getOverrideGyro())
            && !(this.drivingStraight)
            && this.oi.commandingStraight()
            && Math.abs(this.subsystem.getAngularVelCached()) <= this.maxAngularVelToEnterLoop)) {
      // Switch to driving straight
      this.drivingStraight = true;
      // Set the setpoint to the current heading and reset the AHRS
      this.getController().reset();
      this.setSetpoint(this.subsystem.getHeadingCached());
    }

    // Get the outputs
    this.rawOutput = this.getRawOutput();
    this.leftOutput = this.oi.getLeftRightOutputCached()[0];
    this.rightOutput = this.oi.getLeftRightOutputCached()[1];

    // Ramp if it exists
    if (this.leftRamp != null) {
      this.leftOutput = this.leftRamp.applyAsDouble(this.leftOutput);
      this.rightOutput = this.rightRamp.applyAsDouble(this.rightOutput);
    }

    // If we're driving straight..
    if (this.drivingStraight) {
      // Process the output (minimumOutput, deadband, etc.)
      this.processedOutput = this.getOutput();

      // Deadband if we're stationary
      if (this.leftOutput == 0 && this.rightOutput == 0) {
        this.finalOutput = this.deadbandOutput(this.processedOutput);
      } else {
        this.finalOutput = this.processedOutput;
      }

      // Adjust the heading according to the PID output, it'll be positive if we want to go right.
      this.subsystem.setOutput(
          this.leftOutput - this.finalOutput, this.rightOutput + this.finalOutput);
    }
    // If we're free driving...
    else {
      this.processedOutput = 0;
      this.finalOutput = 0;
      // Set the throttle to normal arcade throttle.
      this.subsystem.setOutput(this.leftOutput, this.rightOutput);
    }
  }

  /**
   * Run constantly because this is a defaultDrive
   *
   * @return false
   */
  @Override
  @Log
  public boolean isFinished() {
    return false;
  }

  /** Log when this command ends */
  @Override
  public void end(final boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "UnidirectionalNavXArcadeDrive Interrupted! Stopping the robot.",
          this.getClass().getSimpleName(),
          EventImportance.kNormal);
    }
    this.subsystem.fullStop();
    Shuffleboard.addEventMarker(
        "UnidirectionalNavXArcadeDrive End.",
        this.getClass().getSimpleName(),
        EventImportance.kNormal);
    // Logger.addEvent("UnidirectionalNavXArcadeDrive End.", this.getClass());
  }

  //    /**
  //     * Get the headers for the data this subsystem logs every loop.
  //     *
  //     * @return An N-length array of String labels for data, where N is the length of the
  // Object[] returned by getData().
  //     */
  //    @NotNull
  //    @Override
  //    public String[] getHeader() {
  //        return new String[]{
  //                "drivingStraight",
  //                "running",
  //                "raw_output",
  //                "processed_output",
  //                "final_output"
  //        };
  //    }
  //
  //    /**
  //     * Get the data this subsystem logs every loop.
  //     *
  //     * @return An N-length array of Objects, where N is the number of labels given by getHeader.
  //     */
  //    @NotNull
  //    @Override
  //    public Object[] getData() {
  //        return new Object[]{
  //                drivingStraight,
  //                this.isRunning(),
  //                rawOutput,
  //                processedOutput,
  //                finalOutput
  //        };
  //    }
  //
  //    /**
  //     * Get the name of this object.
  //     *
  //     * @return A string that will identify this object in the log file.
  //     */
  //    @Override
  //    @NotNull
  //    @Contract(pure = true)
  //    public String getLogName() {
  //        return "UnidirectionalNavXDefaultDrive";
  //    }

  @Log
  public boolean isDrivingStraight() {
    return this.drivingStraight;
  }

  @Override
  public double getRawOutput() {
    return super.getRawOutput();
  }

  @Log
  public double getProcessedOutput() {
    return this.processedOutput;
  }

  @Log
  public double getFinalOutput() {
    return this.finalOutput;
  }
}
