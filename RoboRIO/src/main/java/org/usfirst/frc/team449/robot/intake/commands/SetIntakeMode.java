package org.usfirst.frc.team449.robot.intake.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.Subsystem;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.subsystem.intake.SubsystemIntake;

/** Sets the mode of the intake. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SetIntakeMode<T extends Subsystem & SubsystemIntake> extends InstantCommand {

  /** The subsystem to execute this command on. */
  @NotNull @Log.Exclude private final T subsystem;

  /** The mode to set this subsystem to. */
  @NotNull private final SubsystemIntake.IntakeMode mode;

  /**
   * Default constructor
   *
   * @param subsystem The subsystem to execute this command on.
   * @param mode The mode to set the intake to.
   */
  @JsonCreator
  public SetIntakeMode(
      @NotNull @JsonProperty(required = true) final T subsystem,
      @NotNull @JsonProperty(required = true) final SubsystemIntake.IntakeMode mode) {
    addRequirements(subsystem);
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

  /** Set the intake to the given mode. */
  @Override
  public void execute() {
    subsystem.setMode(mode);
  }

  /** Log when this command ends */
  @Override
  public void end(final boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "SetIntakeMode Interrupted!", this.getClass().getSimpleName(), EventImportance.kNormal);
    }
    Shuffleboard.addEventMarker(
        "SetIntakeMode end.", this.getClass().getSimpleName(), EventImportance.kNormal);
  }
}
