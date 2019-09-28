package org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.TwoSideMPSubsystem.commands;

import com.fasterxml.jackson.annotation.*;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.components.PathRequester;
import org.usfirst.frc.team449.robot.generalInterfaces.poseCommand.PoseCommand;
import org.usfirst.frc.team449.robot.generalInterfaces.poseEstimator.PoseEstimator;
import org.usfirst.frc.team449.robot.other.Waypoint;
import org.usfirst.frc.team449.robot.subsystem.interfaces.AHRS.SubsystemAHRS;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.TwoSideMPSubsystem.SubsystemMPTwoSides;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.commands.GetPathFromJetson;

import java.util.function.Supplier;

/**
 * A command that drives the given subsystem to an absolute position.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class GoToPose<T extends Subsystem & SubsystemMPTwoSides & SubsystemAHRS> extends CommandGroup implements PoseCommand {

    /**
     * The object to get robot pose from.
     */
    @NotNull
    private final PoseEstimator poseEstimator;
    /**
     * The subsystem to run the path gotten from the Jetson on.
     */
    @NotNull
    private final T subsystem;
    /**
     * The points for the path to hit. Null to use lambdas.
     */
    @Nullable
    private Waypoint[] waypoints;
    /**
     * Getter for the points for the path to hit. Must not be null if the Waypoint[] parameter is null, otherwise is
     * ignored.
     */
    @Nullable
    private Supplier<Waypoint[]> waypointSupplier;

    /**
     * The position gotten from the pose estimator. Field to avoid garbage collection.
     */
    private double[] pos;

    /**
     * Default constructor
     *
     * @param subsystem     The subsystem to run the path gotten from the Jetson on.
     * @param pathRequester The object for interacting with the Jetson.
     * @param poseEstimator The object to get robot pose from.
     * @param waypoints     The points for the path to hit. Can be null to use setters.
     * @param maxVel        The maximum velocity, in feet/second.
     * @param maxAccel      The maximum acceleration, in feet/(second^2)
     * @param maxJerk       The maximum jerk, in feet/(second^3)
     * @param deltaTime     The time between setpoints in the profile, in seconds.
     */
    @JsonCreator
    public GoToPose(@NotNull @JsonProperty(required = true) T subsystem,
                    @NotNull @JsonProperty(required = true) PathRequester pathRequester,
                    @NotNull @JsonProperty(required = true) PoseEstimator poseEstimator,
                    @Nullable Waypoint[] waypoints,
                    @JsonProperty(required = true) double maxVel,
                    @JsonProperty(required = true) double maxAccel,
                    @JsonProperty(required = true) double maxJerk,
                    @JsonProperty(required = true) double deltaTime) {
        this.waypoints = waypoints;
        this.poseEstimator = poseEstimator;
        this.subsystem = subsystem;
        GetPathFromJetson getPath = new GetPathFromJetson(pathRequester, null, deltaTime, maxVel, maxAccel, maxJerk,
                false);
        GoToPositionRelative goToPositionRelative = new GoToPositionRelative<>(getPath, subsystem);
        goToPositionRelative.setWaypoints(this::getWaypoints);
        addSequential(goToPositionRelative);
    }

    /**
     * @return The points for the path to hit, relative to the robot's current position.
     */
    @NotNull
    private Waypoint[] getWaypoints() {
        if (waypointSupplier != null) {
            waypoints = waypointSupplier.get();
        }

        //Get the pose
        pos = poseEstimator.getPos();
        Waypoint[] toRet = new Waypoint[waypoints.length + 1];
        toRet[0] = new Waypoint(pos[0], pos[1], subsystem.getHeadingCached());

        System.arraycopy(waypoints, 0, toRet, 1, waypoints.length);

        return toRet;
    }

    /**
     * Set the destination to a waypoint array from a function.
     *
     * @param waypointSupplier The supplier for the points for the path to hit.
     */
    @Override
    @JsonIgnore
    public void setWaypoints(Supplier<Waypoint[]> waypointSupplier) {
        this.waypoints = null;
        this.waypointSupplier = waypointSupplier;
    }

    /**
     * Set the destination to given values.
     *
     * @param waypoints The points for the path to hit.
     */
    @Override
    @JsonIgnore
    public void setWaypoints(Waypoint[] waypoints) {
        this.waypoints = waypoints;
        this.waypointSupplier = null;
    }
}
