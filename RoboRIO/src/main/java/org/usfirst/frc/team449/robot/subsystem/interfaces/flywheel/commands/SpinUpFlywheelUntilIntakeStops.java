package org.usfirst.frc.team449.robot.subsystem.interfaces.flywheel.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.PerpetualCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import java.util.Timer;
import java.util.TimerTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.commands.general.ConditionalCommandFunctional;
import org.usfirst.frc.team449.robot.subsystem.complex.shooter.LoggingFlywheel;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.IntakeSimple;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.SubsystemIntake;

/**
 * Just all the stuff Yueqin added to the map for the feeder put into one command, for ease of
 * readability TODO test this out
 */
// @JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SpinUpFlywheelUntilIntakeStops extends SequentialCommandGroup
    implements ISpinUpFlywheelCommand {

  Command x;

  /**
   * @param timeToStart how long the feeder waits to start. Not sure how necessary this is
   * @param spinUpFlywheelCommand
   * @param shooterFlywheel
   * @param feeder the feeder
   * @param timeToStop how long the feeder keeps running after feeder stops, in sec
   */
  @JsonCreator
  public SpinUpFlywheelUntilIntakeStops(
      @Nullable final Double timeToStart,
      @NotNull @JsonProperty(required = true) final SpinUpFlywheel spinUpFlywheelCommand,
      @NotNull @JsonProperty(required = true) final LoggingFlywheel shooterFlywheel,
      @NotNull @JsonProperty(required = true) final IntakeSimple feeder,
      @Nullable final Double timeToStop) {
    super(
        new WaitCommand(timeToStart == null ? 1.0 : timeToStart),
        makePerpetualCommand(spinUpFlywheelCommand, shooterFlywheel, feeder, timeToStop));
  }

  public static Command makePerpetualCommand(
      @NotNull final SpinUpFlywheel spinUpFlywheelCommand,
      @NotNull final LoggingFlywheel shooterFlywheel,
      @NotNull final IntakeSimple intake,
      @Nullable final Double timeToStop) {
    // todo should this be a perpetual command?
    System.out.println("Hello there!!!");
    return new PerpetualCommand(
        // TODO make this less complicated, this is even worse than before
        new ConditionalCommandFunctional(
            new CommandBase() {
              final Runnable endCommand =
                  () -> {
                    this.end(false);
                  };

              @Override
              public void execute() {
                // stop after timeToStop seconds
                final Timer timer = new Timer();
                timer.schedule(
                    new TimerTask() {
                      @Override
                      public void run() {
                        // TODO is it interrupted? Does making it false keep it from starting up
                        // again?
                        endCommand.run();
                      }
                    },
                    (long) (timeToStop == null ? 3000 : timeToStop * 1000L));
              }
            },
            new ConditionalCommandFunctional(
                new TurnAllOn(shooterFlywheel),
                spinUpFlywheelCommand,
                shooterFlywheel::isConditionTrueCached,
                null),
            // whether or not it is off
            () -> intake.getMode().compareTo(SubsystemIntake.IntakeMode.OFF) == 0,
            null));
  }
}
