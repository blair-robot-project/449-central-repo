package org.usfirst.frc.team449.robot.jacksonWrappers

import com.ctre.phoenix.motorcontrol.IMotorController
import com.ctre.phoenix.motorcontrol.InvertType
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.StatusFrame
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.usfirst.frc.team449.robot.generalInterfaces.motors.SlaveMotor

/**
 * A [VictorSPX] that will be slaved to another Victor or a [ ].
 */
class SlaveVictor @JsonCreator constructor(@JsonProperty(required = true) port: Int, invertType: InvertType?) :
    SlaveMotor {
    /** The Victor this is a wrapper on.  */
    private val victorSPX: VictorSPX

    /**
     * Set this Victor to follow another CAN device.
     *
     * @param toFollow The motor controller to follow.
     * @param brakeMode Whether this Talon should be in brake mode or coast mode.
     * @param voltageCompSamples The number of voltage compensation samples to use, or null to not
     * compensate voltage.
     */
    fun setMaster(
        toFollow: IMotorController,
        brakeMode: Boolean,
        voltageCompSamples: Int?
    ) {
        // Brake mode doesn't automatically follow master
        victorSPX.setNeutralMode(if (brakeMode) NeutralMode.Brake else NeutralMode.Coast)

        // Voltage comp might not follow master either
        if (voltageCompSamples != null) {
            victorSPX.enableVoltageCompensation(true)
            victorSPX.configVoltageCompSaturation(12.0, 0)
            victorSPX.configVoltageMeasurementFilter(voltageCompSamples, 0)
        } else {
            victorSPX.enableVoltageCompensation(false)
        }

        // Follow the leader
        victorSPX.follow(toFollow)
    }

    /**
     * Default constructor.
     *
     * @param port The CAN ID of this Victor SPX.
     * @param invertType Whether to invert this relative to the master. Defaults to not inverting
     * relative to master.
     */
    init {
        victorSPX = VictorSPX(port)
        victorSPX.setInverted(invertType ?: InvertType.FollowMaster)
        victorSPX.configPeakOutputForward(1.0, 0)
        victorSPX.configPeakOutputReverse(-1.0, 0)
        victorSPX.enableVoltageCompensation(true)
        victorSPX.configVoltageCompSaturation(12.0, 0)
        victorSPX.configVoltageMeasurementFilter(32, 0)
        victorSPX.setStatusFramePeriod(StatusFrame.Status_1_General, 100, 0)
        victorSPX.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 100, 0)
        victorSPX.setStatusFramePeriod(StatusFrame.Status_6_Misc, 100, 0)
        victorSPX.setStatusFramePeriod(StatusFrame.Status_7_CommStatus, 100, 0)
        victorSPX.setStatusFramePeriod(StatusFrame.Status_9_MotProfBuffer, 100, 0)
        victorSPX.setStatusFramePeriod(StatusFrame.Status_10_MotionMagic, 100, 0)
        victorSPX.setStatusFramePeriod(StatusFrame.Status_12_Feedback1, 100, 0)
        victorSPX.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, 100, 0)
        victorSPX.setStatusFramePeriod(StatusFrame.Status_14_Turn_PIDF1, 100, 0)
    }
}