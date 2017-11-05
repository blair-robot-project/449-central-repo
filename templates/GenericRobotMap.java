package org.usfirst.frc.team449.robot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.oi.buttons.CommandButton;

import java.util.List;

/**
 * A generic example of the Jackson-compatible object representing the entire robot.
 */
public class RobotMap {

	/**
	 * The buttons for controlling this robot.
	 */
	@NotNull
	private final List<CommandButton> buttons;

	/**
	 * Default constructor.
	 *
	 * @param buttons The buttons for controlling this robot.
	 */
	@JsonCreator
	public RobotMap(@NotNull @JsonProperty(required = true) List<CommandButton> buttons) {
		this.buttons = buttons;
	}

	/**
	 * Getter for the list of buttons to control the robot.
	 *
	 * @return The buttons for controlling this robot.
	 */
	@NotNull
	public List<CommandButton> getButtons() {
		return buttons;
	}
}
