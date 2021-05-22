package org.usfirst.frc.team449.robot.components.limelight;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.limelight.Limelight;

import java.util.function.DoubleSupplier;

/**
 * Determines the distance from the Limelight to a vision target, at an appropriate angle up or down
 * from the field.
 */
public class LimelightDistanceComponent implements DoubleSupplier {

  /** The limelight being used */
  private final Limelight limelight;
  /** The height of the Limelight above the ground.
   * Unit-agnostic, but be consistent within the entire Limelight system */
  private final double limelightHeight;
  /** The mounting angle above the horizontal of the limelight, in degrees */
  private final double limelightAngle;
  /** The height of the vision target.
   * Same units as limelightHeight above */
  private final double targetHeight;

  /**
   * Default constructor
   *
   * @param limelight The limelight that supplies the angles
   * @param limelightHeight The height of the Limelight
   * @param limelightAngleUp The angle of the Limelight, in degrees
   * @param targetHeight The height of the expected vision target, probably provided by the game
   *     manual
   */
  @JsonCreator
  public LimelightDistanceComponent(
          @NotNull @JsonProperty(required = true) Limelight limelight,
          @JsonProperty(required = true) double limelightHeight,
          double limelightAngleUp,
          @JsonProperty(required = true) double targetHeight) {
    this.limelight = limelight;
    this.limelightHeight = limelightHeight;
    this.limelightAngle = limelightAngleUp;
    this.targetHeight = targetHeight;
  }

  /** @return Gets the distance from the robot to the vision target, coplanar with the field */
  @Override
  public double getAsDouble() {
    return (targetHeight - limelightHeight)
            / Math.tan(Math.toRadians(limelightAngle + limelight.getY()));
  }
}