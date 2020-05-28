package org.usfirst.frc.team449.robot.drive.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.drive.DriveSubsystem;

/** Stops the motors of the given drive subsystem. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class StopMotors extends InstantCommand {

  /** The subsystem to execute this command on. */
  @NotNull private final DriveSubsystem subsystem;

  /**
   * Default constructor
   *
   * @param subsystem The subsystem to execute this command on.
   */
  @JsonCreator
  public StopMotors(@NotNull @JsonProperty(required = true) DriveSubsystem subsystem) {
    this.subsystem = subsystem;
  }

  /** Log when this command is initialized */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "StopMotors init.", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("StopMotors init.", this.getClass());
  }

  /** Do the state change. */
  @Override
  public void execute() {
    subsystem.fullStop();
  }

  /** Log when this command ends */
  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "StopMotors Interrupted!", this.getClass().getSimpleName(), EventImportance.kNormal);
    }
    Shuffleboard.addEventMarker(
        "StopMotors end.", this.getClass().getSimpleName(), EventImportance.kNormal);
  }
}
