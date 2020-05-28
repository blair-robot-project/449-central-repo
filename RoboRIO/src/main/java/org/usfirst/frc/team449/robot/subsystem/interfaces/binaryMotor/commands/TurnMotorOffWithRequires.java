package org.usfirst.frc.team449.robot.subsystem.interfaces.binaryMotor.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.subsystem.interfaces.binaryMotor.SubsystemBinaryMotor;

/**
 * Turns off the motor of the subsystem, but does so while using requires() to interrupt any other
 * commands currently controlling the subsystem.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class TurnMotorOffWithRequires<T extends Subsystem & SubsystemBinaryMotor>
    extends TurnMotorOff {

  /**
   * Default constructor
   *
   * @param subsystem The subsystem to execute this command on.
   */
  @JsonCreator
  public TurnMotorOffWithRequires(@NotNull @JsonProperty(required = true) T subsystem) {
    super(subsystem);
    addRequirements(subsystem);
  }

  /** Log when this command is initialized */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "TurnMotorOffWithRequires init.", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("TurnMotorOffWithRequires init.", this.getClass());
  }

  /** Log when this command ends */
  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "TurnMotorOffWithRequires Interrupted!",
          this.getClass().getSimpleName(),
          EventImportance.kNormal);
    }
    Shuffleboard.addEventMarker(
        "TurnMotorOffWithRequires end.", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("TurnMotorOffWithRequires end.", this.getClass());
  }
}
