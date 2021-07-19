package org.usfirst.frc.team449.robot.auto.yearlyCommands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.StringIdGenerator;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import org.usfirst.frc.team449.robot.subsystem.intake.feeder2020.commands.DefaultFeederCommand;

import java.util.function.BooleanSupplier;

/**
 * The class that runs the autonomous command for the Galactic Search challenge in 2021. The logic
 * for that challenge was really complicated and the easiest implementation was this.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "@class")
@JsonIdentityInfo(generator = StringIdGenerator.class)
public class AutoSearchCommand2021 extends SequentialCommandGroup {

  BooleanSupplier ballFound;

  /**
   * @param goToCol3 The path going to the third column
   * @param redSecond The path to pick up the second red ball (after column 3)
   * @param redA The path to finish the redA sequence
   * @param redB The path to finish the redB sequence
   * @param blueStart The path to start a blue path (after not finding a red ball)
   * @param blueA The path to finish the blueA sequence
   * @param blueB The path to finish the blueB sequence
   * @param feederCommand The counting command, to be used as a {@link BooleanSupplier}
   */
  @JsonCreator
  public AutoSearchCommand2021(
      @JsonProperty(required = true) Command goToCol3,
      @JsonProperty(required = true) Command intakeStart,
      @JsonProperty(required = true) Command redSecond,
      @JsonProperty(required = true) Command redA,
      @JsonProperty(required = true) Command redB,
      @JsonProperty(required = true) Command blueStart,
      @JsonProperty(required = true) Command blueA,
      @JsonProperty(required = true) Command blueB,
      @JsonProperty(required = true) DefaultFeederCommand feederCommand) {
    ballFound = feederCommand::hasGotBall;
    addCommands(intakeStart);
    addCommands(goToCol3);
    addCommands(new WaitCommand(1.));
    addCommands(
        new ConditionalCommand( // Red or blue?
            new SequentialCommandGroup(
                redSecond,
                new WaitCommand(1.),
                new ConditionalCommand(redA, redB, ballFound)), // A or B?
            new SequentialCommandGroup(
                blueStart,
                new WaitCommand(1.),
                new ConditionalCommand(blueA, blueB, ballFound)), // A or B?
            ballFound));
  }
}
