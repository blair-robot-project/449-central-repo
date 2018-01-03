package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.cscore.UsbCamera;
import org.jetbrains.annotations.NotNull;

/**
 * A Jackson-compatible wrapper on the {@link UsbCamera}.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MappedUsbCamera extends UsbCamera {

    /**
     * Default constructor
     *
     * @param name       The human-friendly name for this camera.
     * @param devAddress The address of  this device in /dev/, e.g. this would be 0 for /dev/video0, 1 for /dev/video1.
     * @param width      The width of this camera's output, in pixels. There's a minimum value for this that WPILib
     *                   won't let us go below, but I don't know what it is.
     * @param height     The height of this camera's output, in pixels. There's a minimum value for this that WPILib
     *                   won't let us go below, but I don't know what it is.
     * @param fps        The frames per second this camera tries to transmit. There's a minimum value for this that
     *                   WPILib won't let us go below, but I don't know what it is.
     */
    @JsonCreator
    public MappedUsbCamera(@NotNull @JsonProperty(required = true) String name,
                           @JsonProperty(required = true) int devAddress,
                           @JsonProperty(required = true) int width,
                           @JsonProperty(required = true) int height,
                           @JsonProperty(required = true) int fps) {
        super(name, devAddress);
        setResolution(width, height);
        setFPS(fps);

        //If we don't have the exposure be automatic, the camera will be super laggy. No idea why.
        setExposureAuto();
    }
}
