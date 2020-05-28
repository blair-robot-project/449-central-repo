package org.usfirst.frc.team449.robot.subsystem.interfaces.position.commands;

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
import org.usfirst.frc.team449.robot.subsystem.interfaces.position.SubsystemPosition;

/** Move the motor until it hits a limit switch in order to "zero" it. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Home<T extends Subsystem & SubsystemPosition> extends CommandBase {

  /** The subsystem to execute this command on. */
  @NotNull @Log.Exclude private final T subsystem;

  /** The speed to go at, on [0, 1]. */
  private double speed;

  /** Whether to use the forward or reverse limit switch. */
  private boolean useForward;

  /**
   * Default constructor
   *
   * @param subsystem The subsystem to execute this command on.
   * @param speed The speed to go at, on [0, 1]. The sign is automatically adjusted based on
   *     useForward.
   * @param useForward Whether to use the forward or reverse limit switch. Defaults to using
   *     reverse.
   */
  @JsonCreator
  public Home(
      @NotNull @JsonProperty(required = true) T subsystem,
      @JsonProperty(required = true) double speed,
      boolean useForward) {
    addRequirements(subsystem);
    this.subsystem = subsystem;
    this.speed = speed;
    this.useForward = useForward;
  }

  /** Log on init */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "Home init", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("Home init", this.getClass());
  }

  /** Set the setpoint */
  @Override
  public void execute() {
    if (useForward) {
      subsystem.setMotorOutput(speed);
    } else {
      subsystem.setMotorOutput(-speed);
    }
  }

  /**
   * Exit when we hit the limit switch.
   *
   * @return True if the given limit switch is triggered, false otherwise
   */
  @Override
  public boolean isFinished() {
    if (useForward) {
      return subsystem.getForwardLimit();
    } else {
      return subsystem.getReverseLimit();
    }
  }

  /** Disable the motor and set the position to zero on end. */
  @Override
  public void end(boolean interrupted) {
    subsystem.resetPosition();
    subsystem.disableMotor();
  }
}
