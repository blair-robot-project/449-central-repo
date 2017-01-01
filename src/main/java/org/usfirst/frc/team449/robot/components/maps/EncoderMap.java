package org.usfirst.frc.team449.robot.components.maps;

import org.usfirst.frc.team449.robot.MapObject;

/**
 * This is a <code>MapObject</code> for an encoder. It contains the two
 * ports and the distance per pulse (DPP) for the encoder.
 */
public class EncoderMap extends MapObject {
    /**
     * Port a of the encoder
     */
    public int a;
    /**
     * Port b of the encoder
     */
    public int b;
    /**
     * DPP (distance per pulse) of the encoder
     */
    public double dpp;

    /**
     * Instantiates a new <code>Encoder</code>
     *
     * @param message The protobuf message with the data for this encoder.
     */
    public EncoderMap(maps.org.usfirst.frc.team449.robot.components.EncoderMap.Encoder message) {
        super(message);
        a = message.getPortA();
        b = message.getPortB();
        dpp = message.getDistancePerPulse();
    }
}
