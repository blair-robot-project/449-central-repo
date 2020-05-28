package org.usfirst.frc.team449.robot.subsystem.interfaces.solenoid.commands;

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
import org.usfirst.frc.team449.robot.subsystem.interfaces.solenoid.SubsystemSolenoid;

/** A command that extends a piston. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SolenoidForward extends InstantCommand {

  /** The subsystem to execute this command on. */
  @NotNull @Log.Exclude private final SubsystemSolenoid subsystem;

  /**
   * Default constructor
   *
   * @param subsystem The solenoid subsystem to execute this command on.
   */
  @JsonCreator
  public SolenoidForward(@NotNull @JsonProperty(required = true) SubsystemSolenoid subsystem) {
    this.subsystem = subsystem;
  }

  /** Log when this command is initialized */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "SolenoidForward init.", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("SolenoidForward init.", this.getClass());
  }

  /** Extend the piston. */
  @Override
  public void execute() {
    subsystem.setSolenoid(DoubleSolenoid.Value.kForward);
  }

  /** Log when this command ends */
  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "SolenoidForward Interrupted!", this.getClass().getSimpleName(), EventImportance.kNormal);
    }
    Shuffleboard.addEventMarker(
        "SolenoidForward end.", this.getClass().getSimpleName(), EventImportance.kNormal);
  }
}
