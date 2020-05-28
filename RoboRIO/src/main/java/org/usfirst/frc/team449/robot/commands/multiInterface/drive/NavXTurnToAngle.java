package org.usfirst.frc.team449.robot.commands.multiInterface.drive;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectional;
import org.usfirst.frc.team449.robot.other.BufferTimer;
import org.usfirst.frc.team449.robot.other.Clock;
import org.usfirst.frc.team449.robot.subsystem.interfaces.AHRS.SubsystemAHRS;
import org.usfirst.frc.team449.robot.subsystem.interfaces.AHRS.commands.PIDAngleCommand;

/**
 * Turns to a specified angle, relative to the angle the AHRS was at when the robot was turned on.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class NavXTurnToAngle<T extends Subsystem & DriveUnidirectional & SubsystemAHRS>
    extends PIDAngleCommand {

  /** The drive subsystem to execute this command on and to get the gyro reading from. */
  @NotNull protected final T subsystem;

  /** The angle to turn to. */
  protected final double setpoint;

  /** How long this command is allowed to run for (in milliseconds) */
  private final long timeout;

  /** The time this command was initiated */
  protected long startTime;

  /** The output of the PID loop. Field to avoid garbage collection. */
  private double output;

  /**
   * Default constructor.
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
   * @param setpoint The setpoint, in degrees from 180 to -180.
   * @param subsystem The drive subsystem to execute this command on.
   * @param timeout How long this command is allowed to run for, in seconds. Needed because
   *     sometimes floating-point errors prevent termination.
   */
  @JsonCreator
  public NavXTurnToAngle(
      @JsonProperty(required = true) double absoluteTolerance,
      @Nullable BufferTimer onTargetBuffer,
      double minimumOutput,
      @Nullable Double maximumOutput,
      @Nullable Integer loopTimeMillis,
      double deadband,
      boolean inverted,
      double kP,
      double kI,
      double kD,
      @JsonProperty(required = true) double setpoint,
      @NotNull @JsonProperty(required = true) T subsystem,
      @JsonProperty(required = true) double timeout) {
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
    this.subsystem = subsystem;
    this.setpoint = setpoint;
    // Convert from seconds to milliseconds
    this.timeout = (long) (timeout * 1000);
    addRequirements(subsystem);
  }

  /** Set up the start time and setpoint. */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "NavXTurnToAngle init.", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("NavXTurnToAngle init.", this.getClass());
    // Set up start time
    this.startTime = Clock.currentTimeMillis();
    this.setSetpoint(clipTo180(setpoint));
  }

  /** Give output to the motors based on the output of the PID loop. */
  @Override
  public void execute() {
    // Process the output with deadband, minimum output, etc.
    output = this.getOutput();

    // spin to the right angle
    subsystem.setOutput(-output, output);
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
    return onTarget() || Clock.currentTimeMillis() - startTime > timeout;
  }

  /** Log when the command ends. */
  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "NavXTurnToAngle interrupted!", this.getClass().getSimpleName(), EventImportance.kNormal);
    }
    subsystem.fullStop();
    Shuffleboard.addEventMarker(
        "NavXTurnToAngle end.", this.getClass().getSimpleName(), EventImportance.kNormal);
  }
}
