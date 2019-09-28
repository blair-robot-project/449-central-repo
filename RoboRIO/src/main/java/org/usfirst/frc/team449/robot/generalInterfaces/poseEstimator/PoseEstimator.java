package org.usfirst.frc.team449.robot.generalInterfaces.poseEstimator;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedRunnable;

/**
 * Anything that estimates the absolute pose of the robot.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface PoseEstimator extends MappedRunnable {

    /**
     * Get the current absolute position of the robot
     *
     * @return The current x,y position in feet.
     */
    @NotNull
    double[] getPos();
}
