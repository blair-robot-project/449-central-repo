package org.usfirst.frc.team449.robot.components.maps;

import org.json.JSONObject;

/**
 * Created by Blair Robot Project on 12/8/2016.
 */
public class AnglePIDMap extends PIDMap {
    public double absoluteTolerance;
    public double minimumOutput;
    public boolean minimumOutputEnabled;

    public AnglePIDMap(JSONObject json, String path, Class enclosing) {
        super(json, path, enclosing);
    }
}
