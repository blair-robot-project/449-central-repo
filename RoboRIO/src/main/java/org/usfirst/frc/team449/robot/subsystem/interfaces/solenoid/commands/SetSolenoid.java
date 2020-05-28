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

/** A command that sets a piston to a given position. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SetSolenoid extends InstantCommand {

  /** The subsystem to execute this command on. */
  @NotNull @Log.Exclude private final SubsystemSolenoid subsystem;

  /** The value to set the solenoid to. */
  @NotNull private final DoubleSolenoid.Value pistonPos;

  /**
   * Default constructor
   *
   * @param subsystem The solenoid subsystem to execute this command on.
   * @param pistonPos The value to set the solenoid to.
   */
  @JsonCreator
  public SetSolenoid(
      @NotNull @JsonProperty(required = true) SubsystemSolenoid subsystem,
      @NotNull @JsonProperty(required = true) DoubleSolenoid.Value pistonPos) {
    this.subsystem = subsystem;
    this.pistonPos = pistonPos;
  }

  /** Log when this command is initialized */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "SetSolenoid init.", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("SetSolenoid init.", this.getClass());
  }

  /** Retract the piston. */
  @Override
  public void execute() {
    subsystem.setSolenoid(pistonPos);
  }

  /** Log when this command ends */
  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "SetSolenoid Interrupted!", this.getClass().getSimpleName(), EventImportance.kNormal);
    }
    Shuffleboard.addEventMarker(
        "SetSolenoid end.", this.getClass().getSimpleName(), EventImportance.kNormal);
  }
}
