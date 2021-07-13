package org.usfirst.frc.team449.robot.subsystem.flywheel.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.components.MapInterpolationComponent;
import org.usfirst.frc.team449.robot.subsystem.flywheel.SubsystemFlywheel;

import java.util.function.DoubleSupplier;

/**
 * Signals the flywheel to turn on and optionally forces the specified subsystem that feeds the
 * flywheel to the off state. Uses a MapInterpolationComponent to calculate the appropriate speed to
 * set the flywheel to.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SpinUpFlywheelInterpolating extends InstantCommand {

  @NotNull @Log.Exclude private final SubsystemFlywheel flywheel;

  private final MapInterpolationComponent targetSpeed;

  private final DoubleSupplier limelightComponent;

  /**
   * Default constructor
   *
   * @param flywheel The subsystem to execute this command on.
   * @param targetSpeed The target speed in arbitrary units at which to run the flywheel.
   * @param limelightComponent The component that calculates the distance to the target using the
   *     limelight
   */
  @JsonCreator
  public SpinUpFlywheelInterpolating(
      @NotNull @JsonProperty(required = true) final SubsystemFlywheel flywheel,
      @NotNull @JsonProperty(required = true) final MapInterpolationComponent targetSpeed,
      @NotNull @JsonProperty(required = true) final DoubleSupplier limelightComponent) {
    this.flywheel = flywheel;
    this.targetSpeed = targetSpeed;
    this.limelightComponent = limelightComponent;
  }

  /** Turn the feeder off and the flywheel on. */
  @Override
  public void execute() {
    flywheel.turnFlywheelOn(targetSpeed.calculate(limelightComponent.getAsDouble()));
  }
}
