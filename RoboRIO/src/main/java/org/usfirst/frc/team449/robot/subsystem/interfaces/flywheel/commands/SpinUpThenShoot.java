package org.usfirst.frc.team449.robot.subsystem.interfaces.flywheel.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.subsystem.interfaces.flywheel.SubsystemFlywheel;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.SubsystemIntake;

/**
 * Spin up the flywheel until it's at the target speed, then start feeding in balls.
 *
 * <p>Does not halt the spin-up process if the flywheel is signalled to stop while this command is
 * running. Consider decorating with {@link
 * Command#withInterrupt(java.util.function.BooleanSupplier)} with a test for flywheel state.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SpinUpThenShoot<T extends Subsystem & SubsystemFlywheel>
    extends SequentialCommandGroup {

  /**
   * Default constructor.
   *
   * <p>Requires the subsystem.
   *
   * @param flywheel The subsystem to execute this command on.
   * @param feeder
   */
  @JsonCreator
  public SpinUpThenShoot(
      @NotNull @JsonProperty(required = true) final T flywheel,
      @NotNull @JsonProperty(required = true) final SubsystemIntake feeder) {
    this.addRequirements(flywheel);
    this.addCommands(
        new SpinUpFlywheel(flywheel, feeder),
        new ParallelRaceGroup(
            new WaitUntilCommand(flywheel::isAtShootingSpeed),
            new WaitCommand(flywheel.getSpinUpTimeoutSecs())),
        new TurnAllOn(flywheel));
  }
}
