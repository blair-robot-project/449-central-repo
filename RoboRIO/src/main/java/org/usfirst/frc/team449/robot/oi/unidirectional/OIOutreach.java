package org.usfirst.frc.team449.robot.oi.unidirectional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedButton;


@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class OIOutreach implements OIUnidirectional {

	/**
	 * The OI with higher priority that overrides if it has any input.
	 */
	@NotNull
	private final OIUnidirectional overridingOI;

	/**
	 * The OI with lower priority that gets overriden.
	 */
	@NotNull
	private final OIUnidirectional overridenOI;

	/**
	 * A button that overrides the lower priority controller
	 */
	@NotNull
	private final Button button;

	/**
	 * The cached outputs for the left and right sides of the drive.
	 */
	private double cachedLeftOutput, cachedRightOutput;

	@JsonCreator
	public OIOutreach(@NotNull @JsonProperty(required = true) OIUnidirectional overridingOI,
	                  @NotNull @JsonProperty(required = true) OIUnidirectional overridenOI,
	                  @NotNull @JsonProperty(required = true) MappedButton button) {
		this.overridingOI = overridingOI;
		this.overridenOI = overridenOI;
		this.button = button;
	}

	/**
	 * The output to be given to the left side of the drive.
	 *
	 * @return Output to left side from [-1, 1]
	 */
	@Override
	public double getLeftOutput() {
		if (overridingOI.getLeftOutput() != 0 || overridingOI.getRightOutput() != 0 || button.get()) {
			return overridingOI.getLeftOutput();
		} else {
			return overridenOI.getLeftOutput();
		}
	}

	/**
	 * The output to be given to the right side of the drive.
	 *
	 * @return Output to right side from [-1, 1]
	 */
	@Override
	public double getRightOutput() {
		if (overridingOI.getLeftOutput() != 0 || overridingOI.getRightOutput() != 0 || button.get()) {
			return overridingOI.getRightOutput();
		} else {
			return overridenOI.getRightOutput();
		}
	}

	/**
	 * Whether the driver is trying to drive straight.
	 *
	 * @return True if the driver is trying to drive straight, false otherwise.
	 */
	@Override
	public boolean commandingStraight() {
		return getLeftOutputCached() == getRightOutputCached();
	}

	/**
	 * The cached output to be given to the left side of the drive.
	 *
	 * @return Output to left side from [-1, 1]
	 */
	@Override
	public double getLeftOutputCached() {
		return cachedLeftOutput;
	}

	/**
	 * The cached output to be given to the right side of the drive.
	 *
	 * @return Output to right side from [-1, 1]
	 */
	@Override
	public double getRightOutputCached() {
		return cachedRightOutput;
	}

	/**
	 * Updates all cached values with current ones.
	 */
	@Override
	public void update() {
		cachedLeftOutput = getLeftOutput();
		cachedRightOutput = getRightOutput();
	}
}
