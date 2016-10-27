package org.usfirst.frc.team449.robot.oi.components;

import edu.wpi.first.wpilibj.Joystick;

/**
 * Created by noah on 10/27/16.
 */
public class ExpThrottle extends SmoothedThrottle{
	//The base that is raised to the power of the joystick input.
	protected double base;

	/**
	 * A basic constructor. The stick is assumed to not be inverted.
	 * @param stick the Joystick object being used
	 * @param axis The axis being used. 0 is X, 1 is Y, 2 is Z.
	 * @param base The base of the exponential
	 */
	public ExpThrottle(Joystick stick, int axis, double base){
		this(stick, axis, base, false);
	}
	
	/**
     * A basic constructor.
     * @param stick The Joystick object being used
     * @param axis The axis being used. 0 is X, 1 is Y, 2 is Z.
     * @param base The base of the exponential
     * @param inverted Whether or not to invert the joystick input.
     */
    public ExpThrottle(Joystick stick, int axis, double base, boolean inverted){
        super(stick, axis);
        this.base = base;
    }

    /**
     * Raises the base to the value of the smoothed joystick output, adjusting for sign.
     * @return The processed value of the joystick
     */
    @Override
    public double getValue(){
        double input = super.getValue();
        if (input > 0)
        	return (Math.pow(base, input)-1)/(base-1);
        return -1*(Math.pow(base, input*-1)-1)/(base-1);
    }
}