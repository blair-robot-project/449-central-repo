package org.usfirst.frc.team449.robot.other;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;

/**
 * An logged event with a message, timestamp, and calling class.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class LogEvent {

    /**
     * The time, in milliseconds, at which this event was created.
     */
    private final long timeCalled;

    /**
     * The message of this event.
     */
    @NotNull
    private final String message;

    /**
     * The class that called this event.
     */
    @NotNull
    private final Class caller;

    /**
     * Default constructor.
     * <p>
     * Note to future people: Don't rewrite this to get the calling class from the stack trace. It's possible, and makes
     * the code cleaner than taking the calling class as an argument, but getting the stack trace actually takes Java a
     * little while, and considering how often this constructor is called, that would significantly slow us down.
     *
     * @param message The message of this event.
     * @param caller  The calling class. Should pretty much always be this.getClass().
     */
    @JsonCreator
    public LogEvent(@NotNull @JsonProperty(required = true) String message,
                    @NotNull @JsonProperty(required = true) Class caller) {
        timeCalled = Clock.currentTimeMillis();
        this.message = message;
        this.caller = caller;
    }

    /**
     * Turn this event into a string for logging.
     *
     * @return The time called, calling class, and message, comma-separated and in that order.
     */
    @NotNull
    public String toString() {
        return timeCalled + "," + caller.toString() + "," + message;
    }
}
