package org.usfirst.frc.team449.robot.components.maps;

import com.google.protobuf.Message;
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

    public ClusterPIDMap(maps.org.usfirst.frc.team449.robot.components.ClusterPIDMap.ClusterPID message) {
        super(message.getSuper());
        cluster = new MotorClusterMap(message.getCluster());
        encoder = new EncoderMap(message.getEncoder());
    }
}