package org.usfirst.frc.team449.robot.oi.buttons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.Joystick;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedButton;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedJoystick;

/**
 * A Button triggered by pushing the D-pad to a specific angle.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class dPadButton extends MappedButton {

    /**
     * The angle that the D-pad must be pushed to to trigger this button.
     */
    private final int angle;

    /**
     * The joystick with the relevant D-pad on it.
     */
    @NotNull
    private final Joystick joystick;

    /**
     * Explicit argument constructor.
     *
     * @param joystick The joystick with the D-pad.
     * @param angle    The angle that the D-pad must be pushed to to trigger this button.
     */
    @JsonCreator
    public dPadButton(@NotNull @JsonProperty(required = true) MappedJoystick joystick,
                      @JsonProperty(required = true) int angle) {
        this.angle = angle;
        this.joystick = joystick;
    }

    /**
     * Get whether this button is pressed
     *
     * @return true if the joystick's D-pad is pressed to the given angle, false otherwise.
     */
    @Override
    public boolean get() {
        return joystick.getPOV() == angle;
    }
}
