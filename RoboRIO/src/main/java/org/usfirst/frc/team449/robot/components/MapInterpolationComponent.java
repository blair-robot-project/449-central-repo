package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MapInterpolationComponent {

  /**
   * LINEAR, COSINE, or CUBIC
   */
  private InterpolationMethod currentMethod;

  /**
   * LookUpTable, the table of experimentally optimized values
   */
  private final TreeMap<Double, Double> LUT;

  /**
   * Upper and lower limits of an interpolation calc
   */
  private Map.Entry<Double, Double> upper;
  private Map.Entry<Double, Double> lower;

  /**
   * Default constructor
   * @param method the interpolation method
   * @param entries the list of experimentally derived values for the LUT
   */
  @JsonCreator
  public MapInterpolationComponent(
      @JsonProperty(required = true) InterpolationMethod method,
      @JsonProperty(required = true) List<Map.Entry<Double, Double>> entries) {
    currentMethod = method;
    LUT = new TreeMap<>();
    for (Map.Entry<Double, Double> entry : entries) {
      LUT.put(entry.getKey(), entry.getValue());
    }
  }

  /**
   * Changes the interpolation method
   */
  public void updateMethod(InterpolationMethod method) {
    currentMethod = method;
  }

  /**
   * Calculates the appropriate value from distance x
   * @param x the distance from the target
   * @return the shooter velocity from distance x
   */
  public double calculate(double x) {
    if (LUT.containsKey(x)) {
      return LUT.get(x);
    }
    setBounds(x);
    double ratio;
    try {
      ratio = (x - lower.getKey()) / (upper.getKey() - lower.getKey());
    } catch (ArithmeticException e) {
      ratio = 0;
    }
    switch (currentMethod) {
      case LINEAR:
        return linear(ratio);
      case COSINE:
        return cosine(ratio);
      case CUBIC:
        return cubic(ratio);
      default:
        return 0;
    }
  }

  /**
   * Sets the upper and lower bounds of the interpolation from distance x
   * @param x the distance from the target
   */
  private void setBounds(double x) {
    lower = LUT.floorEntry(x) != null ? LUT.floorEntry(x) : new AbstractMap.SimpleEntry<>(0., 0.);
    upper =
        LUT.ceilingEntry(x) != null ? LUT.ceilingEntry(x) : new AbstractMap.SimpleEntry<>(0., 0.);
  }

  /**
   * Linear interpolation method
   * @param x the distance from the target
   * @return the shooter vel
   */
  private double linear(double x) {
    return lower.getValue() * (1 - x) + upper.getValue() * x;
  }

  /**
   * Cosine interpolation method
   * @param x the distance from the target
   * @return the shooter vel
   */
  private double cosine(double x) {
    double smoothpoint = (1 - Math.cos(x * Math.PI)) / 2;
    return linear(smoothpoint);
  }

  /**
   * Cubic interpolation method
   * @param x the distance from the target
   * @return the shooter vel
   */
  private double cubic(double x) {
    //The entries above higher and below lower
    Map.Entry<Double, Double> highUpper = LUT.higherEntry(upper.getKey());
    Map.Entry<Double, Double> lowLower = LUT.lowerEntry(lower.getKey());
    //Use linear if the segment is on the end
    if(highUpper == null || lowLower == null){
      return linear(x);
    }
    //Some weird coefficients to simplify calculations
    double c1, c2, c3, c4;
    c1 = highUpper.getValue() + lower.getValue() - upper.getValue() - lowLower.getValue();
    c2 = lowLower.getValue() - lower.getValue() - c1;
    c3 = upper.getValue() - lowLower.getValue();
    c4 = lower.getValue();
    return (c1*Math.pow(x, 3) + c2*Math.pow(x, 2) + c3*x + c4);
  }

  // http://paulbourke.net/miscellaneous/interpolation/
  enum InterpolationMethod {
    LINEAR,
    COSINE,
    CUBIC //this one is a bit sketchy...
  }
}
