package org.usfirst.frc.team449.robot.generalInterfaces.shiftable.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable;

/** A command that switches to low gear. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SwitchToLowGear extends InstantCommand {

  /** The drive subsystem to execute this command on. */
  @NotNull private final Shiftable subsystem;

  /**
   * Default constructor
   *
   * @param subsystem The drive subsystem to execute this command on
   */
  @JsonCreator
  public SwitchToLowGear(@NotNull @JsonProperty(required = true) Shiftable subsystem) {
    this.subsystem = subsystem;
  }

  /** Log when this command is initialized */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "SwitchToLowGear init.", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("SwitchToLowGear init.", this.getClass());
  }

  /** Switch to low gear */
  @Override
  public void execute() {
    subsystem.setGear(Shiftable.gear.LOW.getNumVal());
  }

  /** Log when this command ends */
  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "SwitchToLowGear Interrupted!", this.getClass().getSimpleName(), EventImportance.kNormal);
    }
    Shuffleboard.addEventMarker(
        "SwitchToLowGear end.", this.getClass().getSimpleName(), EventImportance.kNormal);
  }
}
