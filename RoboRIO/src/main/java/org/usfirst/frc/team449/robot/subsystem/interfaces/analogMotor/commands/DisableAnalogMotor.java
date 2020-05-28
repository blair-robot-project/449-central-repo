package org.usfirst.frc.team449.robot.subsystem.interfaces.analogMotor.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.subsystem.interfaces.analogMotor.SubsystemAnalogMotor;

/** A command that disables an analog motor. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class DisableAnalogMotor extends InstantCommand {

  /** The subsystem to execute this command on. */
  @NotNull @Log.Exclude private final SubsystemAnalogMotor subsystem;

  /**
   * Default constructor
   *
   * @param subsystem The subsystem to execute this command on.
   */
  @JsonCreator
  public DisableAnalogMotor(
      @NotNull @JsonProperty(required = true) SubsystemAnalogMotor subsystem) {
    this.subsystem = subsystem;
  }

  /** Log when this command is initialized */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "DisableAnalogMotor init.", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("DisableAnalogMotor init.", this.getClass());
  }

  /** Disable the subsystem. */
  @Override
  public void execute() {
    subsystem.disable();
  }

  /** Log when this command ends */
  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "DisableAnalogMotor Interrupted!",
          this.getClass().getSimpleName(),
          EventImportance.kNormal);
    }
    Shuffleboard.addEventMarker(
        "DisableAnalogMotor end", this.getClass().getSimpleName(), EventImportance.kNormal);
  }
}
