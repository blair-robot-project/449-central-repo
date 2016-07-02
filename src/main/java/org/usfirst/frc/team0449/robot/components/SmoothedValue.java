package org.usfirst.frc.team0449.robot.components;

public class SmoothedValue {

	private final double alpha;

	private double value;

	/**
	 * Exponentially smoothed time series
	 *
	 * @param alpha smoothing constant (0-1 from infinate smoothing to no
	 *              smoothing)
	 */
	public SmoothedValue(double alpha) {
		this.alpha = alpha;
	}

	public double get() {
		return value;
	}

	/**
	 * Update the value, applying the low pass filter (should be called once per
	 * loop iteration with next data point)
	 *
	 * @param value new value
	 */
	public void set(double value) {
		this.value = alpha * value + (1 - alpha) * this.value;
	}
}