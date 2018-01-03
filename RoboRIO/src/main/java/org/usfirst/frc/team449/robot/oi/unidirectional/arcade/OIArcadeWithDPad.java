package org.usfirst.frc.team449.robot.oi.unidirectional.arcade;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.Joystick;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.loggable.Loggable;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedJoystick;
import org.usfirst.frc.team449.robot.oi.throttles.Throttle;
import org.usfirst.frc.team449.robot.other.Polynomial;

/**
 * An arcade OI with an option to use the D-pad for turning.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class OIArcadeWithDPad extends OIArcade implements Loggable {

    /**
     * How much the D-pad moves the robot rotationally on a 0 to 1 scale, equivalent to pushing the turning stick that
     * much of the way
     */
    private final double dPadShift;

    /**
     * The throttle wrapper for the stick controlling turning velocity
     */
    @NotNull
    private final Throttle rotThrottle;

    /**
     * The throttle wrapper for the stick controlling linear velocity
     */
    @NotNull
    private final Throttle fwdThrottle;

    /**
     * The controller with the D-pad. Can be null if not using D-pad.
     */
    @Nullable
    private final Joystick gamepad;

    /**
     * The polynomial to scale the forwards throttle output by before using it to scale the rotational throttle. Can be
     * null, and if it is, rotational throttle is not scaled by forwards throttle.
     */
    @Nullable
    private final Polynomial scaleRotByFwdPoly;

    /**
     * The scalar that scales the rotational throttle while turning in place.
     */
    private final double turnInPlaceRotScale;

    /**
     * Default constructor
     *
     * @param gamepad             The gamepad containing the joysticks and buttons. Can be null if not using the D-pad.
     * @param rotThrottle         The throttle for rotating the robot.
     * @param fwdThrottle         The throttle for driving the robot straight.
     * @param invertDPad          Whether or not to invert the D-pad. Defaults to false.
     * @param dPadShift           How fast the dPad should turn the robot, on [0, 1]. Defaults to 0.
     * @param scaleRotByFwdPoly   The polynomial to scale the forwards throttle output by before using it to scale the
     *                            rotational throttle. Can be null, and if it is, rotational throttle is not scaled by
     *                            forwards throttle.
     * @param turnInPlaceRotScale The scalar that scales the rotational throttle while turning in place.
     */
    @JsonCreator
    public OIArcadeWithDPad(
            @NotNull @JsonProperty(required = true) Throttle rotThrottle,
            @NotNull @JsonProperty(required = true) Throttle fwdThrottle,
            double dPadShift,
            boolean invertDPad,
            @Nullable MappedJoystick gamepad,
            @Nullable Polynomial scaleRotByFwdPoly,
            @JsonProperty(required = true) double turnInPlaceRotScale) {
        this.dPadShift = (invertDPad ? -1 : 1) * dPadShift;
        this.rotThrottle = rotThrottle;
        this.fwdThrottle = fwdThrottle;
        this.gamepad = gamepad;
        this.scaleRotByFwdPoly = scaleRotByFwdPoly;
        this.turnInPlaceRotScale = turnInPlaceRotScale;
    }

    /**
     * The output of the throttle controlling linear velocity, smoothed and adjusted according to what type of joystick
     * it is.
     *
     * @return The processed stick output, sign-adjusted so 1 is forward and -1 is backwards.
     */
    @Override
    public double getFwd() {
        return fwdThrottle.getValue();
    }

    /**
     * Get the output of the D-pad or turning joystick, whichever is in use. If both are in use, the D-pad takes
     * preference.
     *
     * @return The processed stick or D-pad output, sign-adjusted so 1 is right and -1 is left.
     */
    @Override
    public double getRot() {
        //If the gamepad is being pushed to the left or right
        if (gamepad != null && !(gamepad.getPOV() == -1 || gamepad.getPOV() % 180 == 0)) {
            //Output the shift value
            return gamepad.getPOV() < 180 ? dPadShift : -dPadShift;
        } else if (getFwd() == 0) { //Turning in place
            return rotThrottle.getValue() * turnInPlaceRotScale;
        } else if (scaleRotByFwdPoly != null) { //If we're using Cheezy Drive
            return rotThrottle.getValue() * scaleRotByFwdPoly.applyAsDouble(Math.abs(getFwd()));
        } else { //Plain and simple
            return rotThrottle.getValue();
        }
    }

    /**
     * Get the name of this object.
     *
     * @return A string that will identify this object in the log file.
     */
    @NotNull
    @Override
    public String getName() {
        return "OI";
    }
}
