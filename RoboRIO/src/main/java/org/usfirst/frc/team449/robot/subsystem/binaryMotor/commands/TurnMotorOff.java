package org.usfirst.frc.team449.robot.subsystem.binaryMotor.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.subsystem.binaryMotor.SubsystemBinaryMotor;

/** Turns off the motor of the specified subsystem. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class TurnMotorOff extends InstantCommand {

  /** The subsystem to execute this command on. */
  @NotNull @Log.Exclude protected final SubsystemBinaryMotor subsystem;

  /**
   * Default constructor
   *
   * @param subsystem The subsystem to execute this command on.
   */
  @JsonCreator
  public TurnMotorOff(
      @NotNull @JsonProperty(required = true) final SubsystemBinaryMotor subsystem) {
    this.subsystem = subsystem;
  }

  /** Log when this command is initialized */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "TurnMotorOff init.", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("TurnMotorOff init.", this.getClass());
  }

  /** Turn the motor off. */
  @Override
  public void execute() {
    subsystem.turnMotorOff();
  }

  /** Log when this command ends */
  @Override
  public void end(final boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "TurnMotorOff Interrupted!", this.getClass().getSimpleName(), EventImportance.kNormal);
    }
    Shuffleboard.addEventMarker(
        "TurnMotorOff end.", this.getClass().getSimpleName(), EventImportance.kNormal);
  }
}
