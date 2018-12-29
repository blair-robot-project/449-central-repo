package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.components.RunningLinRegComponent;
import org.usfirst.frc.team449.robot.generalInterfaces.loggable.Loggable;

/**
 * A {@link TalonSRX} that will be slaved to another TalonSRX or a {@link com.ctre.phoenix.motorcontrol.can.VictorSPX}.
 */
public class SlaveTalon implements Loggable {

    /**
     * The TalonSRX this object wraps.
     */
    @NotNull
    private final TalonSRX talonSRX;
    /**
     * The PDP this talon runs on. Used for resistance logging purposes.
     */
    @Nullable
    private PDP PDP;

    /**
     * The linear regression component for logging resistance.
     */
    @Nullable
    private RunningLinRegComponent linRegComponent;

    /**
     * Default constructor.
     *
     * @param port     The CAN ID of this Talon SRX.
     * @param inverted Whether or not to invert this Talon. Note this is not relative to the master. Defaults to false.
     */
    @JsonCreator
    public SlaveTalon(@JsonProperty(required = true) int port,
                      boolean inverted) {
        this.talonSRX = new TalonSRX(port);
        this.talonSRX.setInverted(inverted);

        //Turn off features we don't want a slave to have
        talonSRX.configForwardLimitSwitchSource(LimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled, 0);
        talonSRX.configReverseLimitSwitchSource(LimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled, 0);
        talonSRX.configForwardSoftLimitEnable(false, 0);
        talonSRX.configReverseSoftLimitEnable(false, 0);
        talonSRX.configPeakOutputForward(1, 0);
        talonSRX.enableVoltageCompensation(true);
        talonSRX.configVoltageCompSaturation(12, 0);
        talonSRX.configVoltageMeasurementFilter(32, 0);

        //Slow down frames so we don't overload the CAN bus
        talonSRX.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 100, 0);
        talonSRX.setStatusFramePeriod(StatusFrameEnhanced.Status_6_Misc, 100, 0);
        talonSRX.setStatusFramePeriod(StatusFrameEnhanced.Status_7_CommStatus, 100, 0);
        talonSRX.setStatusFramePeriod(StatusFrameEnhanced.Status_9_MotProfBuffer, 100, 0);
        talonSRX.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 100, 0);
        talonSRX.setStatusFramePeriod(StatusFrameEnhanced.Status_12_Feedback1, 100, 0);
        talonSRX.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 100, 0);
        talonSRX.setStatusFramePeriod(StatusFrameEnhanced.Status_14_Turn_PIDF1, 100, 0);
        talonSRX.setStatusFramePeriod(StatusFrameEnhanced.Status_15_FirmareApiStatus, 100, 0);
        talonSRX.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, 100, 0);
        talonSRX.setStatusFramePeriod(StatusFrameEnhanced.Status_8_PulseWidth, 100, 0);
        talonSRX.setStatusFramePeriod(StatusFrameEnhanced.Status_11_UartGadgeteer, 100, 0);
    }

    /**
     * Set this Talon to follow another CAN device.
     *
     * @param port               The CAN ID of the device to follow.
     * @param brakeMode          Whether this Talon should be in brake mode or coast mode.
     * @param currentLimit       The current limit for this Talon. Can be null for no current limit.
     * @param voltageCompSamples The number of voltage compensation samples to use, or null to not compensate voltage.
     * @param PDP                The PDP this Talon is connected to.
     * @param linRegComponent    The linear regression component for logging resistance.
     */
    public void setMaster(int port, boolean brakeMode, @Nullable Integer currentLimit,
                          @Nullable Integer voltageCompSamples, @Nullable PDP PDP, @Nullable RunningLinRegComponent linRegComponent) {
        //Brake mode doesn't automatically follow master
        this.talonSRX.setNeutralMode(brakeMode ? NeutralMode.Brake : NeutralMode.Coast);

        //Current limiting might not automatically follow master, set it just to be safe
        if (currentLimit != null) {
            talonSRX.configContinuousCurrentLimit(currentLimit, 0);
            talonSRX.configPeakCurrentDuration(0, 0);
            talonSRX.configPeakCurrentLimit(0, 0); // No duration
            talonSRX.enableCurrentLimit(true);
        } else {
            //If we don't have a current limit, disable current limiting.
            talonSRX.enableCurrentLimit(false);
        }

        //Voltage comp might not follow master either
        if (voltageCompSamples != null) {
            talonSRX.enableVoltageCompensation(true);
            talonSRX.configVoltageCompSaturation(12, 0);
            talonSRX.configVoltageMeasurementFilter(voltageCompSamples, 0);
        } else {
            talonSRX.enableVoltageCompensation(false);
        }

        //Follow the leader
        this.talonSRX.set(ControlMode.Follower, port);

        //Resistance logging
        this.PDP = PDP;
        this.linRegComponent = linRegComponent;
    }

    /**
     * Get the headers for the data this subsystem logs every loop.
     *
     * @return An N-length array of String labels for data, where N is the length of the Object[] returned by getData().
     */
    @NotNull
    @Override
    public String[] getHeader() {
        return new String[]{
                "current",
                "voltage",
                "resistance"
        };
    }

    /**
     * Get the data this subsystem logs every loop.
     *
     * @return An N-length array of Objects, where N is the number of labels given by getHeader.
     */
    @Nullable
    @Override
    public Object[] getData() {
        if (linRegComponent != null && PDP != null) {
            linRegComponent.addPoint(talonSRX.getOutputCurrent(), PDP.getVoltage() - talonSRX.getBusVoltage());
        }
        return new Object[]{
                talonSRX.getOutputCurrent(),
                talonSRX.getMotorOutputVoltage(),
                (linRegComponent != null && PDP != null) ? -linRegComponent.getSlope() : null
        };
    }

    /**
     * Get the name of this object.
     *
     * @return A string that will identify this object in the log file.
     */
    @Override
    public @NotNull String getLogName() {
        return "talon_" + talonSRX.getDeviceID();
    }
}
