package org.usfirst.frc.team449.robot.jacksonWrappers

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import edu.wpi.cscore.UsbCamera

/** A Jackson-compatible wrapper on the [UsbCamera].  */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator::class)
class MappedUsbCamera @JsonCreator constructor(
    @JsonProperty(required = true) name: String,
    @JsonProperty(required = true) devAddress: Int,
    @JsonProperty(required = true) width: Int,
    @JsonProperty(required = true) height: Int,
    @JsonProperty(required = true) fps: Int
) : UsbCamera(name, devAddress) {
    /**
     * Default constructor
     *
     * @param name The human-friendly name for this camera.
     * @param devAddress The address of this device in /dev/, e.g. this would be 0 for /dev/video0, 1
     * for /dev/video1.
     * @param width The width of this camera's output, in pixels. There's a minimum value for this
     * that WPILib won't let us go below, but I don't know what it is.
     * @param height The height of this camera's output, in pixels. There's a minimum value for this
     * that WPILib won't let us go below, but I don't know what it is.
     * @param fps The frames per second this camera tries to transmit. There's a minimum value for
     * this that WPILib won't let us go below, but I don't know what it is.
     */
    init {
        setResolution(width, height)
        setFPS(fps)

        // If we don't have the exposure be automatic, the camera will be super laggy. No idea why.
        setExposureAuto()
    }
}