package org.usfirst.frc.team449.robot.generalInterfaces.doubleUnaryOperator.FeedForwardComponent;

/**
 * A {@link FeedForwardComponent} that gives no output.
 */
class FeedForwardZeroComponent extends FeedForwardComponent {

    /**
     * Default constructor. Not public and not annotated with @JsonCreator because it should only be constructed via
     * {@link FeedForwardComponent}'s getZeroFeedForward().
     */
    FeedForwardZeroComponent() {
        //Do nothing
    }

    /**
     * Calculate the voltage for a setpoint in MP mode with a position, velocity, and acceleration setpoint.
     *
     * @param positionSetpoint The desired position, in feet.
     * @param velSetpoint      The desired velocity, in feet/sec.
     * @param accelSetpoint    The desired acceleration, in feet/sec^2.
     * @return The voltage, from [-12, 12] needed to achieve that velocity and acceleration.
     */
    @Override
    public double calcMPVoltage(double positionSetpoint, double velSetpoint, double accelSetpoint) {
        return 0;
    }

    /**
     * Calculate the feedforward for the given input.
     *
     * @param operand the setpoint, in feet, feet/sec, feet/sec^2, etc.
     * @return the feedforward (kF gain) to use for that input.
     */
    @Override
    public double applyAsDouble(double operand) {
        return 0;
    }
}
