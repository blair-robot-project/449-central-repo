package org.usfirst.frc.team449.robot.commands.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import org.jetbrains.annotations.Nullable;

/** A conditional command that picks which command to run based on match time. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ConditionalCommandTimeBased extends ConditionalCommand {

  /**
   * Default constructor.
   *
   * @param beforeCommand The Command to execute before the given time. Can be null to not run a
   *     command before.
   * @param afterCommand The Command to execute after the given time. Can be null to not run a
   *     command after.
   * @param matchTimeSecs The time, in seconds until the end of the current period, when which
   *     command to run changes.
   */
  @JsonCreator
  public ConditionalCommandTimeBased(
      @Nullable final Command beforeCommand,
      @Nullable final Command afterCommand,
      @JsonProperty(required = true) final double matchTimeSecs) {
    super(beforeCommand, afterCommand, () -> Timer.getMatchTime() > matchTimeSecs);
  }
}
