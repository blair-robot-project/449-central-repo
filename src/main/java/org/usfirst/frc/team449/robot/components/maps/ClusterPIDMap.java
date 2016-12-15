package org.usfirst.frc.team449.robot.components.maps;

import org.json.JSONObject;

/**
 * a map for a PID controller that has a MotorCluster, and a single encoder
 */
public class ClusterPIDMap extends VelocityPIDMap {
    /**
     * the MotorCluster controlled by this PID controller
     */
    public MotorClusterMap cluster;
    /**
     * the Encoder used for control in this PID controller
     */
    public EncoderMap encoder;

    public ClusterPIDMap(JSONObject json, String path, Class enclosing) {
        super(json, path, enclosing);
    }
}