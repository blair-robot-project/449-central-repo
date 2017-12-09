package org.usfirst.frc.team449.robot.other;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * A timer that checks if condition has been true for the past n seconds/milliseconds.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class BufferTimer {

    /**
     * How long the condition has to be true for, in milliseconds.
     */
    private final long bufferTime;

    /**
     * The time at which the condition most recently switched from false to true.
     */
    private long timeConditionBecameTrue;

    /**
     * The state of the condition the last time get() was called.
     */
    private boolean previousState;

    /**
     * Constructor for a time given in seconds.
     *
     * @param bufferTimeSeconds The amount of time the condition has to be true for, in seconds. Defaults to 0.
     */
    @JsonCreator
    public BufferTimer(double bufferTimeSeconds) {
        bufferTime = (long) (bufferTimeSeconds * 1000.);
    }

    /**
     * Constructor for a time given in milliseconds.
     *
     * @param bufferTimeMilliseconds The amount of time the condition has to be true for, in milliseconds.
     */
    public BufferTimer(long bufferTimeMilliseconds) {
        bufferTime = bufferTimeMilliseconds;
    }

    /**
     * Get whether the condition has been true for at least the specified amount of time.
     *
     * @param currentState The current state of the condition.
     * @return True if the condition has been true for the specified amount of time, false otherwise.
     */
    public boolean get(boolean currentState) {
        //If the condition just became true, store the current time.
        if (currentState && !previousState) {
            timeConditionBecameTrue = Clock.currentTimeMillis();
        }
        //Update previous state
        previousState = currentState;
        //Return true if the condition is currently true and has been true for bufferTime milliseconds.
        return currentState && Clock.currentTimeMillis() - timeConditionBecameTrue >= bufferTime;
    }
}
