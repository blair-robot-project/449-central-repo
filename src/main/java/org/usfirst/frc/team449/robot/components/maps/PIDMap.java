package org.usfirst.frc.team449.robot.components.maps;

import org.json.JSONObject;
import org.usfirst.frc.team449.robot.MapObject;

/**
 * a basic PID object, only contains the p, i and d values
 */
public abstract class PIDMap extends MapObject {
    /**
     * the p value for the pid controller
     */
    public double p;
    /**
     * the i value for the pid controller
     */
    public double i;
    /**
     * the d value for the pid controller
     */
    public double d;
    /**
     * the f value for the pid controller
     */
    public double f;
    /**
     * the percent error around the setpoint that is "close enough" and
     * requires no more tuning (20 for 20%)
     */
    public double percentTolerance;

    public PIDMap(JSONObject json, String path, Class enclosing) {
        super(json, path, enclosing);
    }
}
