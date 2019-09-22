package org.usfirst.frc.team449.robot.commands.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import org.jetbrains.annotations.Nullable;

/**
 * A conditional command that picks which command to run based on match time.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ConditionalCommandTimeBased extends ConditionalCommand {

    /**
     * The time when which command to run changes.
     */
    private final double matchTimeSecs;

    /**
     * Default constructor.
     *
     * @param beforeCommand The Command to execute before the given time. Can be null to not run a command before.
     * @param afterCommand  The Command to execute after the given time. Can be null to not run a command after.
     * @param matchTimeSecs The time, in seconds until the end of the current period, when which command to run
     *                      changes.
     */
    @JsonCreator
    public ConditionalCommandTimeBased(@Nullable Command beforeCommand,
                                       @Nullable Command afterCommand,
                                       @JsonProperty(required = true) double matchTimeSecs) {
        super(beforeCommand, afterCommand);
        this.matchTimeSecs = matchTimeSecs;
    }

    /**
     * The Condition to test to determine which Command to run.
     *
     * @return true if before the given time, false otherwise
     */
    @Override
    protected boolean condition() {
        return Timer.getMatchTime() > matchTimeSecs;
    }
}
