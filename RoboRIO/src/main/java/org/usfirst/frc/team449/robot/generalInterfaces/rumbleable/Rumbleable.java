package org.usfirst.frc.team449.robot.generalInterfaces.rumbleable;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * An interface for any sort of OI device that can rumble to give feedback to the driver.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface Rumbleable {

    /**
     * Rumble at a given strength on each side of the device.
     *
     * @param left  The strength to rumble the left side, on [-1, 1]
     * @param right The strength to rumble the right side, on [-1, 1]
     */
    void rumble(double left, double right);

}
