package org.usfirst.frc.team449.robot.oi.buttons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedButton;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedDigitalInput;
import org.usfirst.frc.team449.robot.other.Clock;

/**
 * A button triggered off of a digital input switch on the RoboRIO.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ButtonDigitalInput extends MappedButton {

	/**
	 * The input to read from.
	 */
	@NotNull
	private final MappedDigitalInput input;

	/**
	 * True if all inputs are true, false otherwise
	 */
	protected boolean value;

	/**
	 * The time at which the value for this button was cached
	 */
	private long timeValueCached;

	/**
	 * Default constructor.
	 *
	 * @param input The input to read from.
	 */
	@JsonCreator
	public ButtonDigitalInput(@NotNull @JsonProperty(required = true) MappedDigitalInput input) {
		this.input = input;
	}

	/**
	 * Cache the value of the input if it hasn't been done yet this tic, otherwise do nothing.
	 */
	protected void cacheValue() {
		if (timeValueCached < Clock.currentTimeMillis()) {
			value = true;
			for (Boolean b : input.getStatus()) {
				if (!b) {
					value = false;
					break;
				}
			}
			timeValueCached = Clock.currentTimeMillis();
		}
	}

	/**
	 * Get whether this button is pressed
	 *
	 * @return true if the all the ports in the MappedDigitalInput are true, false otherwise.
	 */
	@Override
	public boolean get() {
		cacheValue();
		return value;
	}
}
