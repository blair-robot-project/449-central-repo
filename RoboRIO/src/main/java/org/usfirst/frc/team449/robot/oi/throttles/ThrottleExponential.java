package org.usfirst.frc.team449.robot.oi.throttles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedJoystick;

/**
 * An exponentially-scaled throttle.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ThrottleExponential extends ThrottleDeadbanded {

    /**
     * The base that is raised to the power of the joystick input.
     */
    protected final double base;

    /**
     * The input from the joystick. Declared outside of getValue to avoid garbage collection.
     */
    private double input;

    /**
     * The sign of the input from the joystick. Declared outside of getValue to avoid garbage collection.
     */
    private double sign;

    /**
     * A basic constructor.
     *
     * @param stick             The Joystick object being used
     * @param axis              The axis being used.
     * @param deadband          The deadband below which the input will be read as 0, on [0, 1]. Defaults to 0.
     * @param smoothingTimeSecs How many seconds of input to take into account when smoothing. Defaults to 0.02.
     * @param inverted          Whether or not to invert the joystick input. Defaults to false.
     * @param base              The base that is raised to the power of the joystick input.
     */
    @JsonCreator
    public ThrottleExponential(@NotNull @JsonProperty(required = true) MappedJoystick stick,
                               @JsonProperty(required = true) int axis,
                               double deadband,
                               @Nullable Double smoothingTimeSecs,
                               boolean inverted,
                               @JsonProperty(required = true) double base) {
        super(stick, axis, deadband, smoothingTimeSecs, inverted);
        this.base = base;
    }

    /**
     * Raises the base to the value of the deadbanded joystick output, adjusting for sign.
     *
     * @return The processed value of the joystick
     */
    @Override
    public double getValue() {
        input = super.getValue();

        //Extract the sign
        sign = Math.signum(input);
        input = Math.abs(input);

        //Exponentially scale
        input = (Math.pow(base, input) - 1.) / (base - 1.);

        return sign * input;
    }
}