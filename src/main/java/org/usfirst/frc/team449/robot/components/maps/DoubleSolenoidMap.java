package org.usfirst.frc.team449.robot.components.maps;

import org.usfirst.frc.team449.robot.MapObject;

/**
 * Map for a DoubleSolenoid piston.
 */
public class DoubleSolenoidMap extends MapObject {
    public int forward;
    public int reverse;

    public DoubleSolenoidMap(maps.org.usfirst.frc.team449.robot.components.DoubleSolenoidMap.DoubleSolenoid message) {
        super(message);
        forward = message.getForward();
        reverse = message.getReverse();
    }
}