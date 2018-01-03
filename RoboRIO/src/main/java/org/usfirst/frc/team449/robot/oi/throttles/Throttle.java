package org.usfirst.frc.team449.robot.oi.throttles;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.usfirst.frc.team449.robot.generalInterfaces.loggable.Loggable;
import org.usfirst.frc.team449.robot.generalInterfaces.updatable.Updatable;

/**
 * An object representing an axis of a stick on a joystick.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface Throttle extends Updatable, Loggable {

    /**
     * Get the output of the throttle this object represents.
     *
     * @return The output from [-1, 1].
     */
    double getValue();

    /**
     * Get the cached output of the throttle this object represents.
     *
     * @return The output from [-1, 1].
     */
    double getValueCached();
}
