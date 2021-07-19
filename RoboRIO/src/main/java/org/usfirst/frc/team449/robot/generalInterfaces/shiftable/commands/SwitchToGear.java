package org.usfirst.frc.team449.robot.generalInterfaces.shiftable.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable;

/** Switches to a specified gear. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SwitchToGear extends InstantCommand {

  /** The drive to execute this command on. */
  @NotNull private final Shiftable subsystem;

  /** The gear to switch to. */
  private final int switchTo;

  /**
   * Default constructor
   *
   * @param subsystem The drive to execute this command on.
   * @param switchToNum The number of the gear to switch to. Is ignored if switchTo isn't null.
   * @param switchTo The gear to switch to. Can be null, and if it is, switchToNum is used instead.
   */
  @JsonCreator
  public SwitchToGear(
      @NotNull @JsonProperty(required = true) Shiftable subsystem,
      int switchToNum,
      @Nullable Shiftable.Gear switchTo) {
    this.subsystem = subsystem;
    if (switchTo != null) {
      this.switchTo = switchTo.getNumVal();
    } else {
      this.switchTo = switchToNum;
    }
  }

  /** Log when this command is initialized */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "SwitchToGear init.", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("SwitchToGear init.", this.getClass());
  }

  /** Switch to the specified gear */
  @Override
  public void execute() {
    subsystem.setGear(switchTo);
  }

  /** Log when this command ends */
  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "SwitchToGear Interrupted!", this.getClass().getSimpleName(), EventImportance.kNormal);
    }
    Shuffleboard.addEventMarker(
        "SwitchToGear end.", this.getClass().getSimpleName(), EventImportance.kNormal);
  }
}
