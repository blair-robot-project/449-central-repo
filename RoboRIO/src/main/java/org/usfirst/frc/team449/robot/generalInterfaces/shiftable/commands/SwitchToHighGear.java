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

/** A command that switches to high gear. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SwitchToHighGear extends InstantCommand {

  /** The drive subsystem to execute this command on. */
  @NotNull private final Shiftable subsystem;

  /**
   * Default constructor
   *
   * @param subsystem The subsystem to execute this command on
   */
  @JsonCreator
  public SwitchToHighGear(@NotNull @JsonProperty(required = true) Shiftable subsystem) {
    this.subsystem = subsystem;
  }

  /** Log when this command is initialized */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "SwitchToHighGear init.", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("SwitchToHighGear init.", this.getClass());
  }

  /** Switch to high gear */
  @Override
  public void execute() {
    subsystem.setGear(Shiftable.gear.HIGH.getNumVal());
  }

  /** Log when this command ends */
  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "SwitchToHighGear Interrupted!",
          this.getClass().getSimpleName(),
          EventImportance.kNormal);
    }
    Shuffleboard.addEventMarker(
        "SwitchToHighGear end.", this.getClass().getSimpleName(), EventImportance.kNormal);
  }
}
