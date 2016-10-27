package org.usfirst.frc.team449.robot.oi.components;

import edu.wpi.first.wpilibj.Joystick;

/**
 * Created by noah on 10/26/16.
 */
public class SmoothedThrottle extends Throttle{
	
	/**
	 * A basic constructor. The stick is assumed to not be inverted.
	 * @param stick the Joystick object being used
	 * @param axis The axis being used. 0 is X, 1 is Y, 2 is Z.
	 */
	public SmoothedThrottle(Joystick stick, int axis){
		this(stick, axis, false);
	}

	 /**
     * A basic constructor.
     * @param stick The Joystick object being used
     * @param axis The axis being used. 0 is X, 1 is Y, 2 is Z.
     * @param inverted Whether or not to invert the joystick input.
     */
    public SmoothedThrottle(Joystick stick, int axis, boolean inverted){
        super(stick, axis, inverted);
    }

    /**
     * Gets the value from the joystick and smoothes it.
     * @return The joystick's value, after processed with a smoothing function.
     */
    @Override
    public double getValue(){
        double input = super.getValue();
        int sign = (input < 0) ? -1 : 1; // get the sign of the input
        input *= sign; // get the absolute value
        // if in the deadband, return 0
        if (input < 0.02)
            return 0;
        return sign * (1 / (1 - Math.pow(0.02, 2))) * (Math.pow(input, 2) - Math.pow(0.02, 2));
    }
}