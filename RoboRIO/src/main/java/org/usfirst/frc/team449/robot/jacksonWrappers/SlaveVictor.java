package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link VictorSPX} that will be slaved to another Victor or a {@link com.ctre.phoenix.motorcontrol.can.TalonSRX}.
 */
public class SlaveVictor {

    /**
     * The Victor this is a wrapper on.
     */
    @NotNull
    private final VictorSPX victorSPX;

    /**
     * Default constructor.
     *
     * @param port     The CAN ID of this Victor SPX.
     * @param inverted Whether or not to invert this Victor. Note this is not relative to the master. Defaults to
     *                 false.
     */
    @JsonCreator
    public SlaveVictor(@JsonProperty(required = true) int port,
                       boolean inverted) {
        victorSPX = new VictorSPX(port);
        victorSPX.setInverted(inverted);
        victorSPX.configPeakOutputForward(1, 0);
        victorSPX.configPeakOutputReverse(-1, 0);
        victorSPX.enableVoltageCompensation(true);
        victorSPX.configVoltageCompSaturation(12, 0);
        victorSPX.configVoltageMeasurementFilter(32, 0);
        victorSPX.setStatusFramePeriod(StatusFrame.Status_1_General, 100, 0);
        victorSPX.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 100, 0);
        victorSPX.setStatusFramePeriod(StatusFrame.Status_6_Misc, 100, 0);
        victorSPX.setStatusFramePeriod(StatusFrame.Status_7_CommStatus, 100, 0);
        victorSPX.setStatusFramePeriod(StatusFrame.Status_9_MotProfBuffer, 100, 0);
        victorSPX.setStatusFramePeriod(StatusFrame.Status_10_MotionMagic, 100, 0);
        victorSPX.setStatusFramePeriod(StatusFrame.Status_12_Feedback1, 100, 0);
        victorSPX.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, 100, 0);
        victorSPX.setStatusFramePeriod(StatusFrame.Status_14_Turn_PIDF1, 100, 0);
    }

    /**
     * Set this Victor to follow another CAN device.
     *
     * @param toFollow           The motor controller to follow.
     * @param brakeMode          Whether this Talon should be in brake mode or coast mode.
     * @param voltageCompSamples The number of voltage compensation samples to use, or null to not compensate voltage.
     */
    public void setMaster(@NotNull IMotorController toFollow, boolean brakeMode, @Nullable Integer voltageCompSamples) {
        //Brake mode doesn't automatically follow master
        victorSPX.setNeutralMode(brakeMode ? NeutralMode.Brake : NeutralMode.Coast);

        //Voltage comp might not follow master either
        if (voltageCompSamples != null) {
            victorSPX.enableVoltageCompensation(true);
            victorSPX.configVoltageCompSaturation(12, 0);
            victorSPX.configVoltageMeasurementFilter(voltageCompSamples, 0);
        } else {
            victorSPX.enableVoltageCompensation(false);
        }

        //Follow the leader
        victorSPX.follow(toFollow);
    }
}
