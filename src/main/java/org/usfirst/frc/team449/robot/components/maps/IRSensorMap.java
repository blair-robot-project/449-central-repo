package org.usfirst.frc.team449.robot.components.maps;

import edu.wpi.first.wpilibj.AnalogInput;
import org.usfirst.frc.team449.robot.MapObject;

/**
 * Created by Blair Robot Project on 12/8/2016.
 */
public class IRSensorMap extends MapObject {
    public int PORT;

    /**
     * Minimum value at which IntakeIn should stop
     */
    public double LOWER_BOUND;
    /**
     * Maximum value at which IntakeIn should stop
     */
    public double UPPER_BOUND;
    /**
     * number of bits for oversampling as defined by
     * {@link AnalogInput#setOversampleBits(int)}
     * Should probably be the same as {@link #AVERAGE_BITS}
     */
    public int OVERSAMPLING_BITS;
    /**
     * number of bits for averaging as defined by
     * {@link AnalogInput#setAverageBits(int) }
     * Should probably be the same as {@link #OVERSAMPLING_BITS}
     */
    public int AVERAGE_BITS;

    public IRSensorMap(maps.org.usfirst.frc.team449.robot.components.IRSensorMap.IRSensor message) {
        super(message);
        PORT = message.getPort();
        LOWER_BOUND = message.getLowerBound();
        UPPER_BOUND = message.getUpperBound();
        OVERSAMPLING_BITS = message.getOversamplingBits();
        AVERAGE_BITS = message.getAverageBits();
    }

}
