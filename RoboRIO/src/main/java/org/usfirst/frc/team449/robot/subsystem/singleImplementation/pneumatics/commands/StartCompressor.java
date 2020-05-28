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

/** Start up the pneumatic compressor. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class StartCompressor extends InstantCommand {

  /** The subsystem to execute this command on. */
  @NotNull @Log.Exclude private final Pneumatics subsystem;

  /**
   * Default constructor
   *
   * @param subsystem The subsystem to execute this command on.
   */
  @JsonCreator
  public StartCompressor(@NotNull @JsonProperty(required = true) Pneumatics subsystem) {
    this.subsystem = subsystem;
  }

  /** Log when this command is initialized */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "StartCompressor init.", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("StartCompressor init.", this.getClass());
  }

  /** Start the compressor. */
  @Override
  public void execute() {
    subsystem.startCompressor();
  }

  /** Log when this command ends */
  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "StartCompressor Interrupted!", this.getClass().getSimpleName(), EventImportance.kNormal);
    }
    Shuffleboard.addEventMarker(
        "StartCompressor end.", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("StartCompressor end.", this.getClass());
  }
}
