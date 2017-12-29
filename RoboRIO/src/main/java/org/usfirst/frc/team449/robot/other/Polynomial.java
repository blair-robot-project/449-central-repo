package org.usfirst.frc.team449.robot.other;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;

/**
 * A polynomial of a single variable.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Polynomial implements DoubleUnaryOperator {

    /**
     * A map of the powers and coefficients of each term.
     */
    @NotNull
    private final Map<Double, Double> powerToCoefficientMap;

    /**
     * The sign of the most recent input. This is a field to avoid garbage collection.
     */
    private double sign;

    /**
     * The absolute value of the most recent input. This is a field to avoid garbage collection.
     */
    private double abs;

    /**
     * The value to return when get() is called. This is a field to avoid garbage collection.
     */
    private double toRet;

    /**
     * Default constructor.
     *
     * @param powerToCoefficientMap A map of the powers and coefficients of each term. Defaults to [1:1] if null or
     *                              0-length.
     * @param scaleCoefficientSumTo Scales each coefficient so they all add up to this number. Can be null to avoid
     *                              scaling.
     */
    @JsonCreator
    public Polynomial(@Nullable Map<Double, Double> powerToCoefficientMap,
                      @Nullable Double scaleCoefficientSumTo) {
        //Default powerToCoefficientMap to just be [1:1].
        if (powerToCoefficientMap == null || powerToCoefficientMap.size() == 0) {
            this.powerToCoefficientMap = new HashMap<>(1);
            this.powerToCoefficientMap.put(1., 1.);
        } else {
            this.powerToCoefficientMap = powerToCoefficientMap;
        }

        //Scale if scaleCoefficientSumTo isn't null.
        if (scaleCoefficientSumTo != null) {
            scaleCoefficientSum(scaleCoefficientSumTo);
        }
    }

    /**
     * Get the value of the polynomial given x.
     *
     * @param x The variable to be given to the polynomial.
     * @return The value of the polynomial evaluated at |x|, then changed to the sign of x.
     */
    @Override
    public double applyAsDouble(double x) {
        sign = Math.signum(x);
        abs = Math.abs(x);
        toRet = 0;
        for (double power : powerToCoefficientMap.keySet()) {
            toRet += Math.pow(abs, power) * powerToCoefficientMap.get(power);
        }
        return toRet * sign;
    }

    /**
     * Scale each coefficient so they sum to a given number.
     *
     * @param scaleTo The number to scale the sum of coefficients to.
     */
    public void scaleCoefficientSum(double scaleTo) {
        double coefficientSum = 0;
        for (double power : powerToCoefficientMap.keySet()) {
            coefficientSum += powerToCoefficientMap.get(power);
        }
        double scaleFactor = scaleTo / coefficientSum;
        for (double power : powerToCoefficientMap.keySet()) {
            powerToCoefficientMap.replace(power, powerToCoefficientMap.get(power) * scaleFactor);
        }
    }

    /**
     * @return A map of the powers and coefficients of each term.
     */
    @NotNull
    public Map<Double, Double> getPowerToCoefficientMap() {
        return powerToCoefficientMap;
    }
}
