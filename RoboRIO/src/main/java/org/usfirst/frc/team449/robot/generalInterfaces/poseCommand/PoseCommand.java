package org.usfirst.frc.team449.robot.generalInterfaces.poseCommand;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.function.Supplier;
import org.usfirst.frc.team449.robot.other.Waypoint;

/**
 * An interface for commands that take waypoints as an argument. These waypoints may change at
 * runtime, so they need a method to update it.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "@class")
public interface PoseCommand {

  /**
   * Set the destination to given values.
   *
   * @param waypoints The points for the path to hit.
   */
  void setWaypoints(Waypoint[] waypoints);

  /**
   * Set the destination to a waypoint array from a function.
   *
   * @param waypointSupplier The supplier for the points for the path to hit.
   */
  void setWaypoints(Supplier<Waypoint[]> waypointSupplier);
}
