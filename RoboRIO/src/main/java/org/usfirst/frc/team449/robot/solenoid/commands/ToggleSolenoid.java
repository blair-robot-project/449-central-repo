package org.usfirst.frc.team449.robot.solenoid.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.subsystem.solenoid.SubsystemSolenoid;

/**
 * A command that toggles the position of a piston. DO NOT USE IN COMPETITIONS!! Toggles are too
 * hard for driver to track
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ToggleSolenoid extends InstantCommand {

  /** The subsystem to execute this command on. */
  @NotNull @Log.Exclude private final SubsystemSolenoid subsystem;

  /**
   * Default constructor
   *
   * @param subsystem The solenoid subsystem to execute this command on.
   */
  @JsonCreator
  public ToggleSolenoid(@NotNull @JsonProperty(required = true) final SubsystemSolenoid subsystem) {
    this.subsystem = subsystem;
  }

  /** Log when this command is initialized */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "ToggleSolenoid init.", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("ToggleSolenoid init.", this.getClass());
  }

  /** Toggle the state of the piston. */
  @Override
  public void execute() {
    if (subsystem.getSolenoidPosition().equals(DoubleSolenoid.Value.kForward)) {
      subsystem.setSolenoid(DoubleSolenoid.Value.kReverse);
    } else {
      subsystem.setSolenoid(DoubleSolenoid.Value.kForward);
    }
  }

  /** Log when this command ends */
  @Override
  public void end(final boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "ToggleSolenoid Interrupted!", this.getClass().getSimpleName(), EventImportance.kNormal);
    }
    Shuffleboard.addEventMarker(
        "ToggleSolenoid end.", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("ToggleSolenoid end.", this.getClass());
  }
}
