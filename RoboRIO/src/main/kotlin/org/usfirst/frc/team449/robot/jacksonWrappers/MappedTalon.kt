package org.usfirst.frc.team449.robot.jacksonWrappers

import com.ctre.phoenix.motorcontrol.*
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward
import edu.wpi.first.wpilibj.shuffleboard.EventImportance
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard
import io.github.oblarg.oblog.annotations.Log
import org.jetbrains.annotations.Contract
import org.usfirst.frc.team449.robot.components.RunningLinRegComponent
import org.usfirst.frc.team449.robot.generalInterfaces.motors.smart.SmartMotor
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable.Gear
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable.PerGearSettings

/**
 * Component wrapper on the CTRE [TalonSRX], with unit conversions to/from FPS built in. Every
 * non-unit-conversion in this class takes arguments in post-gearing FPS.
 *
 * @param port CAN port of this Talon.
 * @param name The talon's name, used for logging purposes. Defaults to talon_portnum
 * @param reverseOutput Whether to reverse the output.
 * @param enableBrakeMode Whether to brake or coast when stopped.
 * @param voltagePerCurrentLinReg The component for doing linear regression to find the
 * resistance.
 * @param PDP The PDP this Talon is connected to.
 * @param fwdLimitSwitchNormallyOpen Whether the forward limit switch is normally open or closed.
 * If this is null, the forward limit switch is disabled.
 * @param revLimitSwitchNormallyOpen Whether the reverse limit switch is normally open or closed.
 * If this is null, the reverse limit switch is disabled.
 * @param remoteLimitSwitchID The CAN port of the Talon the limit switch to use for this talon is
 * plugged into, or null to not use a limit switch or use the limit switch plugged into this
 * talon.
 * @param fwdSoftLimit The forward software limit, in feet. If this is null, the forward software
 * limit is disabled. Ignored if there's no encoder.
 * @param revSoftLimit The reverse software limit, in feet. If this is null, the reverse software
 * limit is disabled. Ignored if there's no encoder.
 * @param postEncoderGearing The coefficient the output changes by after being measured by the
 * encoder, e.g. this would be 1/70 if there was a 70:1 gearing between the encoder and the
 * final output. Defaults to 1.
 * @param unitPerRotation The number of feet travelled per rotation of the motor this is attached
 * to. Defaults to 1.
 * @param currentLimit The max amps this device can draw. If this is null, no current limit is
 * used.
 * @param enableVoltageComp Whether or not to use voltage compensation. Defaults to false.
 * @param voltageCompSamples The number of 1-millisecond samples to use for voltage compensation.
 * Defaults to 32.
 * @param feedbackDevice The type of encoder used to measure the output velocity of this motor.
 * Can be null if there is no encoder attached to this Talon.
 * @param encoderCPR The counts per rotation of the encoder on this Talon. Can be null if
 * feedbackDevice is, but otherwise must have a value.
 * @param reverseSensor Whether or not to reverse the reading from the encoder on this Talon.
 * Ignored if feedbackDevice is null. Defaults to false.
 * @param perGearSettings The settings for each gear this motor has. Can be null to use default
 * values and gear # of zero. Gear numbers can't be repeated.
 * @param startingGear The gear to start in. Can be null to use startingGearNum instead.
 * @param startingGearNum The number of the gear to start in. Ignored if startingGear isn't null.
 * Defaults to the lowest gear.
 * @param statusFrameRatesMillis The update rates, in millis, for each of the Talon status frames.
 * @param controlFrameRatesMillis The update rate, in milliseconds, for each of the control frame.
 * @param slaveTalons The other [TalonSRX]s that are slaved to this one.
 * @param slaveVictors The [com.ctre.phoenix.motorcontrol.can.VictorSPX]s that are slaved to
 * this Talon.
 * @param slaveSparks The Spark/Neo combinations slaved to this Talon.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator::class)
class MappedTalon @JsonCreator constructor(
    @JsonProperty(required = true) port: Int,
    name: String?,
    reverseOutput: Boolean,
    @JsonProperty(required = true) enableBrakeMode: Boolean,
    voltagePerCurrentLinReg: RunningLinRegComponent?,
    PDP: PDP?,
    fwdLimitSwitchNormallyOpen: Boolean?,
    revLimitSwitchNormallyOpen: Boolean?,
    remoteLimitSwitchID: Int?,
    fwdSoftLimit: Double?,
    revSoftLimit: Double?,
    postEncoderGearing: Double?,
    unitPerRotation: Double?,
    currentLimit: Int?,
    enableVoltageComp: Boolean,
    voltageCompSamples: Int?,
    feedbackDevice: FeedbackDevice?,
    encoderCPR: Int?,
    reverseSensor: Boolean,
    perGearSettings: List<PerGearSettings>?,
    startingGear: Gear?,
    startingGearNum: Int?,
    statusFrameRatesMillis: Map<StatusFrameEnhanced?, Int?>?,
    controlFrameRatesMillis: Map<ControlFrame?, Int?>?,
    slaveTalons: List<SlaveTalon>?,
    slaveVictors: List<SlaveVictor>?,
    slaveSparks: List<SlaveSparkMax>?
) : SmartMotor {
    /** The CTRE CAN Talon SRX that this class is a wrapper on  */
    private val canTalon: TalonSRX = TalonSRX(port)

    /** The PDP this Talon is connected to.  */
    @Log.Exclude
    private val PDP: PDP?

    /** The counts per rotation of the encoder being used, or null if there is no encoder.  */
    private val encoderCPR: Int?

    /**
     * The number of feet travelled per rotation of the motor this is attached to, or null if there is
     * no encoder.
     */
    private val unitPerRotation: Double = unitPerRotation ?: 1.0

    /** A list of all the gears this robot has and their settings.  */
    private val perGearSettings: MutableMap<Int, PerGearSettings>

    /** The talon's name, used for logging purposes.  */
    private val name: String = name ?: "talon_$port"

    /** The component for doing linear regression to find the resistance.  */
    private val voltagePerCurrentLinReg: RunningLinRegComponent?

    /** Whether the forwards or reverse limit switches are normally open or closed, respectively.  */
    private val fwdLimitSwitchNormallyOpen: Boolean
    private val revLimitSwitchNormallyOpen: Boolean

    /** The settings currently being used by this Talon.  */
    private var currentGearSettings: PerGearSettings
    var faults = Faults()

    /**
     * The coefficient the output changes by after being measured by the encoder, e.g. this would be
     * 1/70 if there was a 70:1 gearing between the encoder and the final output.
     */
    private var postEncoderGearing: Double

    /** The most recently set setpoint.  */
    private var setpoint: Double = 0.0

    /** RPS as used in a unit conversion method. Field to avoid garbage collection.  */
    private var RPS: Double? = null

    /** The setpoint in native units. Field to avoid garbage collection.  */
    private var nativeSetpoint = 0.0
    private var voltageCompEnabled = false

    /** Disables the motor, if applicable.  */
    override fun disable() {
        canTalon[ControlMode.Disabled] = 0.0
    }

    /**
     * Set the motor output voltage to a given percent of available voltage.
     *
     * @param percentVoltage percent of total voltage from [-1, 1]
     */
    override fun setPercentVoltage(percentVoltage: Double) {
        // Warn the user if they're setting Vbus to a number that's outside the range of values.
        var percentVoltage = percentVoltage
        if (Math.abs(percentVoltage) > 1.0) {
            Shuffleboard.addEventMarker(
                "WARNING: YOU ARE CLIPPING MAX PERCENT VBUS AT $percentVoltage",
                this.javaClass.simpleName,
                EventImportance.kNormal
            )
            // Logger.addEvent("WARNING: YOU ARE CLIPPING MAX PERCENT VBUS AT " + percentVoltage,
            // this.getClass());
            percentVoltage = Math.signum(percentVoltage)
        }
        setpoint = percentVoltage
        canTalon[ControlMode.PercentOutput] = percentVoltage
    }

    /** @return The gear this subsystem is currently in.
     */
    @Log
    override fun getGear(): Int {
        return currentGearSettings.gear
    }

    /**
     * Shift to a specific gear.
     *
     * @param gear Which gear to shift to.
     */
    override fun setGear(gear: Int) {
        // Set the current gear
        currentGearSettings = perGearSettings[gear]!!
        if (currentGearSettings.postEncoderGearing != null) {
            postEncoderGearing = currentGearSettings.postEncoderGearing!!
        }

        // Set max voltage
        canTalon.configPeakOutputForward(currentGearSettings.fwdPeakOutputVoltage / 12.0, 0)
        canTalon.configPeakOutputReverse(currentGearSettings.revPeakOutputVoltage / 12.0, 0)

        // Set min voltage
        canTalon.configNominalOutputForward(
            currentGearSettings.fwdNominalOutputVoltage / 12.0, 0
        )
        canTalon.configNominalOutputReverse(
            currentGearSettings.revNominalOutputVoltage / 12.0, 0
        )
        if (currentGearSettings.rampRate != null) {
            // Set ramp rate, converting from volts/sec to seconds until 12 volts.
            canTalon.configClosedloopRamp(1 / (currentGearSettings.rampRate!! / 12.0), 0)
            canTalon.configOpenloopRamp(1 / (currentGearSettings.rampRate!! / 12.0), 0)
        } else {
            canTalon.configClosedloopRamp(0.0, 0)
            canTalon.configOpenloopRamp(0.0, 0)
        }

        // Set PID stuff
        // Slot 0 velocity gains. We don't set F yet because that changes based on setpoint.
        canTalon.config_kP(0, currentGearSettings.kP, 0)
        canTalon.config_kI(0, currentGearSettings.kI, 0)
        canTalon.config_kD(0, currentGearSettings.kD, 0)
    }

    /**
     * Convert from native units read by an encoder to feet moved. Note this DOES account for
     * post-encoder gearing.
     *
     * @param nativeUnits A distance native units as measured by the encoder.
     * @return That distance in feet, or null if no encoder CPR was given.
     */
    override fun encoderToUnit(nativeUnits: Double): Double {
        return if (encoderCPR == null) {
            Double.NaN
        } else nativeUnits / (encoderCPR * 4) * postEncoderGearing * unitPerRotation
    }

    /**
     * Convert a distance from feet to encoder reading in native units. Note this DOES account for
     * post-encoder gearing.
     *
     * @param feet A distance in feet.
     * @return That distance in native units as measured by the encoder, or null if no encoder CPR was
     * given.
     */
    override fun unitToEncoder(feet: Double): Double {
        return if (encoderCPR == null) {
            Double.NaN
        } else feet / unitPerRotation * (encoderCPR * 4) / postEncoderGearing
    }

    /**
     * Converts the velocity read by the talon's getVelocity() method to the FPS of the output shaft.
     * Note this DOES account for post-encoder gearing.
     *
     * @param encoderReading The velocity read from the encoder with no conversions.
     * @return The velocity of the output shaft, in FPS, when the encoder has that reading, or null if
     * no encoder CPR was given.
     */
    override fun encoderToUPS(encoderReading: Double): Double {
        RPS = nativeToRPS(encoderReading)
        return if (RPS == null) {
            Double.NaN
        } else RPS!! * postEncoderGearing * unitPerRotation
    }

    /**
     * Converts from the velocity of the output shaft to what the talon's getVelocity() method would
     * read at that velocity. Note this DOES account for post-encoder gearing.
     *
     * @param UPS The velocity of the output shaft, in FPS.
     * @return What the raw encoder reading would be at that velocity, or null if no encoder CPR was
     * given.
     */
    override fun UPSToEncoder(UPS: Double): Double {
        return RPSToNative(UPS / postEncoderGearing / unitPerRotation)
    }

    /**
     * Convert from CANTalon native velocity units to output rotations per second. Note this DOES NOT
     * account for post-encoder gearing.
     *
     * @param nat A velocity in CANTalon native units.
     * @return That velocity in RPS, or null if no encoder CPR was given.
     */
    @Contract(pure = true)
    override fun nativeToRPS(nat: Double): Double? {
        return if (encoderCPR == null) {
            null
        } else nat / (encoderCPR * 4) * 10
        // 4 edges per count, and 10 100ms per second.
    }

    /**
     * Convert from output RPS to the CANTalon native velocity units. Note this DOES NOT account for
     * post-encoder gearing.
     *
     * @param RPS The RPS velocity you want to convert.
     * @return That velocity in CANTalon native units, or null if no encoder CPR was given.
     */
    @Contract(pure = true)
    override fun RPSToNative(RPS: Double): Double {
        return if (encoderCPR == null) {
            Double.NaN
        } else RPS / 10 * (encoderCPR * 4)
        // 4 edges per count, and 10 100ms per second.
    }

    /** @return Total ticks travelled for debug purposes
     */
    override fun encoderPosition(): Double {
        return canTalon.selectedSensorPosition
    }

    override fun setVoltage(volts: Double) {
        if (voltageCompEnabled) {
            setPercentVoltage(volts / 12.0)
        } else {
            setPercentVoltage(volts / batteryVoltage)
        }
    }

    /**
     * Set a position setpoint for the Talon.
     *
     * @param feet An absolute position setpoint, in feet.
     */
    override fun setPositionSetpoint(feet: Double) {
        setpoint = feet
        nativeSetpoint = unitToEncoder(feet)
        canTalon.config_kF(0, 0.0)
        canTalon[ControlMode.Position, nativeSetpoint, DemandType.ArbitraryFeedForward] =
            currentGearSettings.feedForwardCalculator.ks / 12.0
    }

    /** @return Ticks per 100ms for debug purposes
     */
    override fun encoderVelocity(): Double {
        return canTalon.selectedSensorVelocity
    }

    /**
     * Get the velocity of the CANTalon in FPS.
     *
     * @return The CANTalon's velocity in FPS, or null if no encoder CPR was given.
     */
    override fun getVelocity(): Double {
        return encoderToUPS(canTalon.getSelectedSensorVelocity(0))
    }

    /**
     * Set the velocity for the motor to go at.
     *
     * @param velocity the desired velocity, on [-1, 1].
     */
    override fun setVelocity(velocity: Double) {
        if (currentGearSettings.maxSpeed != null) {
            setVelocityUPS(velocity * currentGearSettings.maxSpeed!!)
        } else {
            setPercentVoltage(velocity)
        }
    }

    /**
     * Give a velocity closed loop setpoint in FPS.
     *
     * @param velocity velocity setpoint in FPS.
     */
    override fun setVelocityUPS(velocity: Double) {
        nativeSetpoint = UPSToEncoder(velocity)
        setpoint = velocity
        canTalon.config_kF(0, 0.0, 0)
        canTalon[ControlMode.Velocity, nativeSetpoint, DemandType.ArbitraryFeedForward] =
            currentGearSettings.feedForwardCalculator.calculate(velocity) / 12.0
    }

    /**
     * Get the current closed-loop velocity error in FPS. WARNING: will give garbage if not in
     * velocity mode.
     *
     * @return The closed-loop error in FPS, or null if no encoder CPR was given.
     */
    @Log
    override fun getError(): Double {
        return if (canTalon.controlMode == ControlMode.Velocity) {
            encoderToUPS(canTalon.getClosedLoopError(0))
        } else {
            encoderToUnit(canTalon.getClosedLoopError(0))
        }
    }

    /**
     * Get the current velocity setpoint of the Talon in FPS, the position setpoint in feet
     *
     * @return The setpoint in sensible units for the current control mode.
     */
    @Log
    override fun getSetpoint(): Double {
        return setpoint
    }

    /**
     * Get the voltage the Talon is currently drawing from the PDP.
     *
     * @return Voltage in volts.
     */
    @Log
    override fun getOutputVoltage(): Double {
        return canTalon.motorOutputVoltage
    }

    /**
     * Get the voltage available for the Talon.
     *
     * @return Voltage in volts.
     */
    @Log
    override fun getBatteryVoltage(): Double {
        return canTalon.busVoltage
    }

    /**
     * Get the current the Talon is currently drawing from the PDP.
     *
     * @return Current in amps.
     */
    @Log
    override fun getOutputCurrent(): Double {
        return canTalon.supplyCurrent
    }

    /**
     * Get the current control mode of the Talon. Please don't use this for anything other than
     * logging.
     *
     * @return Control mode as a string.
     */
    override fun getControlMode(): String {
        return canTalon.controlMode.name
    }

    /**
     * Set the velocity scaled to a given gear's max velocity. Used mostly when autoshifting.
     *
     * @param velocity The velocity to go at, from [-1, 1], where 1 is the max speed of the given
     * gear.
     * @param gear The number of the gear to use the max speed from to scale the velocity.
     */
    override fun setGearScaledVelocity(velocity: Double, gear: Int) {
        if (currentGearSettings.maxSpeed != null) {
            setVelocityUPS(currentGearSettings.maxSpeed!! * velocity)
        } else {
            setPercentVoltage(velocity)
        }
    }

    /**
     * Set the velocity scaled to a given gear's max velocity. Used mostly when autoshifting.
     *
     * @param velocity The velocity to go at, from [-1, 1], where 1 is the max speed of the given
     * gear.
     * @param gear The gear to use the max speed from to scale the velocity.
     */
    override fun setGearScaledVelocity(velocity: Double, gear: Gear) {
        this.setGearScaledVelocity(velocity, gear.numVal)
    }

    /** @return Feedforward calculator for this gear
     */
    override fun getCurrentGearFeedForward(): SimpleMotorFeedforward {
        return currentGearSettings.feedForwardCalculator
    }

    /** @return the position of the talon in feet, or null of inches per rotation wasn't given.
     */
    @Log
    override fun getPositionUnits(): Double {
        return encoderToUnit(canTalon.getSelectedSensorPosition(0))
    }

    /** Resets the position of the Talon to 0.  */
    override fun resetPosition() {
        canTalon.setSelectedSensorPosition(0.0, 0, 0)
    }

    /**
     * Get the status of the forwards limit switch.
     *
     * @return True if the forwards limit switch is closed, false if it's open or doesn't exist.
     */
    override fun getFwdLimitSwitch(): Boolean {
        return fwdLimitSwitchNormallyOpen == canTalon.sensorCollection.isFwdLimitSwitchClosed
    }

    /**
     * Get the status of the reverse limit switch.
     *
     * @return True if the reverse limit switch is closed, false if it's open or doesn't exist.
     */
    override fun getRevLimitSwitch(): Boolean {
        return (revLimitSwitchNormallyOpen
                == canTalon.sensorCollection.isRevLimitSwitchClosed)
    }

    override fun isInhibitedForward(): Boolean {
        canTalon.getFaults(faults)
        return faults.ForwardLimitSwitch
    }

    override fun isInhibitedReverse(): Boolean {
        canTalon.getFaults(faults)
        return faults.ReverseLimitSwitch
    }

    override fun getPort(): Int {
        return canTalon.deviceID
    }

    override fun configureLogName(): String {
        return name
    }

    init {
        // Instantiate the base CANTalon this is a wrapper on.
        // Set the name to the given one or to talon_portnum
        // Set this to false because we only use reverseOutput for slaves.
        canTalon.inverted = reverseOutput
        // Set brake mode
        canTalon.setNeutralMode(if (enableBrakeMode) NeutralMode.Brake else NeutralMode.Coast)
        // Reset the position
        resetPosition()
        this.PDP = PDP
        this.voltagePerCurrentLinReg = voltagePerCurrentLinReg

        // Set frame rates
        if (controlFrameRatesMillis != null) {
            for (controlFrame in controlFrameRatesMillis.keys) {
                canTalon.setControlFramePeriod(
                    controlFrame, controlFrameRatesMillis[controlFrame]!!
                )
            }
        }
        if (statusFrameRatesMillis != null) {
            for (statusFrame in statusFrameRatesMillis.keys) {
                canTalon.setStatusFramePeriod(statusFrame, statusFrameRatesMillis[statusFrame]!!, 0)
            }
        }

        // Initialize
        this.perGearSettings = HashMap()

        // If given no gear settings, use the default values.
        if (perGearSettings == null || perGearSettings.isEmpty()) {
            this.perGearSettings[0] = PerGearSettings()
        } else {
            for (settings in perGearSettings) {
                this.perGearSettings[settings.gear] = settings
            }
        }
        var currentGear: Int
        // If the starting gear isn't given, assume we start in low gear.
        if (startingGear == null) {
            if (startingGearNum == null) {
                currentGear = Int.MAX_VALUE
                for (gear in this.perGearSettings.keys) {
                    if (gear < currentGear) {
                        currentGear = gear
                    }
                }
            } else {
                currentGear = startingGearNum
            }
        } else {
            currentGear = startingGear.numVal
        }
        currentGearSettings = this.perGearSettings[currentGear]!!

        // Only enable the limit switches if it was specified if they're normally open or closed.
        if (fwdLimitSwitchNormallyOpen != null) {
            if (remoteLimitSwitchID != null) {
                canTalon.configForwardLimitSwitchSource(
                    RemoteLimitSwitchSource.RemoteTalonSRX,
                    if (fwdLimitSwitchNormallyOpen) LimitSwitchNormal.NormallyOpen else LimitSwitchNormal.NormallyClosed,
                    remoteLimitSwitchID,
                    0
                )
            } else {
                canTalon.configForwardLimitSwitchSource(
                    LimitSwitchSource.FeedbackConnector,
                    if (fwdLimitSwitchNormallyOpen) LimitSwitchNormal.NormallyOpen else LimitSwitchNormal.NormallyClosed,
                    0
                )
            }
            this.fwdLimitSwitchNormallyOpen = fwdLimitSwitchNormallyOpen
        } else {
            canTalon.configForwardLimitSwitchSource(
                LimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled, 0
            )
            this.fwdLimitSwitchNormallyOpen = true
        }
        if (revLimitSwitchNormallyOpen != null) {
            if (remoteLimitSwitchID != null) {
                canTalon.configReverseLimitSwitchSource(
                    RemoteLimitSwitchSource.RemoteTalonSRX,
                    if (revLimitSwitchNormallyOpen) LimitSwitchNormal.NormallyOpen else LimitSwitchNormal.NormallyClosed,
                    remoteLimitSwitchID,
                    0
                )
            } else {
                canTalon.configReverseLimitSwitchSource(
                    LimitSwitchSource.FeedbackConnector,
                    if (revLimitSwitchNormallyOpen) LimitSwitchNormal.NormallyOpen else LimitSwitchNormal.NormallyClosed,
                    0
                )
            }
            this.revLimitSwitchNormallyOpen = revLimitSwitchNormallyOpen
        } else {
            canTalon.configReverseLimitSwitchSource(
                LimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled, 0
            )
            this.revLimitSwitchNormallyOpen = true
        }

        // Set up the feedback device if it exists.
        if (feedbackDevice != null) {
            // CTRE encoder use RPM instead of native units, and can be used as QuadEncoders, so we switch
            // them to avoid
            // having to support RPM.
            if (feedbackDevice == FeedbackDevice.CTRE_MagEncoder_Absolute || feedbackDevice == FeedbackDevice.CTRE_MagEncoder_Relative) {
                canTalon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0)
            } else {
                canTalon.configSelectedFeedbackSensor(feedbackDevice, 0, 0)
            }
            this.encoderCPR = encoderCPR
            canTalon.setSensorPhase(reverseSensor)

            // Only enable the software limits if they were given a value and there's an encoder.
            if (fwdSoftLimit != null) {
                canTalon.configForwardSoftLimitEnable(true, 0)
                canTalon.configForwardSoftLimitThreshold(unitToEncoder(fwdSoftLimit), 0)
            } else {
                canTalon.configForwardSoftLimitEnable(false, 0)
            }
            if (revSoftLimit != null) {
                canTalon.configReverseSoftLimitEnable(true, 0)
                canTalon.configReverseSoftLimitThreshold(unitToEncoder(revSoftLimit), 0)
            } else {
                canTalon.configReverseSoftLimitEnable(false, 0)
            }
        } else {
            this.encoderCPR = null
            canTalon.configSelectedFeedbackSensor(FeedbackDevice.None, 0, 0)
        }

        // postEncoderGearing defaults to 1
        this.postEncoderGearing = postEncoderGearing ?: 1.0

        // Set up gear-based settings.
        this.gear = currentGear

        // Set the current limit if it was given
        if (currentLimit != null) {
            canTalon.configContinuousCurrentLimit(currentLimit, 0)
            canTalon.configPeakCurrentDuration(0, 0)
            canTalon.configPeakCurrentLimit(0, 0) // No duration
            canTalon.enableCurrentLimit(true)
        } else {
            // If we don't have a current limit, disable current limiting.
            canTalon.enableCurrentLimit(false)
        }

        // Enable or disable voltage comp
        if (enableVoltageComp) {
            canTalon.enableVoltageCompensation(true)
            canTalon.configVoltageCompSaturation(12.0, 0)
            voltageCompEnabled = true
        }
        val notNullVoltageCompSamples = voltageCompSamples ?: 32
        canTalon.configVoltageMeasurementFilter(notNullVoltageCompSamples, 0)

        // Use slot 0
        canTalon.selectProfileSlot(0, 0)
        if (slaveTalons != null) {
            // Set up slaves.
            for (slave in slaveTalons) {
                slave.setMaster(
                    port,
                    enableBrakeMode,
                    currentLimit,
                    if (enableVoltageComp) notNullVoltageCompSamples else null,
                    PDP,
                    voltagePerCurrentLinReg
                )
            }
        }
        if (slaveVictors != null) {
            // Set up slaves.
            for (slave in slaveVictors) {
                slave.setMaster(
                    canTalon, enableBrakeMode, if (enableVoltageComp) notNullVoltageCompSamples else null
                )
            }
        }
        if (slaveSparks != null) {
            for (slave in slaveSparks) {
                slave.setMasterPhoenix(port, enableBrakeMode)
            }
        }
        canTalon.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_10Ms)
        canTalon.configVelocityMeasurementWindow(10)
    }
}