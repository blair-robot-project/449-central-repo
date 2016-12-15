package org.usfirst.frc.team449.robot.components.maps;

import org.json.JSONObject;
import org.usfirst.frc.team449.robot.MapObject;

/**
 * a map for a MotorCluster of variable size. the size of the Cluster is
 * defined by the JSONObject
 */
public class MotorClusterMap extends MapObject {
    /**
     * an array of maps for the motors in this Cluster
     */
    public MotorMap[] motors;
    /**
     * whether the whole cluster should be inverted
     */
    public boolean INVERTED;

    public MotorClusterMap(JSONObject json, String path, Class enclosing) {
        super(json, path, enclosing);
    }
}
