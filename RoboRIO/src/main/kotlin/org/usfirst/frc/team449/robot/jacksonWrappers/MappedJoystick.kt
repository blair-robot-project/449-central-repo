package org.usfirst.frc.team449.robot.jacksonWrappers

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import edu.wpi.first.wpilibj.Joystick
import edu.wpi.first.wpilibj.RobotBase
import org.usfirst.frc.team449.robot.generalInterfaces.rumbleable.Rumbleable
import org.usfirst.frc.team449.robot.jacksonWrappers.simulated.JoystickSimulated
import org.usfirst.frc.team449.robot.other.Util

/** A Jackson-compatible wrapper on a [Joystick].  */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator::class)
open class MappedJoystick
/**
 * Default constructor
 *
 * @param port The USB port of this joystick, on [0, 5].
 */
    (@JsonProperty(required = true) port: Int) : Joystick(port), Rumbleable {
    /**
     * Rumble at a given strength on each side of the device.
     *
     * @param left The strength to rumble the left side, on [-1, 1]
     * @param right The strength to rumble the right side, on [-1, 1]
     */
    override fun rumble(left: Double, right: Double) {
        setRumble(RumbleType.kLeftRumble, left)
        setRumble(RumbleType.kRightRumble, right)
    }

    companion object {
        /**
         * Whether to construct instances of [JoystickSimulated] instead of [MappedJoystick]
         * when the robot is running in a simulation.
         */
        private const val SIMULATE = true

        /**
         * Factory method to enable faking in simulation.
         *
         * @param port The USB port of this joystick, on [0, 5].
         */
        @JsonCreator
        fun create(@JsonProperty(required = true) port: Int): MappedJoystick {
            if (!SIMULATE || RobotBase.isReal()) {
                return MappedJoystick(port)
            }
            println(
                Util.getLogPrefix(MappedJoystick::class.java) + "Creating simulated joystick on port " + port
            )
            return JoystickSimulated(port)
        }
    }
}