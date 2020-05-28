package org.usfirst.frc.team449.robot.subsystem.interfaces.flywheel.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.subsystem.interfaces.flywheel.SubsystemFlywheel;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.SubsystemIntake;

/** Toggle whether or not the subsystem is firing. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public final class ToggleShooting<T extends Subsystem & SubsystemFlywheel>
    extends InstantCommand { // TODO What's with the generic constraint antics? Can't we just have
                             // the subsystems extend Subsystem?
  /**
   * Default constructor.
   *
   * @param subsystem The subsystem to execute this command on.
   */
  @JsonCreator
  public ToggleShooting(
      @NotNull @JsonProperty(required = true) final T subsystem,
      @NotNull @JsonProperty(required = true) final SubsystemIntake feeder) {
    super(
        () -> {
          final CommandBase commandToSchedule;

          switch (subsystem.getFlywheelState()) {
            case OFF:
              commandToSchedule = new SpinUpThenShoot(subsystem, feeder);
              break;
            case SHOOTING:
            case SPINNING_UP:
              commandToSchedule = new TurnAllOffWithRequires<>(subsystem);
              break;
            default:
              throw new RuntimeException(
                  "Switch statement fall-through on enum type "
                      + SubsystemFlywheel.FlywheelState.class.getSimpleName());
          }

          commandToSchedule.schedule(true);
        });
  }
}
