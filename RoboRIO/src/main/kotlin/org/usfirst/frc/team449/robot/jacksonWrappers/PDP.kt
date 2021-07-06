package org.usfirst.frc.team449.robot.jacksonWrappers

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import edu.wpi.first.wpilibj.PowerDistributionPanel
import io.github.oblarg.oblog.Loggable
import io.github.oblarg.oblog.annotations.Log
import org.usfirst.frc.team449.robot.components.RunningLinRegComponent
import org.usfirst.frc.team449.robot.generalInterfaces.updatable.Updatable

/**
 * An object representing the [PowerDistributionPanel] that logs power, current, and
 * resistance.
 * @param canID CAN ID of the PDP. Defaults to 0.
 * @param voltagePerCurrentLinReg The component for doing linear regression to find the resistance.
 *
 * @property voltagePerCurrentLinReg The component for doing linear regression to find the resistance.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator::class)
class PDP @JsonCreator constructor(canID: Int, private val voltagePerCurrentLinReg: RunningLinRegComponent?) :
    Loggable, Updatable {
    /** The WPILib PDP this is a wrapper on.  */
    private val PDP: PowerDistributionPanel = PowerDistributionPanel(canID)

    /**
     * Query the input voltage of the PDP.
     *
     * @return The voltage of the PDP in volts
     */
    /** The cached values from the PDP object this wraps.  */
    @get:Log
    var voltage: Double = 0.0
        private set

    /**
     * Query the current of all monitored PDP channels (0-15).
     *
     * @return The current of all the channels in Amperes
     */
    @get:Log
    var totalCurrent: Double = 0.0
        private set

    /**
     * Query the temperature of the PDP.
     *
     * @return The temperature of the PDP in degrees Celsius.
     */
    @get:Log
    var temperature: Double = 0.0
        private set
    private var resistance: Double = 0.0
    private var unloadedVoltage: Double = 0.0

    /**
     * Get the resistance of the wires leading to the PDP.
     *
     * @return Resistance in ohms, or null if not calculating resistance.
     */
    @Log
    fun getResistance(): Double? {
        return if (voltagePerCurrentLinReg == null) null else resistance
    }

    /**
     * Get the voltage at the PDP when there's no load on the battery.
     *
     * @return Voltage in volts when there's 0 amps of current draw, or null if not calculating
     * resistance.
     */
    @Log
    fun getUnloadedVoltage(): Double? {
        return if (voltagePerCurrentLinReg == null) null else unloadedVoltage
    }

    /** Updates all cached values with current ones.  */
    override fun update() {
        this.totalCurrent = PDP.totalCurrent
        this.voltage = PDP.voltage
        this.temperature = PDP.temperature
        if (this.voltagePerCurrentLinReg != null) {
            this.voltagePerCurrentLinReg.addPoint(this.totalCurrent, this.voltage)
            this.unloadedVoltage = this.voltagePerCurrentLinReg.intercept
            this.resistance = -this.voltagePerCurrentLinReg.slope
        }
    }
}