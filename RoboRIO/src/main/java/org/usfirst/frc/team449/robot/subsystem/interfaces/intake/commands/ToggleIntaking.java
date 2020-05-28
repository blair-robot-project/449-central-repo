package org.usfirst.frc.team449.robot.subsystem.interfaces.intake.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.SubsystemIntake;

/** Toggles whether the subsystem is off or set to a given mode. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ToggleIntaking extends InstantCommand {

  /** The subsystem to execute this command on. */
  @NotNull @Log.Exclude private final SubsystemIntake subsystem;

  /** The mode to set this subsystem to if it's currently off. */
  @NotNull private final SubsystemIntake.IntakeMode mode;

  /**
   * Default constructor
   *
   * @param subsystem The subsystem to execute this command on.
   * @param mode The mode to set this subsystem to if it's currently off.
   */
  @JsonCreator
  public ToggleIntaking(
      @NotNull @JsonProperty(required = true) SubsystemIntake subsystem,
      @NotNull @JsonProperty(required = true) SubsystemIntake.IntakeMode mode) {
    this.subsystem = subsystem;
    this.mode = mode;
  }

  /** Log when this command is initialized */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "SetIntakeMode init.", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("SetIntakeMode init.", this.getClass());
  }

  /** Set the subsystem to the specified mode if it's off, and set it to off otherwise. */
  @Override
  public void execute() {
    if (subsystem.getMode() == SubsystemIntake.IntakeMode.OFF) {
      subsystem.setMode(mode);
    } else {
      subsystem.setMode(SubsystemIntake.IntakeMode.OFF);
    }
  }

  /** Log when this command ends */
  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "SetIntakeMode Interrupted!", this.getClass().getSimpleName(), EventImportance.kNormal);
    }
    Shuffleboard.addEventMarker(
        "SetIntakeMode end.", this.getClass().getSimpleName(), EventImportance.kNormal);
  }
}
