package org.usfirst.frc.team449.robot.oi.unidirectional.tank;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.usfirst.frc.team449.robot.oi.unidirectional.OIUnidirectional;

/**
 * A tank-style dual joystick OI.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public abstract class OITank implements OIUnidirectional {

	/**
	 * Cached left and right throttle values.
	 */
	private double[] leftRightThrottleCached;

	/**
	 * Get the throttle for the left side of the drive.
	 *
	 * @return percent of max speed for left motor cluster from [-1.0, 1.0]
	 */
	public abstract double getLeftThrottle();

	/**
	 * Get the throttle for the right side of the drive.
	 *
	 * @return percent of max speed for right motor cluster from [-1.0, 1.0]
	 */
	public abstract double getRightThrottle();

	/**
	 * The output to be given to the left and right sides of the drive.
	 *
	 * @return An array of length 2, where the 1st element is the output for the left and the second for the right, both from [-1, 1].
	 */
	@Override
	public double[] getLeftRightOutput(){
		return new double[]{getLeftThrottle(), getRightThrottle()};
	}

	/**
	 * The cached output to be given to the left and right sides of the drive.
	 *
	 * @return An array of length 2, where the 1st element is the output for the left and the second for the right, both from [-1, 1].
	 */
	@Override
	public double[] getLeftRightOutputCached(){
		return leftRightThrottleCached;
	}

	/**
	 * Updates all cached values with current ones.
	 */
	@Override
	public void update() {
		leftRightThrottleCached= getLeftRightOutput();
	}
}
