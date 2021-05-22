package org.usfirst.frc.team449.robot.flywheel.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.subsystem.flywheel.SubsystemFlywheel;

/** Turn off the flywheel and feeder. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class TurnAllOff extends InstantCommand {

  /** The subsystem to execute this command on. */
  @NotNull @Log.Exclude private final SubsystemFlywheel subsystem;

  /**
   * Default constructor
   *
   * @param subsystem The subsystem to execute this command on.
   */
  @JsonCreator
  public TurnAllOff(@NotNull @JsonProperty(required = true) final SubsystemFlywheel subsystem) {
    this.subsystem = subsystem;
  }

  /** Turn off the flywheel and feeder. */
  @Override
  public void execute() {
    subsystem.turnFlywheelOff();
  }
}
