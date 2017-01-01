package org.usfirst.frc.team449.robot.components.maps;

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
     * @param message The protobuf message with the data for this <code>MotorPID</code>
     */
    public MotorPIDMap(maps.org.usfirst.frc.team449.robot.components.MotorPIDMap.MotorPID message) {
        super(message.getSuper());
        motor = new MotorMap(message.getMotor());
        encoder = new EncoderMap(message.getEncoder());
    }
}