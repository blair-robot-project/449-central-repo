package org.usfirst.frc.team449.robot.jacksonWrappers

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import edu.wpi.first.wpilibj.AnalogInput

/**
 * Wrapper for an [AnalogInput] pressure sensor that returns a voltage linearly proportional
 * to pressure.
 *
 * @param port The port of the sensor.
 * @param oversampleBits The number of oversample bits.
 * @param averageBits The number of averaging bits.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator::class)
class PressureSensor @JsonCreator constructor(
    @JsonProperty(required = true) port: Int,
    @JsonProperty(required = true) oversampleBits: Int,
    @JsonProperty(required = true) averageBits: Int
) {
    /** The AnalogInput this is a wrapper on.  */
    private val sensor: AnalogInput = AnalogInput(port)// these are constants given by REV, assuming 5.0V in

    /**
     * Returns the pressure measured by the sensor.
     *
     * @return pressure in PSI
     */
    val pressure: Double
        get() = (50.0 * sensor.averageVoltage
                - 25.0 // these are constants given by REV, assuming 5.0V in
                )

    init {
        sensor.oversampleBits = oversampleBits
        sensor.averageBits = averageBits
    }
}