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

/** Shifts gears. Basically a "ToggleGear" command. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ShiftGears extends InstantCommand {

  /** The drive to execute this command on */
  @NotNull private final Shiftable subsystem;

  /**
   * Default constructor
   *
   * @param subsystem The drive to execute this command on
   */
  @JsonCreator
  public ShiftGears(@NotNull @JsonProperty(required = true) Shiftable subsystem) {
    this.subsystem = subsystem;
  }

  /** Log when this command is initialized */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "ShiftGears init.", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("ShiftGears init.", this.getClass());
  }

  /** Switch gears */
  @Override
  public void execute() {
    subsystem.setGear(
        subsystem.getGear() == Shiftable.gear.LOW.getNumVal()
            ? Shiftable.gear.HIGH.getNumVal()
            : Shiftable.gear.LOW.getNumVal());
  }

  /** Log when this command ends */
  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "ShiftGears Interrupted!", this.getClass().getSimpleName(), EventImportance.kNormal);
    }
    Shuffleboard.addEventMarker(
        "ShiftGears end.", this.getClass().getSimpleName(), EventImportance.kNormal);
  }
}
