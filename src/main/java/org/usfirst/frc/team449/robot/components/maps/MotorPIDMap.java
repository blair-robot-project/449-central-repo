package org.usfirst.frc.team449.robot.components.maps;

import org.json.JSONObject;

/**
 * a PID controller that uses an Encoder to control a Motor
 */
public class MotorPIDMap extends PIDMap {
    /**
     * the motor controlled by this controller
     */
    public MotorMap motor;
    /**
     * the encoder controlling the motor
     */
    public EncoderMap encoder;

    /**
     * Creates a new <code>MotorPID</code>
     *
     * @param json      <code>JSONObject</code> containing the map
     * @param path      dot-delimited path to the <code>MotorPID</code> in the map
     * @param enclosing the enclosing class of the <code>MotorPID</code>
     */
    public MotorPIDMap(maps.org.usfirst.frc.team449.robot.components.MotorPIDMap.MotorPID message) {
        super(message.getSuper());
        motor = new MotorMap(message.getMotor());
        encoder = new EncoderMap(message.getEncoder());
    }
}