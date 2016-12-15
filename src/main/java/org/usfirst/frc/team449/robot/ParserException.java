package org.usfirst.frc.team449.robot;

/**
 * Created by Blair Robot Project on 12/8/2016.
 */
public class ParserException  extends RuntimeException {
    /**
     * Constructs a ParserException with an explanatory message.
     *
     * @param message Detail about the reason for the exception.
     */
    public ParserException(String message) {
        super(message);
    }

    /**
     * Constructs a ParserException with an explanatory message and cause.
     *
     * @param message Detail about the reason for the exception.
     * @param cause   The cause.
     */
    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new ParserException with the specified cause.
     *
     * @param cause The cause.
     */
    public ParserException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
