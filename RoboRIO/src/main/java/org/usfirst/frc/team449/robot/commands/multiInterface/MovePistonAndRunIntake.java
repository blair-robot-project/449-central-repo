package org.usfirst.frc.team449.robot.commands.multiInterface;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.SubsystemIntake;
import org.usfirst.frc.team449.robot.subsystem.interfaces.solenoid.SubsystemSolenoid;

/** Move the intake piston and change the intake state. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MovePistonAndRunIntake<T extends Subsystem & SubsystemIntake & SubsystemSolenoid>
    extends InstantCommand {

  /** The subsystem to execute this command on. */
  @NotNull private final T subsystem;

  /** The position to set the piston to. */
  @NotNull private final DoubleSolenoid.Value pistonPos;

  /** The mode to set the intake to. */
  @NotNull private final SubsystemIntake.IntakeMode intakeMode;

  /**
   * Default constructor
   *
   * @param subsystem The subsystem to execute this command on.
   * @param pistonPos The position to set the piston to.
   * @param intakeMode The mode to set the intake to.
   */
  @JsonCreator
  public MovePistonAndRunIntake(
      @NotNull @JsonProperty(required = true) T subsystem,
      @NotNull @JsonProperty(required = true) DoubleSolenoid.Value pistonPos,
      @NotNull @JsonProperty(required = true) SubsystemIntake.IntakeMode intakeMode) {
    this.subsystem = subsystem;
    this.pistonPos = pistonPos;
    this.intakeMode = intakeMode;
  }

  /** Log when this command is initialized */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "MovePistonAndRunIntake init.", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("MovePistonAndRunIntake init.", this.getClass());
  }

  /** Move the piston and change the intake. */
  @Override
  public void execute() {
    subsystem.setSolenoid(pistonPos);
    subsystem.setMode(intakeMode);
  }

  /** Log when this command ends */
  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "MovePistonAndRunIntake Interrupted!",
          this.getClass().getSimpleName(),
          EventImportance.kNormal);
    }
    Shuffleboard.addEventMarker(
        "MovePistonAndRunIntake end.", this.getClass().getSimpleName(), EventImportance.kNormal);
  }
}
