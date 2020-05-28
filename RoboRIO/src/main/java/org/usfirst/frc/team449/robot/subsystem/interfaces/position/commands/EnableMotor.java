package org.usfirst.frc.team449.robot.subsystem.interfaces.position.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.subsystem.interfaces.position.SubsystemPosition;

/** Enable the motors in the given subsystem. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class EnableMotor extends InstantCommand {

  /** The subsystem to execute this command on. */
  @NotNull @Log.Exclude private final SubsystemPosition subsystem;

  /**
   * Default constructor
   *
   * @param subsystem The subsystem to execute this command on.
   */
  @JsonCreator
  public EnableMotor(@NotNull @JsonProperty(required = true) SubsystemPosition subsystem) {
    this.subsystem = subsystem;
  }

  /** Log when this command is initialized */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "EnableMotor init.", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("EnableMotor init.", this.getClass());
  }

  /** Enables the motor. */
  @Override
  public void execute() {
    subsystem.enableMotor();
  }

  /** Log when this command ends */
  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "EnableMotor interrupted!", this.getClass().getSimpleName(), EventImportance.kNormal);
    }
    Shuffleboard.addEventMarker(
        "EnableMotor end.", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("EnableMotor end.", this.getClass());
  }
}
