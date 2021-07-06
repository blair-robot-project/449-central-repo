package org.usfirst.frc.team449.robot.jacksonWrappers

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.revrobotics.CANDigitalInput
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel
import com.revrobotics.CANSparkMaxLowLevel.PeriodicFrame
import io.github.oblarg.oblog.Loggable
import io.github.oblarg.oblog.annotations.Log
import org.usfirst.frc.team449.robot.generalInterfaces.motors.SlaveMotor

class SlaveSparkMax @JsonCreator constructor(
    @JsonProperty(required = true) port: Int,
    inverted: Boolean?,
    val PDP: PDP?
) : SlaveMotor, Loggable {
    private val slaveSpark: CANSparkMax = CANSparkMax(port, CANSparkMaxLowLevel.MotorType.kBrushless)
    val inverted: Boolean = inverted ?: false
    fun setMasterSpark(masterController: CANSparkMax?, brakeMode: Boolean) {
        slaveSpark.follow(masterController, inverted)
        slaveSpark.idleMode = if (brakeMode) CANSparkMax.IdleMode.kBrake else CANSparkMax.IdleMode.kCoast
    }

    fun setMasterPhoenix(masterPort: Int, brakeMode: Boolean) {
        slaveSpark.follow(CANSparkMax.ExternalFollower.kFollowerPhoenix, masterPort)
        slaveSpark.idleMode = if (brakeMode) CANSparkMax.IdleMode.kBrake else CANSparkMax.IdleMode.kCoast
        slaveSpark.inverted = inverted
    }

    @get:Log
    val outputCurrent: Double
        get() = slaveSpark.outputCurrent

    @get:Log
    val motorOutputVoltage: Double
        get() = slaveSpark.appliedOutput

    init {
        slaveSpark
            .getForwardLimitSwitch(CANDigitalInput.LimitSwitchPolarity.kNormallyOpen)
            .enableLimitSwitch(false)
        slaveSpark
            .getReverseLimitSwitch(CANDigitalInput.LimitSwitchPolarity.kNormallyOpen)
            .enableLimitSwitch(false)
        slaveSpark.setPeriodicFramePeriod(PeriodicFrame.kStatus0, 100)
        slaveSpark.setPeriodicFramePeriod(PeriodicFrame.kStatus1, 100)
        slaveSpark.setPeriodicFramePeriod(PeriodicFrame.kStatus2, 100)
    }
}