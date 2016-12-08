package org.usfirst.frc.team449.robot.components.maps;

import org.json.JSONObject;
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
     * @param json      the <code>JSONObject</code> containing the values for this
     *                  object
     * @param path      the path to find this object in the
     *                  <code>JSONObject</code>
     * @param enclosing <code>Class</code> one up from this <code>MapObject</code>
     *                  in the map
     */
    public MotorMap(JSONObject json, String path, Class enclosing) {
        super(json, path, enclosing);
    }
}
