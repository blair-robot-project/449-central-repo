package org.usfirst.frc.team449.robot.jacksonWrappers

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.kauailabs.navx.frc.AHRS
import com.kauailabs.navx.frc.AHRS.SerialDataType
import edu.wpi.first.wpilibj.SPI
import edu.wpi.first.wpilibj.SerialPort
import io.github.oblarg.oblog.Loggable
import io.github.oblarg.oblog.annotations.Log
import org.jetbrains.annotations.Contract
import org.usfirst.frc.team449.robot.generalInterfaces.updatable.Updatable

/**
 * A Jackson-compatible, invertible wrapper for the NavX.
 * @param port The port the NavX is plugged into. It seems like only kMXP (the port on the RIO)
 * works.
 * @param invertYaw Whether or not to invert the yaw axis. Defaults to true.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator::class)
class MappedAHRS @JsonCreator constructor(
    @JsonProperty(required = true) port: SerialPort.Port, invertYaw: Boolean?
) : Updatable, Loggable {
    /** The AHRS this class is a wrapper on.  */
    private var ahrs: AHRS =
        if (port == SerialPort.Port.kMXP) AHRS(SPI.Port.kMXP)
        else AHRS(port, SerialDataType.kProcessedData, 100.toByte())

    /** A multiplier for the yaw angle. -1 to invert, 1 to not.  */
    private val invertYaw =
        if (invertYaw == null || invertYaw) -1
        else 1

    /** The 9-axis heading value to return. Field to avoid garbage collection.  */
    private var toRet = 0.0

    /** The cached heading value, in degrees from [-180, 180].  */
    @get:Log
    var cachedHeading = 0.0
        private set

    /**
     * The cached total angular displacement. Differs from getHeading because it doesn't limit
     * angle.
     */
    @get:Log
    var cachedAngularDisplacement = 0.0
        private set

    /**
     * The cached angular yaw velocity in degrees/sec.
     */
    @get:Log
    var cachedAngularVelocity = 0.0
        private set

    /**
     * The cached, linear, absolute X acceleration of the robot, relative to the field, in feet/(sec^2).
     */
    @get:Log
    var cachedXAccel = 0.0
        private set

    /**
     * The cached, linear, absolute Y acceleration of the robot, relative to the field, in feet/(sec^2).
     */
    @get:Log
    var cachedYAccel = 0.0
        private set

    /**
     * The cached pitch value, in degrees from [-180, 180].
     */
    @get:Log
    var cachedPitch = 0.0
        private set

    /**
     * Get the current total angular displacement. Differs from getHeading because it doesn't limit
     * angle.
     *
     * @return The angular displacement, in degrees.
     */
    val angularDisplacement: Double
        get() = ahrs.angle * invertYaw

    /**
     * Get the current angular yaw velocity.
     *
     * @return The angular yaw velocity, in degrees/sec.
     */
    val angularVelocity: Double
        get() = ahrs.rate * invertYaw

    /**
     * Get the absolute X acceleration of the robot, relative to the field.
     *
     * @return Linear X acceleration, in feet/(sec^2)
     */
    val xAccel: Double
        get() = gsToFeetPerSecondSquared(ahrs.worldLinearAccelX.toDouble())

    /**
     * Get the absolute Y acceleration of the robot, relative to the field.
     *
     * @return Linear Y acceleration, in feet/(sec^2)
     */
    val yAccel: Double
        get() = gsToFeetPerSecondSquared(ahrs.worldLinearAccelY.toDouble())

    /**
     * Get the pitch value.
     *
     * @return The pitch, in degrees from [-180, 180]
     */
    val pitch: Double
        get() = ahrs.pitch.toDouble()

    /**
     * Get the current yaw value.
     *
     * @return The heading, in degrees from [-180, 180]
     */
    fun getHeading(): Double {
        toRet = (invertYaw * ahrs.yaw).toDouble()
        //        toRet = Math.IEEEremainder(toRet, 360);
        return toRet
    }

    /**
     * Set the current yaw value.
     *
     * @param headingDegrees An angle in degrees, from [-180, 180], to set the heading to.
     */
    fun setHeading(headingDegrees: Double) {
        ahrs.angleAdjustment = headingDegrees
    }

    /** Updates all cached values with current ones.  */
    override fun update() {
        cachedHeading = getHeading()
        cachedAngularDisplacement = angularDisplacement
        cachedAngularVelocity = angularVelocity
        cachedXAccel = xAccel
        cachedYAccel = yAccel
        cachedPitch = pitch
    }

    companion object {
        /**
         * Convert from gs (acceleration due to gravity) to feet/(second^2).
         *
         * @param accelGs An acceleration in gs.
         * @return That acceleration in feet/(sec^2)
         */
        @Contract(pure = true)
        @JvmStatic
        private fun gsToFeetPerSecondSquared(accelGs: Double): Double {
            return accelGs * 32.17 // Wolfram alpha said so
        }
    }
}