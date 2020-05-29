package org.usfirst.frc.team449.robot.subsystem.interfaces.analogMotor.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.function.DoubleSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.other.BufferTimer;
import org.usfirst.frc.team449.robot.other.Clock;
import org.usfirst.frc.team449.robot.subsystem.interfaces.analogMotor.SubsystemAnalogMotor;

@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class AnalogPIDCommand<T extends Subsystem & SubsystemAnalogMotor> extends CommandBase {

  /** On-board PID controller */
  protected final PIDController pidController;

  /** The measurement of the process variable */
  @NotNull private final DoubleSupplier processVariableSupplier;
  /** Determines whether input should be inverted */
  private final boolean inverted;
  /** How long this command is allowed to run for (in milliseconds) */
  @Nullable private final Long timeout;
  /** The drive subsystem to execute this command on and to get the gyro reading from. */
  @NotNull private final T subsystem;
  /** The range in which output is turned off to prevent "dancing" around the setpoint. */
  private final double deadband;
  /** The minimum the robot should be able to output, to overcome friction. */
  private final double minimumOutput;
  /**
   * A buffer timer for having the loop be on target before it stops running. Can be null for no
   * buffer.
   */
  @Nullable private final BufferTimer onTargetBuffer;
  /** The time this command was initiated */
  protected long startTime;
  /** Supplies the setpoint */
  @NotNull private DoubleSupplier setpointSupplier;

  /**
   * @param onTargetBuffer A buffer timer for having the loop be on target before it stops running.
   *     Can be null for no buffer.
   * @param absoluteTolerance The maximum value off from the target at which we can be considered
   *     within tolerance.
   * @param minimumOutput The minimum output of the loop. Defaults to zero.
   * @param maximumOutput The maximum output of the loop. Can be null, and if it is, no maximum
   *     output is used.
   * @param loopTimeMillis The time, in milliseconds, between each loop iteration. Defaults to 20
   *     ms.
   * @param deadband The deadband around the setpoint within which no output is given to the motors.
   *     Defaults to zero.
   * @param inverted Determines whether input should be inverted
   * @param kP Proportional gain. Defaults to zero.
   * @param kI Integral gain. Defaults to zero.
   * @param kD Derivative gain. Defaults to zero.
   * @param setpoint The setpoint.
   * @param setpointSupplier Supplies the setpoint
   * @param subsystem The drive subsystem to execute this command on and to get the gyro reading
   *     from.
   * @param timeout How long this command is allowed to run for (in milliseconds). Defaults to no
   *     timeout.
   * @param processVariableSupplier The measurement of the process variable
   */
  @JsonCreator
  public AnalogPIDCommand(
      double absoluteTolerance,
      @Nullable BufferTimer onTargetBuffer,
      double minimumOutput,
      @Nullable Double maximumOutput,
      @Nullable Integer loopTimeMillis,
      double deadband,
      boolean inverted,
      double kP,
      double kI,
      double kD,
      double setpoint,
      @NotNull @JsonProperty(required = true) T subsystem,
      @Nullable Long timeout,
      @NotNull @JsonProperty(required = true) DoubleSupplier processVariableSupplier,
      @Nullable DoubleSupplier setpointSupplier) {
    // Set P, I and D
    this.pidController =
        new PIDController(
            kP, kI, kD, loopTimeMillis != null ? loopTimeMillis / 1000. : 20. / 1000.);
    addRequirements(subsystem);

    // The drive subsystem to execute this command on and to get the gyro reading from.
    this.subsystem = subsystem;

    // Checks in input should be inverted
    this.inverted = inverted;

    // Convert from seconds to milliseconds
    this.timeout = timeout == null ? null : timeout * 1000;

    // This is how long we have to be within the tolerance band. Multiply by loop period for time in
    // ms.
    this.onTargetBuffer = onTargetBuffer;

    // Set the absolute tolerance to be considered on target within.
    this.getController().setTolerance(absoluteTolerance);

    this.minimumOutput = minimumOutput;

    // Supplying a setpoint, in degrees from 180 to -180.
    if (setpointSupplier == null) {
      this.setpointSupplier = () -> setpoint;
    } else {
      this.setpointSupplier = setpointSupplier;
    }

    // Make the processVariableSupplier equal the setpoint
    this.processVariableSupplier = processVariableSupplier;

    // Set a deadband around the setpoint, in appropriate units, within which don't move, to avoid
    // "dancing"
    this.deadband = deadband;

    // Setpoint is always zero since we subtract it off ourselves, from the input.
    this.setSetpoint(0);

    // This caps the output we can give. One way to set up closed-loop is to make P large and then
    // use this to
    // prevent overshoot.
    if (maximumOutput != null) {
      this.getController().enableContinuousInput(-maximumOutput, maximumOutput);
    }
  }

  /**
   * Whether or not the loop is on target. Use this instead of {@link PIDController}'s onTarget.
   *
   * @return True if on target, false otherwise.
   */
  protected boolean onTarget() {
    if (onTargetBuffer == null) {
      return this.getController().atSetpoint();
    } else {
      return onTargetBuffer.get(this.getController().atSetpoint());
    }
  }

  /**
   * Deadband the output of the PID loop.
   *
   * @param output The output from the WPILib PID loop.
   * @return That output after being deadbanded with the map-given deadband.
   */
  protected double deadbandOutput(double output) {
    return Math.abs(this.getController().getPositionError()) > deadband ? output : 0;
  }

  /** Set setpoint for PID loop to use */
  protected void setSetpoint(double setpoint) {
    this.getController().setSetpoint(setpoint);
  }

  /**
   * Returns the input for the pid loop.
   *
   * <p>It returns the input for the pid loop, so if this command was based off of a gyro, then it
   * should return the angle of the gyro.
   *
   * @return the value the pid loop should use as input
   */
  protected double returnPIDInput() {
    double value = processVariableSupplier.getAsDouble();
    if (Double.isNaN(value)) {
      return 0;
    }
    return !inverted
        ? value - setpointSupplier.getAsDouble()
        : -value - setpointSupplier.getAsDouble();
  }

  /**
   * Uses the value that the pid loop calculated. The calculated value is the "output" parameter.
   * This method is a good time to set motor values, maybe something along the lines of <code>
   * driveline.tankDrive(output, -output)</code>
   *
   * @param output the value the pid loop calculated
   */
  protected void usePIDOutput(double output) {
    output = deadbandOutput(output);
    subsystem.set(output);
  }

  /** Set up the start time and setpoint. */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "NavXTurnToAngle init.", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("NavXTurnToAngle init.", this.getClass());
    // Set up start time
    this.startTime = Clock.currentTimeMillis();
    // We handle setpoint math here rather than in the PID Controller.
    this.setSetpoint(0);
  }

  /**
   * Exit when the robot reaches the setpoint or enough time has passed.
   *
   * @return True if timeout seconds have passed or the robot is on target, false otherwise.
   */
  @Override
  public boolean isFinished() {
    // The PIDController onTarget() is crap and sometimes never returns true because of floating
    // point errors, so
    // we need a timeout
    return onTarget() || (timeout != null && Clock.currentTimeMillis() - startTime > timeout);
  }

  /**
   * Process the output of the PID loop to account for minimum output and inversion.
   *
   * @param output The output from the WPILib angular PID loop.
   * @return The processed output, ready to be subtracted from the left side of the drive output and
   *     added to the right side.
   */
  protected double processPIDOutput(double output) {
    // Set the output to the minimum if it's too small.
    if (output > 0 && output < minimumOutput) {
      output = minimumOutput;
    } else if (output < 0 && output > -minimumOutput) {
      output = -minimumOutput;
    }
    if (inverted) {
      output *= -1;
    }

    return output;
  }

  /**
   * Returns the PIDController used by the command.
   *
   * @return The PIDController
   */
  public PIDController getController() {
    return this.pidController;
  }
}
