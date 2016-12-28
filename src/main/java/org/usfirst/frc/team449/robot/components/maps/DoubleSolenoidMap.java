package org.usfirst.frc.team449.robot.components.maps;

import org.json.JSONObject;
import org.usfirst.frc.team449.robot.MapObject;

/**
 * Created by blairrobot on 12/23/16.
 */
public class DoubleSolenoidMap extends MapObject {
    public int forward;
    public int reverse;

    public DoubleSolenoidMap(JSONObject json, String objPath, Class enclosing) {
        super(json, objPath, enclosing);
    }
}