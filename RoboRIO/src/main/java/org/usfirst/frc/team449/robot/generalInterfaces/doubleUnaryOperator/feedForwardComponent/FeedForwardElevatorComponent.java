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
public class FeedForwardElevatorComponent extends FeedForwardComponent {

    /**
     * The upper positions of each elevator stage, in ascending order.
     */
    @NotNull
    private final Double[] positions;

    /**
     * The voltage required to counteract gravity at each elevator stage, in ascending order.
     */
    @NotNull
    private final double[] voltages;

    /**
     * The current talon position. Field to avoid garbage collection.
     */
    private double pos;

    /**
     * Default constructor.
     *
     * @param feetToVoltageMap A map of the upper heights of each elevator stage to the voltage required to counter
     *                         gravity at that stage.
     */
    @JsonCreator
    public FeedForwardElevatorComponent(@NotNull @JsonProperty(required = true) Map<Double, Double> feetToVoltageMap) {
        //Sort the positions and voltages so we can find the correct voltage to use faster.
        positions = feetToVoltageMap.keySet().toArray(new Double[0]);
        Arrays.sort(positions);
        voltages = new double[positions.length];
        for (int i = 0; i < positions.length; i++) {
            voltages[i] = feetToVoltageMap.get(voltages[i]);
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
        return 0; //No MP support
    }

    /**
     * Calculate the voltage for the given input.
     *
     * @param operand the setpoint, in feet, feet/sec, feet/sec^2, etc.
     * @return the feedforward voltage to use for that input.
     */
    @Override
    public double applyAsDouble(double operand) {
        pos = talon.getPositionFeet();
        //Find the appropriate FF to counteract gravity
        for (int i = 0; i < positions.length; i++) {
            if (pos <= positions[i]) {
                //1023/12 converts from voltage to native, and divide by operand because FF gets multiplied by setpoint
                // even in position mode.
                return (voltages[i] / operand);
            }
        }
        return 0;
    }
}
