package org.usfirst.frc.team449.robot.subsystem.flywheel.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.subsystem.flywheel.SubsystemFlywheel;

/**
 * Signals the flywheel to turn on and optionally forces the specified subsystem that feeds the
 * flywheel to the off state.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SpinUpFlywheel extends InstantCommand {

  @NotNull @Log.Exclude private final SubsystemFlywheel flywheel;

  private final double targetSpeed;

  /**
   * Default constructor
   *
   * @param flywheel The subsystem to execute this command on.
   * @param targetSpeed The target speed in arbitrary units at which to run the flywheel.
   */
  @JsonCreator
  public SpinUpFlywheel(
      @NotNull @JsonProperty(required = true) final SubsystemFlywheel flywheel,
      @JsonProperty(required = true) final double targetSpeed) {
    this.flywheel = flywheel;
    this.targetSpeed = targetSpeed;
  }

  /** Turn the feeder off and the flywheel on. */
  @Override
  public void execute() {
    flywheel.turnFlywheelOn(targetSpeed);
  }
}
