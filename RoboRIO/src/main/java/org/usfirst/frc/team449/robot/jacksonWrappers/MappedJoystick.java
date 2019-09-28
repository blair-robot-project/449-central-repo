package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.Joystick;
import org.usfirst.frc.team449.robot.generalInterfaces.rumbleable.Rumbleable;

/**
 * A Jackson-compatible wrapper on a {@link Joystick}.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MappedJoystick extends Joystick implements Rumbleable {

    /**
     * Default constructor
     *
     * @param port The USB port of this joystick, on [0, 5].
     */
    @JsonCreator
    public MappedJoystick(@JsonProperty(required = true) int port) {
        super(port);
    }

    /**
     * Rumble at a given strength on each side of the device.
     *
     * @param left  The strength to rumble the left side, on [-1, 1]
     * @param right The strength to rumble the right side, on [-1, 1]
     */
    @Override
    public void rumble(double left, double right) {
        setRumble(RumbleType.kLeftRumble, left);
        setRumble(RumbleType.kRightRumble, right);
    }
}
