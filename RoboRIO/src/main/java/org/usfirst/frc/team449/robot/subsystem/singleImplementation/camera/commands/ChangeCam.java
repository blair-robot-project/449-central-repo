package org.usfirst.frc.team449.robot.subsystem.singleImplementation.camera.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.subsystem.singleImplementation.camera.CameraNetwork;

/** Toggles camera on button press. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ChangeCam extends InstantCommand {

  /** The subsystem to execute this command on */
  @NotNull @Log.Exclude private final CameraNetwork subsystem;

  /**
   * Default constructor.
   *
   * @param subsystem The subsystem to execute this command on.
   */
  @JsonCreator
  public ChangeCam(@NotNull @JsonProperty(required = true) CameraNetwork subsystem) {
    this.subsystem = subsystem;
    addRequirements(subsystem);
  }

  /** Log when this command is initialized */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "ChangeCam init", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("ChangeCam init", this.getClass());
  }

  /** Switch the MjpegServer to use the next camera in the list */
  @Override
  public void execute() {
    // Switches camNum to next camera, if applicable
    if (subsystem.getCameras().size() == 1) {
      Shuffleboard.addEventMarker(
          "You're trying to switch cameras, but your robot only has one camera!",
          this.getClass().getSimpleName(),
          EventImportance.kNormal);
      // Logger.addEvent("You're trying to switch cameras, but your robot only has one camera!",
      // this.getClass());
    } else {
      subsystem.setCamNum((subsystem.getCamNum() + 1) % subsystem.getCameras().size());
    }

    // Switches to set camera
    subsystem.getServer().setSource(subsystem.getCameras().get(subsystem.getCamNum()));
  }

  /** Log when this command ends */
  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "ChangeCam interrupted!", this.getClass().getSimpleName(), EventImportance.kNormal);
    }
    Shuffleboard.addEventMarker(
        "ChangeCam end", this.getClass().getSimpleName(), EventImportance.kNormal);
  }
}
