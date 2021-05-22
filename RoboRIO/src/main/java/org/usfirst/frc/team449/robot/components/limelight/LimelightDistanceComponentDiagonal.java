package org.usfirst.frc.team449.robot.components.limelight;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.limelight.Limelight;

import java.util.function.DoubleSupplier;

/** Determines the diagonal distance from the Limelight to a vision target, on the field plane.
 *  Ignores height */
public class LimelightDistanceComponentDiagonal implements DoubleSupplier {

  /** The limelight being used */
  private final Limelight limelight;
  /** The height of the Limelight above the ground */
  private final double limelightHeight;
  /** The mounting angle to the right of the robot front, in degrees */
  private final double limelightAngleRight;
  /** The mounting angle above the horizontal of the limelight, in degrees */
  private final double limelightAngleUp;
  /** The height of the vision target */
  private final double targetHeight;

  /**
   * Default constructor
   *
   * @param limelightHeight The height of the Limelight
   * @param limelightAngleRight The angle of the Limelight, in degrees
   * @param targetHeight the height of the expected vision target, probably provided by the game
   *     manual
   */
  @JsonCreator
  public LimelightDistanceComponentDiagonal(
          @NotNull @JsonProperty(required = true) Limelight limelight,
          @JsonProperty(required = true) double limelightHeight,
          double limelightAngleRight,
          double limelightAngleUp,
          @JsonProperty(required = true) double targetHeight) {
    this.limelight = limelight;
    this.limelightHeight = limelightHeight;
    this.limelightAngleRight = limelightAngleRight;
    this.limelightAngleUp = limelightAngleUp;
    this.targetHeight = targetHeight;
  }

  /** @return Gets the distance from the robot to the vision target, at an angle above the field */
  @Override
  public double getAsDouble() {
    LimelightDistanceComponent distance = new LimelightDistanceComponent(limelight, limelightHeight, limelightAngleUp, targetHeight);
    return distance.getAsDouble()
            * Math.cos(Math.toRadians(limelightAngleRight + limelight.getX()));
  }
}