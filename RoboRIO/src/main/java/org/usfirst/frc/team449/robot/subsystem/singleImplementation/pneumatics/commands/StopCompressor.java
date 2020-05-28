package org.usfirst.frc.team449.robot.subsystem.singleImplementation.pneumatics.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.subsystem.singleImplementation.pneumatics.Pneumatics;

/** Stop the pneumatic compressor. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class StopCompressor extends InstantCommand {

  /** The subsystem to execute this command on. */
  @NotNull @Log.Exclude private final Pneumatics subsystem;

  /**
   * Default constructor
   *
   * @param subsystem The subsystem to execute this command on.
   */
  @JsonCreator
  public StopCompressor(@NotNull @JsonProperty(required = true) Pneumatics subsystem) {
    this.subsystem = subsystem;
  }

  /** Log when this command is initialized */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "StopCompressor init.", this.getClass().getSimpleName(), EventImportance.kCritical);
    // Logger.addEvent("StopCompressor init.", this.getClass());
  }

  /** Stop the compressor. */
  @Override
  public void execute() {
    subsystem.stopCompressor();
  }

  /** Log when this command ends */
  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "StopCompressor Interrupted!", this.getClass().getSimpleName(), EventImportance.kNormal);
    }
    Shuffleboard.addEventMarker(
        "StopCompressor end.", this.getClass().getSimpleName(), EventImportance.kNormal);
  }
}
