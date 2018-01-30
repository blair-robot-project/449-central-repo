package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
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
    private PDP PDP;

    /**
     * The linear regression component for logging resistance.
     */
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
        this.talonSRX.configForwardLimitSwitchSource(LimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled, 0);
        this.talonSRX.configReverseLimitSwitchSource(LimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled, 0);
        this.talonSRX.configForwardSoftLimitEnable(false, 0);
        this.talonSRX.configReverseSoftLimitEnable(false, 0);
        this.talonSRX.configPeakOutputForward(1, 0);
    }

    /**
     * Set this Talon to follow another CAN device.
     *
     * @param port            The CAN ID of the device to follow.
     * @param brakeMode       Whether this Talon should be in brake mode or coast mode.
     * @param currentLimit    The current limit for this Talon. Can be null for no current limit.
     * @param PDP             The PDP this Talon is connected to.
     * @param linRegComponent The linear regression component for logging resistance.
     */
    public void setMaster(int port, boolean brakeMode, @Nullable Integer currentLimit,
                          @NotNull PDP PDP, @NotNull RunningLinRegComponent linRegComponent) {
        //Brake mode doesn't automatically follow master
        this.talonSRX.setNeutralMode(brakeMode ? NeutralMode.Brake : NeutralMode.Coast);

        //Current limiting might not automatically follow master, set it just to be safe
        if (currentLimit != null) {
            talonSRX.configContinuousCurrentLimit(currentLimit, 0);
            talonSRX.configPeakCurrentLimit(0, 0); // No duration
            talonSRX.enableCurrentLimit(true);
        } else {
            //If we don't have a current limit, disable current limiting.
            talonSRX.enableCurrentLimit(false);
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
    @NotNull
    @Override
    public Object[] getData() {
        linRegComponent.addPoint(talonSRX.getOutputCurrent(), PDP.getVoltage() - talonSRX.getBusVoltage());
        return new Object[]{
                talonSRX.getOutputCurrent(),
                talonSRX.getMotorOutputVoltage(),
                -linRegComponent.getSlope()
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
