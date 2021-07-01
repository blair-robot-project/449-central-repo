package org.usfirst.frc.team449.robot.jacksonWrappers

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import edu.wpi.first.wpilibj.geometry.Pose2d
import edu.wpi.first.wpilibj.geometry.Translation2d

class MappedTranslationSet @JsonCreator constructor(
    /**
     * Pose2d wrapper for Trajectory loading from map
     *
     * @param startingPose The absolute x position in feet
     * @param endingPose The absolute y position in feet
     * @param translations The angle at this position in degrees
     */
    @param:JsonProperty(required = true) var startingPose: Pose2d,
    @param:JsonProperty var translations: List<Translation2d>,
    @param:JsonProperty(required = true) var endingPose: Pose2d
)