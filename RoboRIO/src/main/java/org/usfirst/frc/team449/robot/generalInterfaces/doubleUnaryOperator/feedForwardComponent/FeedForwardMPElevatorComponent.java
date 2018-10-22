package org.usfirst.frc.team449.robot.generalInterfaces.doubleUnaryOperator.feedForwardComponent;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;

/**
 * A {@link FeedForwardComponent} for an elevator, where the feed-forward needed changes based on how many stages are
 * lifted.
 */
public class FeedForwardMPElevatorComponent extends FeedForwardComponent {

    /**
     * The upper positions of each elevator stage, in ascending order.
     */
    @NotNull
    private final Double[] positions;

    /**
     * The feed forwards at each elevator stage, in ascending order.
     */
    @NotNull
    private final FeedForwardComponent[] feedForwardComponents;

    /**
     * The current talon position. Field to avoid garbage collection.
     */
    private double pos;

    /**
     * Default constructor.
     *
     * @param feetToFFComponentMap A map of the upper heights of each elevator stage to the {@link FeedForwardComponent}
     *                             that should be used for that stage.
     */
    @JsonCreator
    public FeedForwardMPElevatorComponent(@NotNull @JsonProperty(required = true) Map<Double, FeedForwardComponent> feetToFFComponentMap) {
        //Sort the positions and voltages so we can find the correct voltage to use faster.
        positions = feetToFFComponentMap.keySet().toArray(new Double[0]);
        Arrays.sort(positions);
        feedForwardComponents = new FeedForwardComponent[positions.length];
        for (int i = 0; i < positions.length; i++) {
            feedForwardComponents[i] = feetToFFComponentMap.get(positions[i]);
        }
    }

    /**
     * Calculate the voltage for a setpoint in MP mode with a position, velocity, and acceleration setpoint.
     *
     * @param positionSetpoint The desired position, in feet.
     * @param velSetpoint      The desired velocity, in feet/sec.
     * @param accelSetpoint    The desired acceleration, in feet/sec^2.
     * @return The voltage, from [-12, 12], needed to achieve that velocity and acceleration.
     */
    @Override
    public double calcMPVoltage(double positionSetpoint, double velSetpoint, double accelSetpoint) {
        //Find the appropriate component
        for (int i = 0; i < positions.length; i++) {
            if (positionSetpoint <= positions[i]) {
                return (feedForwardComponents[i].calcMPVoltage(positionSetpoint, velSetpoint, accelSetpoint));
            }
        }
        System.out.println("Catch case!");
        //Catch case.
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
        pos = talon.getPositionFeet();
        //Find the appropriate component
        for (int i = 0; i < positions.length; i++) {
            if (pos <= positions[i]) {
                return (feedForwardComponents[i].applyAsDouble(operand));
            }
        }
        //Catch case.
        return 0;
    }
}
