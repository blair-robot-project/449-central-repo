package org.usfirst.frc.team449.robot.components.maps;

import org.usfirst.frc.team449.robot.MapObject;

/**
 * The map for an ultrasound sensor.
 */
public class UltrasonicMap  extends MapObject {
    public int PORT;

    public UltrasonicMap(maps.org.usfirst.frc.team449.robot.components.UltrasonicMap.Ultrasonic message) {
        super(message);
        PORT = message.getPort();
    }
}