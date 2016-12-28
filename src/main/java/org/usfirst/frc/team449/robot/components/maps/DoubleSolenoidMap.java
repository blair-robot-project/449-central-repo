package org.usfirst.frc.team449.robot.components.maps;

import org.usfirst.frc.team449.robot.MapObject;

/**
 * Created by blairrobot on 12/23/16.
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