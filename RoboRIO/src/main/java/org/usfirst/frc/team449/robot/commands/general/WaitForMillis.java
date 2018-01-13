package org.usfirst.frc.team449.robot.commands.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.other.Clock;

/**
 * A command that does nothing and finishes after a set number of milliseconds. For use to create a delay in sequential
 * CommandGroups.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class WaitForMillis extends Command {

    /**
     * How long this command takes to finish, in milliseconds.
     */
    private final long timeout;

    /**
     * The time this command started at.
     */
    private long startTime;

    /**
     * Default constructor
     *
     * @param time How long this command will take to finish, in milliseconds.
     */
    @JsonCreator
    public WaitForMillis(@JsonProperty(required = true) long time) {
        timeout = time;
    }

    /**
     * Store the start time.
     */
    @Override
    protected void initialize() {
        startTime = Clock.currentTimeMillis();
    }

    /**
     * Finish if the specified amount of time has passed.
     *
     * @return true if the specified number of milliseconds have passed since this command started, false otherwise.
     */
    @Override
    protected boolean isFinished() {
        return Clock.currentTimeMillis() - startTime >= timeout;
    }
}
