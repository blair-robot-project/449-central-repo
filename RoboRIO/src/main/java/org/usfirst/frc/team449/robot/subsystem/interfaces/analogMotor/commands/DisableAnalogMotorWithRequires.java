package org.usfirst.frc.team449.robot.subsystem.interfaces.analogMotor.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.subsystem.interfaces.analogMotor.SubsystemAnalogMotor;

/**
 * Disables the motor of the subsystem, but does so while using requires() to interrupt any other
 * commands currently controlling the subsystem.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class DisableAnalogMotorWithRequires<T extends Subsystem & SubsystemAnalogMotor>
    extends DisableAnalogMotor {

  /**
   * Default constructor
   *
   * @param subsystem The subsystem to execute this command on.
   */
  @JsonCreator
  public DisableAnalogMotorWithRequires(@NotNull @JsonProperty(required = true) T subsystem) {
    super(subsystem);
    addRequirements(subsystem);
  }

  /** Log when this command is initialized */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "DisableAnalogMotorWithRequires init.",
        this.getClass().getSimpleName(),
        EventImportance.kNormal);
    // Logger.addEvent("DisableAnalogMotorWithRequires init.", this.getClass());
  }

  /** Log when this command ends */
  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "DisableAnalogMotorWithRequires Interrupted!",
          this.getClass().getSimpleName(),
          EventImportance.kNormal);
    }
    Shuffleboard.addEventMarker(
        "DisableAnalogMotorWithRequires end.",
        this.getClass().getSimpleName(),
        EventImportance.kNormal);
  }
}
