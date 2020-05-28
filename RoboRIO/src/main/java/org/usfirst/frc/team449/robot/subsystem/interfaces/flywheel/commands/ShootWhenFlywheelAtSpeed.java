package org.usfirst.frc.team449.robot.subsystem.interfaces.flywheel.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.PerpetualCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.subsystem.interfaces.flywheel.SubsystemFlywheel;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.SubsystemIntake;

/**
 * Periodically checks whether a flywheel is at speed, turning on a feeder if so and turning the
 * feeder off if not.
 *
 * <p>Turns the flywheel on if it isn't running.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ShootWhenFlywheelAtSpeed<T extends Subsystem & SubsystemFlywheel>
    extends PerpetualCommand {
  /**
   * Default constructor.
   *
   * @param flywheel The flywheel to execute this command on.
   * @param feeder The feeder to execute this command on.
   * @param pollingInterval The duration, in seconds, to wait between iterations of this command.
   */
  @JsonCreator
  public ShootWhenFlywheelAtSpeed(
      @NotNull @JsonProperty(required = true) final T flywheel,
      @Nullable final SubsystemIntake feeder,
      @Nullable final Double pollingInterval) {
    super(
        new SequentialCommandGroup(
            new ConditionalCommand(
                new TurnAllOn(flywheel),
                new SpinUpFlywheel(flywheel, feeder),
                flywheel::isAtShootingSpeed),
            new WaitCommand(Objects.requireNonNullElse(pollingInterval, 1.0))));
    this.addRequirements(flywheel);
  }
}
