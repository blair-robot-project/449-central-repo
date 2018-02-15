package org.usfirst.frc.team449.robot.generalInterfaces.simpleMotor;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.loggable.Loggable;
import org.usfirst.frc.team449.robot.jacksonWrappers.SlaveVictor;

import java.util.List;

/**
 * A simple wrapper on the {@link VictorSPX}.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MappedVictorSPX implements SimpleMotor, Loggable {

    /**
     * The Victor SPX this object is a wrapper on.
     */
    @NotNull
    private final VictorSPX victorSPX;

    /**
     * Default constructor.
     *
     * @param port              The CAN ID of this Victor SPX.
     * @param brakeMode         Whether to have the Victor brake or coast when no voltage is applied.
     * @param inverted          Whether or not to invert this Victor. Defaults to false.
     * @param enableVoltageComp Whether or not to enable voltage compensation. Defaults to true.
     * @param slaveVictors      Any other Victor SPXs slaved to this one.
     */
    @JsonCreator
    public MappedVictorSPX(@JsonProperty(required = true) int port,
                           @JsonProperty(required = true) boolean brakeMode,
                           boolean inverted,
                           @Nullable Boolean enableVoltageComp,
                           @Nullable List<SlaveVictor> slaveVictors) {
        victorSPX = new VictorSPX(port);
        victorSPX.setInverted(inverted);
        victorSPX.setNeutralMode(brakeMode ? NeutralMode.Brake : NeutralMode.Coast);
        victorSPX.enableVoltageCompensation(enableVoltageComp != null ? enableVoltageComp : true);

        if (slaveVictors != null) {
            //Set up slaves.
            for (SlaveVictor slave : slaveVictors) {
                slave.setMaster(victorSPX, brakeMode);
            }
        }
    }

    /**
     * Set the velocity for the motor to go at.
     *
     * @param velocity the desired velocity, on [-1, 1].
     */
    @Override
    public void setVelocity(double velocity) {
        victorSPX.set(ControlMode.PercentOutput, velocity);
    }

    /**
     * Enables the motor, if applicable.
     */
    @Override
    public void enable() {
        //Do nothing
    }

    /**
     * Disables the motor, if applicable.
     */
    @Override
    public void disable() {
        victorSPX.set(ControlMode.Disabled, 0);
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
                "bus_voltage",
                "voltage"
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
        return new Object[]{
                victorSPX.getBusVoltage(),
                victorSPX.getMotorOutputVoltage()
        };
    }

    /**
     * Get the name of this object.
     *
     * @return A string that will identify this object in the log file.
     */
    @Override
    @NotNull
    public String getLogName() {
        return "victor_" + victorSPX.getDeviceID();
    }
}
