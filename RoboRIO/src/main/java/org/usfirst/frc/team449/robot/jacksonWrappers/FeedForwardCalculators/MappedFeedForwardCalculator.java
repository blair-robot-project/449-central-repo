package org.usfirst.frc.team449.robot.jacksonWrappers.FeedForwardCalculators;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;

/** Motor feed forward calculator using velocity and acceleration */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "@class")
public class MappedFeedForwardCalculator extends SimpleMotorFeedforward {

  /**
   * Default constructor.
   *
   * @param kS The static gain.
   * @param kV The velocity gain.
   * @param kA The acceleration gain.
   */
  @JsonCreator
  public MappedFeedForwardCalculator(
      @JsonProperty(required = true) final double kS,
      @JsonProperty(required = true) final double kV,
      @JsonProperty(required = true) final double kA) {
    super(kS, kV, kA);
  }
}
