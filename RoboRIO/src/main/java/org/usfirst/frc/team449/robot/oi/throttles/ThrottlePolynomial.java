package org.usfirst.frc.team449.robot.oi.throttles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedJoystick;
import org.usfirst.frc.team449.robot.other.Polynomial;

/**
 * A polynomially scaled throttle.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ThrottlePolynomial extends ThrottleDeadbanded {

    /**
     * The polynomially that scales the throttle.
     */
    @NotNull
    protected final Polynomial polynomial;

    /**
     * A basic constructor.
     *
     * @param stick             The Joystick object being used
     * @param axis              The axis being used. 0 is X, 1 is Y, 2 is Z.
     * @param deadband          The deadband below which the input will be read as 0, on [0, 1]. Defaults to 0.
     * @param smoothingTimeSecs How many seconds of input to take into account when smoothing. Defaults to 0.02.
     * @param inverted          Whether or not to invert the joystick input. Defaults to false.
     * @param polynomial        The polynomially that scales the throttle. Must not have any negative exponents.
     */
    @JsonCreator
    public ThrottlePolynomial(@NotNull @JsonProperty(required = true) MappedJoystick stick,
                              @JsonProperty(required = true) int axis,
                              double deadband,
                              @Nullable Double smoothingTimeSecs,
                              boolean inverted,
                              @NotNull @JsonProperty(required = true) Polynomial polynomial) {
        super(stick, axis, deadband, smoothingTimeSecs, inverted);

        //Check for negative exponents
        for (Double power : polynomial.getPowerToCoefficientMap().keySet()) {
            if (power < 0) {
                throw new IllegalArgumentException("Negative exponents are not allowed!");
            }
        }

        //Scale coefficient sum to 1
        polynomial.scaleCoefficientSum(1);

        this.polynomial = polynomial;
    }

    /**
     * Passes the deadbanded joystick output to the polynomial, while preserving sign.
     *
     * @return The processed value of the joystick
     */
    @Override
    public double getValue() {
        return polynomial.applyAsDouble(super.getValue());
    }
}