package org.usfirst.frc.team449.robot.jacksonWrappers.FeedForwardCalculators;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.wpi.first.wpilibj.controller.ElevatorFeedforward;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "@class")
public class ElevatorFeedForwardCalculator extends ElevatorFeedforward {

  /**
   * Default constructor.
   *
   * @param kS The static gain.
   * @param kG The gravity gain.
   * @param kV The velocity gain.
   * @param kA The acceleration gain.
   */
  public ElevatorFeedForwardCalculator(
      @JsonProperty(required = true) double kS,
      @JsonProperty(required = true) double kG,
      @JsonProperty(required = true) double kV,
      @JsonProperty(required = true) double kA) {
    super(kS, kG, kV, kA);
  }
}
