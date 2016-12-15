package org.usfirst.frc.team449.robot;

/**
 * Created by Blair Robot Project on 12/8/2016.
 */
public class FatalParserException extends RuntimeException {
    /**
     * Constructs a FatalParserException with an explanatory message.
     *
     * @param message Detail about the reason for the exception.
     */
    public FatalParserException(String message) {
        super(message);
    }

    /**
     * Constructs a FatalParserException with an explanatory message and
     * cause.
     *
     * @param message Detail about the reason for the exception.
     * @param cause   The cause.
     */
    public FatalParserException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new FatalParserException with the specified cause.
     *
     * @param cause The cause.
     */
    public FatalParserException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
