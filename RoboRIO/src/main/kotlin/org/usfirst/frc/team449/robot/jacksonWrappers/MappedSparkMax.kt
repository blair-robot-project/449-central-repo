package org.usfirst.frc.team449.robot.jacksonWrappers

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.revrobotics.*
import com.revrobotics.CANSparkMaxLowLevel.PeriodicFrame
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward
import edu.wpi.first.wpilibj.shuffleboard.EventImportance
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard
import io.github.oblarg.oblog.annotations.Log
import org.jetbrains.annotations.Contract
import org.usfirst.frc.team449.robot.generalInterfaces.motors.smart.SmartMotor
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable.Gear
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable.PerGearSettings

/**
 * SPARK MAX Controller
 *
 * @param port CAN port of this Spark.
 * @param name The Spark's name, used for logging purposes. Defaults to "spark_&gt;port&lt;"
 * @param reverseOutput Whether to reverse the output.
 * @param enableBrakeMode Whether to brake or coast when stopped.
 * @param PDP The PDP this Spark is connected to.
 * @param fwdLimitSwitchNormallyOpen Whether the forward limit switch is normally open or closed.
 * If this is null, the forward limit switch is disabled.
 * @param revLimitSwitchNormallyOpen Whether the reverse limit switch is normally open or closed.
 * If this is null, the reverse limit switch is disabled.
 * @param remoteLimitSwitchID The CAN ID the limit switch to use for this Spark is plugged into,
 * or null to not use a limit switch.
 * @param fwdSoftLimit The forward software limit, in feet. If this is null, the forward software
 * limit is disabled. Ignored if there's no encoder.
 * @param revSoftLimit The reverse software limit, in feet. If this is null, the reverse software
 * limit is disabled. Ignored if there's no encoder.
 * @param postEncoderGearing The coefficient the output changes by after being measured by the
 * encoder, e.g. this would be 1/70 if there was a 70:1 gearing between the encoder and the final
 * output. Defaults to 1.
 * @param unitPerRotation The number of feet travelled per rotation of the motor this is attached
 * to. Defaults to 1.
 * @param currentLimit The max amps this device can draw. If this is null, no current limit is
 * used.
 * @param enableVoltageComp Whether or not to use voltage compensation. Defaults to false.
 * @param perGearSettings The settings for each gear this motor has. Can be null to use default
 * values and gear # of zero. Gear numbers can't be repeated.
 * @param startingGear The gear to start in. Can be null to use startingGearNum instead.
 * @param startingGearNum The number of the gear to start in. Ignored if startingGear isn't null.
 * Defaults to the lowest gear.
 * @param statusFrameRatesMillis The update rates, in millis, for each of the status frames.
 * @param controlFrameRateMillis The update rate, in milliseconds, for each control frame.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator::class)
class MappedSparkMax @JsonCreator constructor(
    @JsonProperty(required = true) port: Int,
    name: String?,
    reverseOutput: Boolean,
    @JsonProperty(required = true) enableBrakeMode: Boolean,
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
    perGearSettings: List<PerGearSettings>?,
    startingGear: Gear?,
    startingGearNum: Int?,
    statusFrameRatesMillis: Map<PeriodicFrame?, Int?>?,
    controlFrameRateMillis: Int?,
    slaveSparks: List<SlaveSparkMax>?
) : SmartMotor {
    /** The PDP this Spark is connected to.  */
    @Log.Exclude
    private val PDP: PDP?

    /** The counts per rotation of the encoder being used, or null if there is no encoder.  */
    private val encoderCPR: Int?

    /**
     * The coefficient the output changes by after being measured by the encoder, e.g. this would be
     * 1/70 if there was a 70:1 gearing between the encoder and the final output.
     */
    @Log
    private var postEncoderGearing: Double

    /**
     * The number of feet travelled per rotation of the motor this is attached to, or null if there is
     * no encoder.
     */
    private val unitPerRotation: Double = unitPerRotation ?: 1.0

    /** A list of all the gears this robot has and their settings.  */
    private val perGearSettings: MutableMap<Int, PerGearSettings>

    /** Forward limit switch object  */
    private var forwardLimitSwitch: CANDigitalInput

    /** Reverse limit switch object  */
    private var reverseLimitSwitch: CANDigitalInput

    /** The Spark's name, used for logging purposes.  */
    private val name: String

    /** Whether the forwards or reverse limit switches are normally open or closed, respectively.  */
    private val fwdLimitSwitchNormallyOpen: Boolean
    private val revLimitSwitchNormallyOpen: Boolean

    /** REV brushless controller object  */
    private val spark: CANSparkMax = CANSparkMax(port, CANSparkMaxLowLevel.MotorType.kBrushless)

    /** REV provided encoder object  */
    private val canEncoder: CANEncoder

    /** REV provided PID Controller  */
    private val pidController: CANPIDController

    /** The settings currently being used by this Spark.  */
    private var currentGearSettings: PerGearSettings

    /** The control mode of the motor  */
    private var currentControlMode: ControlType? = null

    /** The most recently set setpoint.  */
    private var setpoint: Double = 0.0

    /** RPS as used in a unit conversion method. Field to avoid garbage collection.  */
    private var RPS: Double? = null

    /** The setpoint in native units. Field to avoid garbage collection.  */
    @Log
    private var nativeSetpoint: Double = 0.0

    override fun disable() {
        spark.disable()
    }

    override fun setPercentVoltage(percentVoltage: Double) {
        this.currentControlMode = ControlType.kVoltage
        // Warn the user if they're setting Vbus to a number that's outside the range of values.
        if (Math.abs(percentVoltage) > 1.0) {
            Shuffleboard.addEventMarker(
                "WARNING: YOU ARE CLIPPING MAX PERCENT VBUS AT $percentVoltage",
                this.javaClass.simpleName,
                EventImportance.kNormal
            )
            // Logger.addEvent("WARNING: YOU ARE CLIPPING MAX PERCENT VBUS AT " + percentVoltage,
            // this.getClass());
            this.setpoint = Math.signum(percentVoltage)
        } else {
            this.setpoint = percentVoltage
        }
        this.spark.set(this.setpoint)
    }

    @Log
    override fun getGear(): Int {
        return currentGearSettings.gear
    }

    override fun setGear(gear: Int) {
        // Set the current gear
        currentGearSettings = perGearSettings[gear]!!

        // note, no current limiting
        if (currentGearSettings.rampRate != null) {
            // Set ramp rate, converting from volts/sec to seconds until 12 volts.
            spark.closedLoopRampRate = 1 / (currentGearSettings.rampRate!! / 12.0)
            spark.openLoopRampRate = 1 / (currentGearSettings.rampRate!! / 12.0)
        } else {
            spark.closedLoopRampRate = 0.0
            spark.openLoopRampRate = 0.0
        }
        if (currentGearSettings.postEncoderGearing != null) {
            postEncoderGearing = currentGearSettings.postEncoderGearing!!
        }
        pidController.setP(currentGearSettings.kP, 0)
        pidController.setI(currentGearSettings.kI, 0)
        pidController.setD(currentGearSettings.kD, 0)
    }

    /**
     * Convert from native units read by an encoder to feet moved. Note this DOES account for
     * post-encoder gearing.
     *
     * @param revs revolutions measured by the encoder
     * @return That distance in feet, or null if no encoder CPR was given.
     */
    override fun encoderToUnit(revs: Double): Double {
        return revs * unitPerRotation * postEncoderGearing
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
        return feet / unitPerRotation / postEncoderGearing
    }

    /**
     * Converts the velocity read by the getVelocity() method to the FPS of the output shaft. Note
     * this DOES account for post-encoder gearing.
     *
     * @param encoderReading The velocity read from the encoder with no conversions.
     * @return The velocity of the output shaft, in FPS, when the encoder has that reading, or null if
     * no encoder CPR was given.
     */
    override fun encoderToUPS(encoderReading: Double): Double {
        RPS = nativeToRPS(encoderReading)
        return RPS!! * postEncoderGearing * unitPerRotation
    }

    /**
     * Converts from the velocity of the output shaft to what the getVelocity() method would read at
     * that velocity. Note this DOES account for post-encoder gearing.
     *
     * @param FPS The velocity of the output shaft, in FPS.
     * @return What the raw encoder reading would be at that velocity, or null if no encoder CPR was
     * given.
     */
    override fun UPSToEncoder(FPS: Double): Double {
        return RPSToNative(FPS / postEncoderGearing / unitPerRotation)
    }

    /**
     * Convert from native velocity units to output rotations per second. Note this DOES NOT account
     * for post-encoder gearing.
     *
     * @param nat A velocity in RPM
     * @return That velocity in RPS
     */
    @Contract(pure = true)
    override fun nativeToRPS(nat: Double): Double {
        return nat / 60.0
    }

    /**
     * Convert from output RPS to native velocity units. Note this DOES NOT account for post-encoder
     * gearing.
     *
     * @param RPS The RPS velocity you want to convert.
     * @return That velocity in RPM
     */
    @Contract(pure = true)
    override fun RPSToNative(RPS: Double): Double {
        return RPS * 60.0
    }

    /** @return Total revolutions for debug purposes
     */
    override fun encoderPosition(): Double {
        return canEncoder.position
    }

    override fun setVoltage(volts: Double) {
        spark.setVoltage(volts)
    }

    /**
     * Set a position setpoint for the Spark.
     *
     * @param feet An absolute position setpoint, in feet.
     */
    override fun setPositionSetpoint(feet: Double) {
        setpoint = feet
        nativeSetpoint = unitToEncoder(feet)
        pidController.ff = currentGearSettings.feedForwardCalculator.ks / 12.0
        pidController.setReference(
            nativeSetpoint,
            ControlType.kPosition,
            0,
            currentGearSettings.feedForwardCalculator.ks,
            CANPIDController.ArbFFUnits.kVoltage
        )
    }

    /** @return Current RPM for debug purposes
     */
    @Log
    override fun encoderVelocity(): Double {
        return canEncoder.velocity
    }

    /**
     * Get the velocity of the CANTalon in FPS.
     *
     * @return The CANTalon's velocity in FPS, or null if no encoder CPR was given.
     */
    @Log
    override fun getVelocity(): Double {
        return encoderToUPS(canEncoder.velocity)
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
        currentControlMode = ControlType.kVelocity
        nativeSetpoint = UPSToEncoder(velocity)
        setpoint = velocity
        pidController.ff = 0.0
        pidController.setReference(
            nativeSetpoint,
            ControlType.kVelocity,
            0,
            currentGearSettings.feedForwardCalculator.calculate(velocity),
            CANPIDController.ArbFFUnits.kVoltage
        )
    }

    @Log
    override fun getError(): Double {
        return getSetpoint() - this.velocity
    }

    @Log
    override fun getSetpoint(): Double {
        return setpoint
    }

    @Log
    override fun getOutputVoltage(): Double {
        return spark.appliedOutput * spark.busVoltage
    }

    @Log
    override fun getBatteryVoltage(): Double {
        return spark.busVoltage
    }

    @Log
    override fun getOutputCurrent(): Double {
        return spark.outputCurrent
    }

    override fun getControlMode(): String {
        return currentControlMode!!.name
    }

    override fun setGearScaledVelocity(velocity: Double, gear: Int) {
        if (currentGearSettings.maxSpeed != null) {
            setVelocityUPS(currentGearSettings.maxSpeed!! * velocity)
        } else {
            setPercentVoltage(velocity)
        }
    }

    override fun setGearScaledVelocity(velocity: Double, gear: Gear) {
        this.setGearScaledVelocity(velocity, gear.numVal)
    }

    override fun getCurrentGearFeedForward(): SimpleMotorFeedforward {
        return currentGearSettings.feedForwardCalculator
    }

    override fun getPositionUnits(): Double {
        return encoderToUnit(canEncoder.position)
    }

    override fun resetPosition() {
        canEncoder.position = 0.0
    }

    override fun getFwdLimitSwitch(): Boolean {
        return forwardLimitSwitch.get()
    }

    override fun getRevLimitSwitch(): Boolean {
        return reverseLimitSwitch.get()
    }

    override fun isInhibitedForward(): Boolean {
        return spark.getFault(CANSparkMax.FaultID.kHardLimitFwd)
    }

    override fun isInhibitedReverse(): Boolean {
        return spark.getFault(CANSparkMax.FaultID.kHardLimitRev)
    }

    override fun getPort(): Int {
        return spark.deviceId
    }

    override fun configureLogName(): String {
        return name
    }

    init {
        spark.restoreFactoryDefaults()
        canEncoder = spark.encoder
        pidController = spark.pidController

        // Set the name to the given one or to spark_<portnum>
        this.name = name ?: "spark_$port"
        // Set this to false because we only use reverseOutput for slaves.
        spark.inverted = reverseOutput
        // Set brake mode
        spark.idleMode = if (enableBrakeMode) CANSparkMax.IdleMode.kBrake else CANSparkMax.IdleMode.kCoast
        // Reset the position
        resetPosition()

        // Set frame rates
        if (controlFrameRateMillis != null) {
            // Must be between 1 and 100 ms.
            spark.setControlFramePeriodMs(controlFrameRateMillis)
        }
        if (statusFrameRatesMillis != null) {
            for (frame in statusFrameRatesMillis.keys) {
                spark.setPeriodicFramePeriod(frame, statusFrameRatesMillis[frame]!!)
            }
        }
        this.PDP = PDP

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
        // Set up gear-based settings.
        this.gear = currentGear
        // postEncoderGearing defaults to 1
        this.postEncoderGearing = postEncoderGearing ?: 1.0
        encoderCPR = canEncoder.countsPerRevolution

        // Only enable the limit switches if it was specified if they're normally open or closed.
        if (fwdLimitSwitchNormallyOpen != null) {
            this.forwardLimitSwitch = if (remoteLimitSwitchID != null) {
                // set CANDigitalInput to other limit switch
                CANSparkMax(remoteLimitSwitchID, CANSparkMaxLowLevel.MotorType.kBrushless)
                    .getForwardLimitSwitch(CANDigitalInput.LimitSwitchPolarity.kNormallyOpen)
            } else {
                spark.getForwardLimitSwitch(CANDigitalInput.LimitSwitchPolarity.kNormallyOpen)
            }
            this.fwdLimitSwitchNormallyOpen = fwdLimitSwitchNormallyOpen
        } else {
            this.forwardLimitSwitch = spark.getForwardLimitSwitch(CANDigitalInput.LimitSwitchPolarity.kNormallyOpen)
            this.forwardLimitSwitch.enableLimitSwitch(false)
            this.fwdLimitSwitchNormallyOpen = true
        }
        if (revLimitSwitchNormallyOpen != null) {
            this.reverseLimitSwitch = if (remoteLimitSwitchID != null) {
                CANSparkMax(remoteLimitSwitchID, CANSparkMaxLowLevel.MotorType.kBrushless)
                    .getReverseLimitSwitch(CANDigitalInput.LimitSwitchPolarity.kNormallyClosed)
            } else {
                spark.getReverseLimitSwitch(CANDigitalInput.LimitSwitchPolarity.kNormallyClosed)
            }
            this.revLimitSwitchNormallyOpen = revLimitSwitchNormallyOpen
        } else {
            this.reverseLimitSwitch = spark.getReverseLimitSwitch(CANDigitalInput.LimitSwitchPolarity.kNormallyOpen)
            this.reverseLimitSwitch.enableLimitSwitch(false)
            this.revLimitSwitchNormallyOpen = true
        }
        if (fwdSoftLimit != null) {
            spark.setSoftLimit(CANSparkMax.SoftLimitDirection.kForward, fwdSoftLimit.toFloat())
        }
        if (revSoftLimit != null) {
            spark.setSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, revSoftLimit.toFloat())
        }

        // Set the current limit if it was given
        if (currentLimit != null) {
            spark.setSmartCurrentLimit(currentLimit)
        }
        if (enableVoltageComp) {
            spark.enableVoltageCompensation(12.0)
        } else {
            spark.disableVoltageCompensation()
        }
        if (slaveSparks != null) {
            // Set up slaves.
            for (slave in slaveSparks) {
                slave.setMasterSpark(spark, enableBrakeMode)
            }
        }
        spark.burnFlash()
    }
}