package org.usfirst.frc.team449.robot.components.maps;

import org.usfirst.frc.team449.robot.MapObject;

/**
 * A map for a limit switch
 */
public class LimitSwitchMap  extends MapObject {
    public int PORT;

    /**
     * Creates a LimitSwitch Map.
     *
     * @param message The protobuf message with the data for this <code>LimitSwitch</code>.
     */
    public LimitSwitchMap(maps.org.usfirst.frc.team449.robot.components.LimitSwitchMap.LimitSwitch message) {
        super(message);
        PORT = message.getPort();
    }
}