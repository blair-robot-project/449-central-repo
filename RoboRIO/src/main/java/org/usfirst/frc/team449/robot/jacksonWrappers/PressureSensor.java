package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.AnalogInput;

/**
 * Wrapper for an {@link AnalogInput} pressure sensor that returns a voltage linearly proportional to pressure.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class PressureSensor {

    /**
     * The AnalogInput this is a wrapper on.
     */
    private final AnalogInput sensor;


    /**
     * Default constructor.
     *
     * @param port           The port of the sensor.
     * @param oversampleBits The number of oversample bits.
     * @param averageBits    The number of averaging bits.
     */
    @JsonCreator
    public PressureSensor(@JsonProperty(required = true) int port,
                          @JsonProperty(required = true) int oversampleBits,
                          @JsonProperty(required = true) int averageBits) {
        sensor = new AnalogInput(port);
        sensor.setOversampleBits(oversampleBits);
        sensor.setAverageBits(averageBits);
    }

    /**
     * Returns the pressure measured by the sensor.
     *
     * @return pressure in PSI
     */
    public double getPressure() {
        return 50.0 * sensor.getAverageVoltage() - 25.0;    //these are constants given by REV, assuming 5.0V in
    }
}
