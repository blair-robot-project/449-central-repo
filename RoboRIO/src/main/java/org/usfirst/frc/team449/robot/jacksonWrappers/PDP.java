package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.components.RunningLinRegComponent;
import org.usfirst.frc.team449.robot.generalInterfaces.loggable.Loggable;
import org.usfirst.frc.team449.robot.generalInterfaces.updatable.Updatable;

/**
 * An object representing the Power Distribution Panel that logs power, current, and resistance.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class PDP extends PowerDistributionPanel implements Loggable, Updatable {

    /**
     * The component for doing linear regression to find the resistance.
     */
    private final RunningLinRegComponent voltagePerCurrentLinReg;

    /**
     * Default constructor.
     *
     * @param canID                   CAN ID of the PDP. Defaults to 0.
     * @param voltagePerCurrentLinReg The component for doing linear regression to find the resistance.
     */
    @JsonCreator
    public PDP(int canID,
               @NotNull @JsonProperty(required = true) RunningLinRegComponent voltagePerCurrentLinReg) {
        super(canID);
        this.voltagePerCurrentLinReg = voltagePerCurrentLinReg;
    }

    /**
     * Get the resistance of the wires leading to the PDP.
     *
     * @return Resistance in ohms.
     */
    public double getResistance() {
        return -voltagePerCurrentLinReg.getSlope();
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
                "temperature",
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
        return new Object[]{
                getTemperature(),
                getTotalCurrent(),
                getVoltage(),
                getResistance()
        };
    }

    /**
     * Get the name of this object.
     *
     * @return A string that will identify this object in the log file.
     */
    @NotNull
    @Override
    public String getLogName() {
        return "PDP";
    }

    /**
     * Updates all cached values with current ones.
     */
    @Override
    public void update() {
        //Calculate running linear regression
        voltagePerCurrentLinReg.addPoint(getTotalCurrent(), getVoltage());
    }
}
