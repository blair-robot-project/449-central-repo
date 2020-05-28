package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.wpilibj.geometry.Translation2d;

// TODO Unmap
public class MappedTranslation2d {

  public Translation2d translation;

  /**
   * Pose2d wrapper for Trajectory loading from map
   *
   * @param xPosition The absolute x position in meters
   * @param yPosition The absolute y position in meters
   */
  @JsonCreator
  public MappedTranslation2d(
      @JsonProperty(required = true) final double xPosition,
      @JsonProperty(required = true) final double yPosition) {
    this.translation = new Translation2d(xPosition, yPosition);
  }
}
