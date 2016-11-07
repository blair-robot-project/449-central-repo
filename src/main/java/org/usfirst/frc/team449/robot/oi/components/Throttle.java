package org.usfirst.frc.team449.robot.oi.components;

import edu.wpi.first.wpilibj.Joystick;

/**
 * Created by Noah Gleason on 10/26/16.
 */
public class Throttle {
	//The stick we're using
	protected Joystick stick;
	//The axis on the joystick we care about. Usually 1.
	protected int axis;
	//Whether or not the controls should be inverted
	protected boolean inverted;

	/**
	 * A basic constructor. The stick is assumed to not be inverted.
	 * @param stick the Joystick object being used
	 * @param axis The axis being used. 0 is X, 1 is Y, 2 is Z.
	 */
    public Throttle (Joystick stick, int axis){
        this(stick, axis, false);
    }
    
    /**
     * A basic constructor.
     * @param stick The Joystick object being used
     * @param axis The axis being used. 0 is X, 1 is Y, 2 is Z.
     * @param inverted Whether or not to invert the joystick input.
     */
    public Throttle (Joystick stick, int axis, boolean inverted){
        this.stick = stick;
        this.axis = axis;
        this.inverted = inverted;
    }

    /**
     * Gets the raw value from the stick and inverts it if necessary.
     * @return The raw joystick output.
     */
    public double getValue(){
        return (inverted ? -1:1) * stick.getRawAxis(axis);
    }
}