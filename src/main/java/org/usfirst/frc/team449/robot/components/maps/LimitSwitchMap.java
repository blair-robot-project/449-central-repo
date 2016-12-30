package org.usfirst.frc.team449.robot.components.maps;

import org.usfirst.frc.team449.robot.MapObject;

/**
 * A map for a limit switch
 */
public class LimitSwitchMap  extends MapObject {
    public int PORT;

    /**
     * creates a LimitSwitch Map based on the JSONObject given to it, and a
     * path down to this object
     *
     * @param json      the JSONObject containing the values for this object
     * @param path      the path to find this object in the JSONObject
     * @param enclosing the enclosing class of the <code>LimitSwitch</code>
     */
    public LimitSwitchMap(maps.org.usfirst.frc.team449.robot.components.LimitSwitchMap.LimitSwitch message) {
        super(message);
        PORT = message.getPort();
    }
}