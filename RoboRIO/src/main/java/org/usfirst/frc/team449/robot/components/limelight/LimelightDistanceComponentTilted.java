package org.usfirst.frc.team449.robot.components.limelight;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.function.DoubleSupplier;

/** Determines the distance from the Limelight to a vision target. */
public class LimelightDistanceComponentTilted implements DoubleSupplier {

  /** The height of the Limelight above the ground */
  private final double limelightHeight;
  /** The mounting angle above the horizontal of the limelight, in degrees */
  private final double limelightAngle;
  /** The height of the vision target */
  private final double targetHeight;

  /**
   * Default constructor
   *
   * @param limelightHeight The height of the Limelight
   * @param limelightAngleDown The angle of the Limelight, in degrees
   * @param targetHeight the height of the expected vision target, probably provided by the game
   *     manual
   */
  @JsonCreator
  public LimelightDistanceComponentTilted(
      @JsonProperty(required = true) double limelightHeight,
      @JsonProperty(required = true) double limelightAngleDown,
      @JsonProperty(required = true) double targetHeight) {
    this.limelightHeight = limelightHeight;
    this.limelightAngle = limelightAngleDown;
    this.targetHeight = targetHeight;
  }

  /** @return Gets the distance from the robot to the vision target, at an angle above the field */
  @Override
  public double getAsDouble() {
    DoubleSupplier robotToTargAngle = new LimelightComponent(LimelightComponent.ReturnValue.y, 0);
    return (targetHeight - limelightHeight)
        / Math.sin(Math.toRadians(limelightAngle + robotToTargAngle.getAsDouble()));
  }
}
