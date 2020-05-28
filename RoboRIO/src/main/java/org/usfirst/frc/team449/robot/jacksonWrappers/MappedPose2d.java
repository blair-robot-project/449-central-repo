package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;

// TODO Unmap
public class MappedPose2d {

  public Pose2d pose;

  /**
   * Pose2d wrapper for Trajectory loading from map
   *
   * @param xPosition The absolute x position in meters
   * @param yPosition The absolute y position in meters
   * @param angle The angle at this position in degrees
   */
  @JsonCreator
  public MappedPose2d(
      @JsonProperty(required = true) final double xPosition,
      @JsonProperty(required = true) final double yPosition,
      @JsonProperty(required = true) final double angle) {
    this.pose = new Pose2d(xPosition, yPosition, Rotation2d.fromDegrees(angle));
  }
}
