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
	private double leftThrottleCached, rightThrottleCached;

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
	 * Get the cached throttle for the left side of the drive.
	 *
	 * @return percent of max speed for left motor cluster from [-1.0, 1.0]
	 */
	public double getLeftThrottleCached() {
		return leftThrottleCached;
	}

	/**
	 * Get the cached throttle for the right side of the drive.
	 *
	 * @return percent of max speed for right motor cluster from [-1.0, 1.0]
	 */
	public double getRightThrottleCached() {
		return rightThrottleCached;
	}

	/**
	 * The output to be given to the left side of the drive.
	 *
	 * @return Output to left side from [-1, 1]
	 */
	public double getLeftOutput() {
		return getLeftThrottle();
	}

	/**
	 * The output to be given to the right side of the drive.
	 *
	 * @return Output to right side from [-1, 1]
	 */
	public double getRightOutput() {
		return getRightThrottle();
	}

	/**
	 * The cached output to be given to the left side of the drive.
	 *
	 * @return Output to left side from [-1, 1]
	 */
	public double getLeftOutputCached() {
		return leftThrottleCached;
	}

	/**
	 * The cached output to be given to the right side of the drive.
	 *
	 * @return Output to right side from [-1, 1]
	 */
	public double getRightOutputCached() {
		return rightThrottleCached;
	}

	/**
	 * Updates all cached values with current ones.
	 */
	@Override
	public void update() {
		leftThrottleCached = getLeftThrottle();
		rightThrottleCached = getRightThrottle();
	}
}
