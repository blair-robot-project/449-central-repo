package org.usfirst.frc.team449.robot.subsystem.interfaces.flywheel.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.subsystem.interfaces.flywheel.SubsystemFlywheel;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.SubsystemIntake;

/** Turn off the flywheel and feeder. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class TurnAllOffJank extends InstantCommand {

  /** The subsystem to execute this command on. */
  @NotNull @Log.Exclude private final SubsystemFlywheel subsystem;

  private final SubsystemIntake feeder;

  /**
   * Default constructor
   *
   * @param subsystem The subsystem to execute this command on.
   */
  @JsonCreator
  public TurnAllOffJank(
      @NotNull @JsonProperty(required = true) SubsystemFlywheel subsystem,
      @NotNull @JsonProperty(required = true) SubsystemIntake feeder) {
    this.subsystem = subsystem;
    this.feeder = feeder;
  }

  /** Log when this command is initialized */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "TurnAllOff init.", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("TurnAllOff init.", this.getClass());
  }

  /** Turn off the flywheel and feeder. */
  @Override
  public void execute() {
    subsystem.turnFeederOff();
    subsystem.turnFlywheelOff();
    subsystem.setFlywheelState(SubsystemFlywheel.FlywheelState.OFF);
    feeder.setMode(SubsystemIntake.IntakeMode.OFF);
  }

  /** Log when this command ends */
  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "TurnAllOff Interrupted!", this.getClass().getSimpleName(), EventImportance.kNormal);
    }
    Shuffleboard.addEventMarker(
        "TurnAllOff end.", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("TurnAllOff end.", this.getClass());
  }
}
