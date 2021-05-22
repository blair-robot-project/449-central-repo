package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.wpi.first.wpilibj.trajectory.Trajectory;

/** TODO add some actual javadocs here */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "@class")
public interface TrajectoryGenerationComponent {

  Trajectory getTrajectory();
}
