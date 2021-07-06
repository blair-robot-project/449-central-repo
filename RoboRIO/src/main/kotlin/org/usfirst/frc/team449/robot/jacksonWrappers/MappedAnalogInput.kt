package org.usfirst.frc.team449.robot.jacksonWrappers

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import edu.wpi.first.wpilibj.AnalogInput
import io.github.oblarg.oblog.Loggable
import io.github.oblarg.oblog.annotations.Log
import org.usfirst.frc.team449.robot.generalInterfaces.updatable.Updatable

/**
 * A Jackson-friendly wrapper on WPILib's [AnalogInput].
 *
 * @param port The analog input port this object reads analog voltage from.
 * @param oversampleBits The sensor will be oversampled by 2^oversampleBits bits. Oversampling is
 * kinda confusing, so just read the wikipedia page on it. Defaults to 0.
 * @param averageBits The sensor output will be the average of 2^averageBits readings. Defaults to
 * 0.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator::class)
class MappedAnalogInput @JsonCreator constructor(
    @JsonProperty(required = true) port: Int,
    oversampleBits: Int,
    averageBits: Int
) : AnalogInput(port), Updatable, Loggable {
    /** The value of analog input, as a percent.  */
    @get:Log
    var percentValueCached = 0.0
        private set// Round to 3 decimal places and clip to between 0 and 1.

    /**
     * Get the percentage value of the analog input.
     *
     * @return The value of the analog input on [0,1], scaled so that 5 volts is 1 and 0 volts is 0.
     */
    @get:Log
    val percentValue: Double
        get() =// Round to 3 decimal places and clip to between 0 and 1.
            Math.min(Math.max(Math.round((averageValue - 55.0) / 64190.0 * 1000.0) / 1000.0, 0.0), 1.0)

    override fun update() {
        percentValueCached = percentValue
    }

    init {
        setOversampleBits(oversampleBits)
        setAverageBits(averageBits)
    }
}