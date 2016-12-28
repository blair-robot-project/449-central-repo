package org.usfirst.frc.team449.robot.components.maps;

import org.json.JSONObject;
import org.usfirst.frc.team449.robot.MapObject;

/**
 * Created by Blair Robot Project on 12/8/2016.
 */
public class UltrasonicMap  extends MapObject {
    public int PORT;

    public UltrasonicMap(maps.org.usfirst.frc.team449.robot.components.UltrasonicMap.Ultrasonic message) {
        super(message);
        PORT = message.getPort();
    }
}