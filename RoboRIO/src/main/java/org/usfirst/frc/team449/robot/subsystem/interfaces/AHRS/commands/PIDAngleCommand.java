package org.usfirst.frc.team449.robot.subsystem.interfaces.AHRS.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.other.BufferTimer;
import org.usfirst.frc.team449.robot.subsystem.interfaces.AHRS.SubsystemAHRS;

/** A command that uses a AHRS to turn to a certain angle. */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "@class")
public abstract class PIDAngleCommand extends CommandBase implements Loggable {

  /** The subsystem to execute this command on. */
  @NotNull @Log.Exclude protected final SubsystemAHRS subsystem;

  /** On-board PID controller */
  @Log protected final PIDController pidController;

  /** The minimum the robot should be able to output, to overcome friction. */
  private final double minimumOutput;

  /** The range in which output is turned off to prevent "dancing" around the setpoint. */
  private final double deadband;

  /** Whether or not the loop is inverted. */
  private final boolean inverted;

  /**
   * A buffer timer for having the loop be on target before it stops running. Can be null for no
   * buffer.
   */
  @Nullable private final BufferTimer onTargetBuffer;

  /**
   * Default constructor.
   *
   * @param absoluteTolerance The maximum number of degrees off from the target at which we can be
   *     considered within tolerance.
   * @param onTargetBuffer A buffer timer for having the loop be on target before it stops running.
   *     Can be null for no buffer.
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
   * @param subsystem The subsystem to execute this command on.
   */
  @JsonCreator
  public PIDAngleCommand(
      @JsonProperty(required = true) final double absoluteTolerance,
      @Nullable final BufferTimer onTargetBuffer,
      final double minimumOutput,
      @Nullable final Double maximumOutput,
      @Nullable final Integer loopTimeMillis,
      final double deadband,
      final boolean inverted,
      @NotNull @JsonProperty(required = true) final SubsystemAHRS subsystem,
      final double kP,
      final double kI,
      final double kD) {

    // Set P, I and D. I and D will normally be 0 if you're using cascading control, like you should
    // be.
    this.pidController =
        new PIDController(
            kP, kI, kD, loopTimeMillis != null ? loopTimeMillis / 1000. : 20. / 1000.);

    this.subsystem = subsystem;

    // It's a circle, so it's continuous
    pidController.enableContinuousInput(-180, 180);

    // Set the absolute tolerance to be considered on target within.
    pidController.setTolerance(absoluteTolerance);

    // This is how long we have to be within the tolerance band. Multiply by loop period for time in
    // ms.
    this.onTargetBuffer = onTargetBuffer;

    // Minimum output, the smallest output it's possible to give. One-tenth of your drive's top
    // speed is about
    // right.
    this.minimumOutput = minimumOutput;

    // Set a deadband around the setpoint, in degrees, within which don't move, to avoid "dancing"
    this.deadband = deadband;

    // Set whether or not to invert the loop.
    this.inverted = inverted;
  }

  /**
   * Clip a degree number to the NavX's -180 to 180 system.
   *
   * @param theta The angle to clip, in degrees.
   * @return The equivalent of that number, clipped to be between -180 and 180.
   */
  @Contract(pure = true)
  protected static double clipTo180(final double theta) {
    return (theta + 180) % 360 - 180;
  }

  @Log
  protected double getSetpoint() {
    return pidController.getSetpoint();
  }

  /** Set setpoint for PID loop to use */
  protected void setSetpoint(final double setpoint) {
    pidController.setSetpoint(setpoint);
  }

  /**
   * Raw output of the PID loop for later processing
   *
   * @return standard output
   */
  @Log
  protected double getRawOutput() {
    return pidController.calculate(subsystem.getHeadingCached());
  }

  @Log
  public double getError() {
    return pidController.getPositionError();
  }

  /**
   * Process the output of the PID loop to account for minimum output and inversion.
   *
   * @return The processed output, ready to be subtracted from the left side of the drive output and
   *     added to the right side.
   */
  @Log
  protected double getOutput() {
    double controllerOutput = getRawOutput();
    // Set the output to the minimum if it's too small.
    if (controllerOutput > 0 && controllerOutput < minimumOutput) {
      controllerOutput = minimumOutput;
    } else if (controllerOutput < 0 && controllerOutput > -minimumOutput) {
      controllerOutput = -minimumOutput;
    }
    if (inverted) {
      controllerOutput *= -1;
    }

    return controllerOutput;
  }

  protected double getOutputHardcoded(final double setpoint) {
    double controllerOutput = pidController.calculate(subsystem.getHeadingCached(), setpoint);
    // Set the output to the minimum if it's too small.
    if (controllerOutput > 0 && controllerOutput < minimumOutput) {
      controllerOutput = minimumOutput;
    } else if (controllerOutput < 0 && controllerOutput > -minimumOutput) {
      controllerOutput = -minimumOutput;
    }
    if (inverted) {
      controllerOutput *= -1;
    }

    return controllerOutput;
  }

  /**
   * Deadband the output of the PID loop.
   *
   * @param output The output from the WPILib angular PID loop.
   * @return That output after being deadbanded with the map-given deadband.
   */
  protected double deadbandOutput(final double output) {
    return Math.abs(pidController.getPositionError()) > deadband ? output : 0;
  }

  /**
   * Whether or not the loop is on target. Use this instead of {@link
   * PIDController}'s onTarget.
   *
   * @return True if on target, false otherwise.
   */
  @Log
  protected boolean onTarget() {
    if (onTargetBuffer == null) {
      return pidController.atSetpoint();
    } else {
      return onTargetBuffer.get(pidController.atSetpoint());
    }
  }

  /**
   * Returns the PIDController used by the command.
   *
   * @return The PIDController
   */
  public PIDController getController() {
    return this.pidController;
  }

  //    /**
  //     * Get the headers for the data this subsystem logs every loop.
  //     *
  //     * @return An N-length array of String labels for data, where N is the length of the
  // Object[] returned by getData().
  //     */
  //    @NotNull
  //    @Override
  //    public String[] getHeader(){
  //        return new String[]{"setpoint","error"};
  //    }
  //
  //    /**
  //     * Get the data this subsystem logs every loop.
  //     *
  //     * @return An N-length array of Objects, where N is the number of labels given by getHeader.
  //     */
  //    @Nullable
  //    @Override
  //    public Object[] getData(){
  //        return new Object[]{getPIDController().getSetpoint(), getPIDController().getError()};
  //    }
  //
  //    /**
  //     * Get the name of this object.
  //     *
  //     * @return A string that will identify this object in the log file.
  //     */
  //    @NotNull
  //    @Override
  //    public String getLogName(){
  //        return this.getClass().getSimpleName();
  //    }
}
