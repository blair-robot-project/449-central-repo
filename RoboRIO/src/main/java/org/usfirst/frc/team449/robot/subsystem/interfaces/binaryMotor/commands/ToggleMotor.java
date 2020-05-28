package org.usfirst.frc.team449.robot.subsystem.interfaces.binaryMotor.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.subsystem.interfaces.binaryMotor.SubsystemBinaryMotor;

/** A command that toggles the state of the motor between off and on. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ToggleMotor extends InstantCommand {

  /** The subsystem to execute this command on. */
  @NotNull @Log.Exclude private final SubsystemBinaryMotor subsystem;

  /**
   * Default constructor
   *
   * @param subsystem The subsystem to execute this command on.
   */
  @JsonCreator
  public ToggleMotor(@NotNull @JsonProperty(required = true) SubsystemBinaryMotor subsystem) {
    this.subsystem = subsystem;
  }

  /** Log when this command is initialized */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "ToggleMotor init.", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("ToggleMotor init.", this.getClass());
  }

  /** Toggle the motor state. */
  @Override
  public void execute() {
    if (subsystem.isMotorOn()) {
      subsystem.turnMotorOff();
    } else {
      subsystem.turnMotorOn();
    }
  }

  /** Log when this command ends */
  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "ToggleMotor Interrupted!", this.getClass().getSimpleName(), EventImportance.kNormal);
    }
    Shuffleboard.addEventMarker(
        "ToggleMotor end.", this.getClass().getSimpleName(), EventImportance.kNormal);
  }
}
