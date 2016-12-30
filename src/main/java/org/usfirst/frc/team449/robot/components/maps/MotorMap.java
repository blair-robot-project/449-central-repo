package org.usfirst.frc.team449.robot.components.maps;

import org.usfirst.frc.team449.robot.MapObject;

/**
 * This is a <code>MapObject</code> for a motor; it contains a port number
 * and a flag for inversion.
 */
public class MotorMap extends MapObject {
    /**
     * Port number
     */
    public int PORT;
    /**
     * Whether the motor is inverted
     */
    public boolean INVERTED;

    /**
     * Instantiates a new <code>Motor</code>
     *
     * @param message The protobuf message with the data for this <code>Motor</code>
     */
    public MotorMap(maps.org.usfirst.frc.team449.robot.components.MotorMap.Motor message) {
        super(message);
        PORT = message.getPort();
        INVERTED = message.getInverted();
    }
}
