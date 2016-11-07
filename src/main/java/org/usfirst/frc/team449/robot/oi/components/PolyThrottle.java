package org.usfirst.frc.team449.robot.oi.components;

import edu.wpi.first.wpilibj.Joystick;

/**
 * Created by noah on 10/27/16.
 */
public class PolyThrottle extends SmoothedThrottle{
	//The power that X is raised to.
	protected int degree;

	/**
	 * A basic constructor. The stick is assumed to not be inverted.
	 * @param stick the Joystick object being used
	 * @param axis The axis being used. 0 is X, 1 is Y, 2 is Z.
	 * @param degree The degree of the polynomial
	 */
	public PolyThrottle(Joystick stick, int axis, int degree){
		this(stick, axis, degree, false);
	}
	
	/**
     * A basic constructor.
     * @param stick The Joystick object being used
     * @param axis The axis being used. 0 is X, 1 is Y, 2 is Z.
     * @param degree The degree of the polynomial
     * @param inverted Whether or not to invert the joystick input.
     */
    public PolyThrottle(Joystick stick, int axis, int degree, boolean inverted){
        super(stick, axis, inverted);
        this.degree = degree;
    }

    /**
     * Raises the value of the smoothed joystick output to the degreeth power, while preserving sign.
     * @return The processed value of the joystick
     */
    @Override
    public double getValue(){
        double input = super.getValue();
        if (degree%2 == 0 && input < 0)
        	return -1*Math.pow(input, degree);
        return Math.pow(input, degree);
    }
}