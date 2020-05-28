package org.usfirst.frc.team449.robot.subsystem.interfaces.analogMotor.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Subsystem;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.oi.throttles.Throttle;
import org.usfirst.frc.team449.robot.subsystem.interfaces.analogMotor.SubsystemAnalogMotor;

/** A command to control an analog motor with a throttle. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ControlAnalogMotorWithThrottle<T extends Subsystem & SubsystemAnalogMotor>
    extends CommandBase {

  /** The subsystem to execute this command on */
  @NotNull @Log.Exclude private final T subsystem;

  /** The throttle that controls the motor. */
  @NotNull private final Throttle throttle;

  /**
   * Default constructor
   *
   * @param subsystem The subsystem to execute this command on.
   * @param throttle The throttle that controls the motor.
   */
  @JsonCreator
  public ControlAnalogMotorWithThrottle(
      @NotNull @JsonProperty(required = true) T subsystem,
      @NotNull @JsonProperty(required = true) Throttle throttle) {
    this.subsystem = subsystem;
    addRequirements(subsystem);
    this.throttle = throttle;
  }

  /** Log when this command is initialized */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "ControlAnalogMotorWithThrottle init",
        this.getClass().getSimpleName(),
        EventImportance.kNormal);
    // Logger.addEvent("ControlAnalogMotorWithThrottle init", this.getClass());
  }

  /** Set the motor output to the throttle output. */
  @Override
  public void execute() {
    subsystem.set(throttle.getValueCached());
  }

  /**
   * Stop when finished.
   *
   * @return true if finished, false otherwise.
   */
  @Override
  public boolean isFinished() {
    return false;
  }

  /** Log that the command has ended. */
  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "ControlAnalogMotorWithThrottle interrupted!",
          this.getClass().getSimpleName(),
          EventImportance.kNormal);
    }
    Shuffleboard.addEventMarker(
        "ControlAnalogMotorWithThrottle end",
        this.getClass().getSimpleName(),
        EventImportance.kNormal);
  }
}
