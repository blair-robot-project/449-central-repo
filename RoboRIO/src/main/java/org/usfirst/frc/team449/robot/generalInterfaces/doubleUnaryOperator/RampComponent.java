package org.usfirst.frc.team449.robot.generalInterfaces.doubleUnaryOperator;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.other.Clock;

import java.util.function.DoubleUnaryOperator;

/**
 * A component for limiting the rate of change of a value.
 */
public class RampComponent implements DoubleUnaryOperator, Cloneable {

    /**
     * The maximum allowed change in the value per second.
     */
    private final double maxChangePerMillis;

    /**
     * The value most recently returned.
     */
    private double lastValue;

    /**
     * The time, in milliseconds, that the value most recently returned was returned at.
     */
    private long lastTime;

    /**
     * Default constructor.
     *
     * @param maxChangePerSecond The maximum allowed change in the value per second.
     */
    @JsonCreator
    public RampComponent(double maxChangePerSecond) {
        this.maxChangePerMillis = maxChangePerSecond / 1000.;
    }

    /**
     * Ramp the given value.
     *
     * @param value The current value of whatever it is you're ramping
     * @return The ramped version of that value.
     */
    @Override
    public double applyAsDouble(double value) {
        if (value > lastValue) {
            lastValue = Math.min(value, lastValue + (Clock.currentTimeMillis() - lastTime) * maxChangePerMillis);
        } else {
            lastValue = Math.max(value, lastValue - (Clock.currentTimeMillis() - lastTime) * maxChangePerMillis);
        }
        lastTime = Clock.currentTimeMillis();
        return lastValue;
    }

    /**
     * Get an a copy of this object.
     *
     * @return a new {@link org.usfirst.frc.team449.robot.generalInterfaces.doubleUnaryOperator.RampComponent} with the
     * same max change per second
     */
    @Override
    @NotNull
    public org.usfirst.frc.team449.robot.generalInterfaces.doubleUnaryOperator.RampComponent clone() {
        return new org.usfirst.frc.team449.robot.generalInterfaces.doubleUnaryOperator.RampComponent(maxChangePerMillis * 1000.);
    }
}
