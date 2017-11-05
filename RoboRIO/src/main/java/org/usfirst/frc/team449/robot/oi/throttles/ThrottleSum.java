package org.usfirst.frc.team449.robot.oi.throttles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;

/**
 * A Throttle that sums any number of other Throttles.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ThrottleSum implements Throttle {

	/**
	 * The throttles to sum.
	 */
	@NotNull
	protected final Throttle[] throttles;

	/**
	 * The cached output.
	 */
	protected double cachedValue;

	/**
	 * The sum. Field to avoid garbage collection.
	 */
	private double sum;

	/**
	 * Default constructor.
	 *
	 * @param throttles The throttles to sum.
	 */
	@JsonCreator
	public ThrottleSum(@NotNull @JsonProperty(required = true) Throttle[] throttles) {
		this.throttles = throttles;
	}

	/**
	 * Sums the throttles and returns their output
	 *
	 * @return The summed outputs, clipped to [-1, 1].
	 */
	public double getValue() {
		//sum throttles
		sum = 0;
		for (Throttle throttle : throttles) {
			sum += throttle.getValue();
		}

		//clip to [-1, 1]
		if (sum >= 1) {
			return 1;
		} else if (sum <= -1) {
			return -1;
		} else {
			return sum;
		}
	}

	/**
	 * Get the cached output of the throttle this object represents.
	 *
	 * @return The output from [-1, 1].
	 */
	@Override
	public double getValueCached() {
		return cachedValue;
	}

	/**
	 * Updates all cached values with current ones.
	 */
	@Override
	public void update() {
		cachedValue = getValue();
	}
}