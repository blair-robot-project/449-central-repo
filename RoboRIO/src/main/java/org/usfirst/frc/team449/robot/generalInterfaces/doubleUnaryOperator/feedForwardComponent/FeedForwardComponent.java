package org.usfirst.frc.team449.robot.generalInterfaces.doubleUnaryOperator.feedForwardComponent;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.FPSTalon;

import java.util.function.DoubleUnaryOperator;

/**
 * A component for calculating feedforwards for a Talon. Takes the setpoint and returns the correct feedforward
 * voltage.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public abstract class FeedForwardComponent implements DoubleUnaryOperator {

    /**
     * The talon this controls the feedforward for.
     */
    protected FPSTalon talon;

    /**
     * Get a FeedForwardComponent that gives no feedforward.
     *
     * @return A FeedForwardComponent whose methods all return 0.
     */
    @NotNull
    public static FeedForwardComponent getZeroFeedForward() {
        return new FeedForwardZeroComponent();
    }

    /**
     * Set the talon to get information from. This is a setter instead of being in the constructor to avoid circular
     * referencing.
     *
     * @param talon The talon this controls the feedforward for.
     */
    public void setTalon(@NotNull FPSTalon talon) {
        this.talon = talon;
    }

    /**
     * Calculate the voltage for a setpoint in MP mode with a position, velocity, and acceleration setpoint.
     *
     * @param positionSetpoint The desired position, in feet.
     * @param velSetpoint      The desired velocity, in feet/sec.
     * @param accelSetpoint    The desired acceleration, in feet/sec^2.
     * @return The voltage, from [-12, 12] needed to achieve that velocity and acceleration.
     */
    public abstract double calcMPVoltage(double positionSetpoint, double velSetpoint, double accelSetpoint);

    /**
     * Calculate the voltage for the given input.
     *
     * @param operand the setpoint, in feet, feet/sec, feet/sec^2, etc.
     * @return the feedforward voltage to use for that input.
     */
    @Override
    public abstract double applyAsDouble(double operand);
}
