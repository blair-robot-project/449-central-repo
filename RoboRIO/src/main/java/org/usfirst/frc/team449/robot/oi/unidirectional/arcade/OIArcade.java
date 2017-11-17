package org.usfirst.frc.team449.robot.oi.unidirectional.arcade;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.usfirst.frc.team449.robot.oi.unidirectional.OIUnidirectional;

/**
 * An arcade-style dual joystick OI.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public abstract class OIArcade implements OIUnidirectional {

	/**
	 * Cached output values.
	 */
	private double rotCached, fwdCached;

	/**
	 * Cached left-right output values
	 */
	private double[] leftRightCached;

	/**
	 * Unscaled, unclipped values for left and right output. Fields to avoid garbage collection.
	 */
	private double tmpLeft, tmpRight;

	/**
	 * Whether or not to scale the left and right outputs so the max output is 1.
	 */
	private final boolean rescaleOutputs;

	/**
	 * Default constructor.
	 *
	 * @param rescaleOutputs Whether or not to scale the left and right outputs so the max output is 1. Defaults to false.
	 */
	@JsonCreator
	public OIArcade(boolean rescaleOutputs){
		this.rescaleOutputs = rescaleOutputs;
	}

	/**
	 * Get the rotational input.
	 *
	 * @return rotational velocity component from [-1, 1], where 1 is right and -1 is left.
	 */
	public abstract double getRot();

	/**
	 * Get the velocity input.
	 *
	 * @return forward velocity component from [-1, 1], where 1 is forwards and -1 is backwards
	 */
	public abstract double getFwd();

	/**
	 * Get the cached rotational input.
	 *
	 * @return rotational velocity component from [-1, 1], where 1 is right and -1 is left.
	 */
	public double getRotCached() {
		return rotCached;
	}

	/**
	 * Get the cached velocity input.
	 *
	 * @return forward velocity component from [-1, 1], where 1 is forwards and -1 is backwards
	 */
	public double getFwdCached() {
		return fwdCached;
	}

	/**
	 * Whether the driver is trying to drive straight.
	 *
	 * @return True if the driver is trying to drive straight, false otherwise.
	 */
	@Override
	public boolean commandingStraight() {
		return rotCached == 0;
	}

	/**
	 * The output to be given to the left and right sides of the drive.
	 *
	 * @return An array of length 2, where the 1st element is the output for the left and the second for the right, both from [-1, 1].
	 */
	public double[] getLeftRightOutput(){
		tmpLeft = getFwd() + getRot();
		tmpRight = getFwd() - getRot();
		//If left is too large
		if (Math.abs(tmpLeft) > 1){
			if (rescaleOutputs){
				//Rescale right, return left clipped to [-1, 1]
				return new double[]{Math.signum(tmpLeft), tmpRight/Math.abs(tmpLeft)};
			} else {
				//Return left clipped to [-1, 1], don't change right
				return new double[]{Math.signum(tmpLeft), tmpRight};
			}
		} else if (Math.abs(tmpRight) > 1){ //If right is too large
			if (rescaleOutputs){
				//Rescale left, return right clipped to [-1, 1]
				return new double[]{tmpLeft/Math.abs(tmpRight), Math.signum(tmpRight)};
			} else {
				//Return right clipped to [-1, 1], don't change left
				return new double[]{tmpLeft, Math.signum(tmpRight)};
			}
		} else {
			//Return unaltered if nothing is too large
			return new double[]{tmpLeft, tmpRight};
		}
	}

	/**
	 * The cached output to be given to the left and right sides of the drive.
	 *
	 * @return An array of length 2, where the 1st element is the output for the left and the second for the right, both from [-1, 1].
	 */
	public double[] getLeftRightOutputCached(){
		return leftRightCached;
	}

	/**
	 * Updates all cached values with current ones.
	 */
	@Override
	public void update() {
		rotCached = getRot();
		fwdCached = getFwd();
		leftRightCached = getLeftRightOutput();
	}
}
