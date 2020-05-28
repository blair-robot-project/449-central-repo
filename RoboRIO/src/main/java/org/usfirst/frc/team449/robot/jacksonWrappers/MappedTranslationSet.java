package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import java.util.ArrayList;
import java.util.List;

public class MappedTranslationSet {

  /**
   * Pose2d wrapper for Trajectory loading from map
   *
   * @param startingPose The absolute x position in feet
   * @param endingPose The absolute y position in feet
   * @param translations The angle at this position in degrees
   */
  Pose2d startingPose;

  Pose2d endingPose;

  List<Translation2d> translations;

  @JsonCreator
  public MappedTranslationSet(
      @JsonProperty(required = true) final MappedPose2d startingPose,
      @JsonProperty final List<MappedTranslation2d> translations,
      @JsonProperty(required = true) final MappedPose2d endingPose) {
    this.startingPose = startingPose.pose;
    this.endingPose = endingPose.pose;
    this.translations = new ArrayList<>();
    if (translations != null) {
      for (final var entry : translations) {
        this.translations.add(entry.translation);
      }
    }
  }

  public Pose2d getStartingPose() {
    return this.startingPose;
  }

  public Pose2d getEndingPose() {
    return this.endingPose;
  }

  public List<Translation2d> getTranslations() {
    return this.translations;
  }
}
