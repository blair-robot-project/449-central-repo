package org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.commands;

import com.fasterxml.jackson.annotation.*;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.components.PathRequester;
import org.usfirst.frc.team449.robot.generalInterfaces.poseCommand.PoseCommand;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.other.MotionProfileData;
import org.usfirst.frc.team449.robot.other.Waypoint;

import java.util.function.Supplier;

/**
 * Requests and receives a profile from the Jetson, accessible via a getter.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class GetPathFromJetson extends Command implements PoseCommand {

    /**
     * The object for interacting with the Jetson.
     */
    @NotNull
    private final PathRequester pathRequester;
    /**
     * The time between setpoints in the profile, in seconds.
     */
    private final double deltaTime;
    /**
     * Whether to reset the encoder position before running the profile.
     */
    private final boolean resetPosition;
    /**
     * The maximum velocity, in feet/second.
     */
    private final double maxVel;
    /**
     * The maximum acceleration, in feet/(second^2)
     */
    private final double maxAccel;
    /**
     * The maximum jerk, in feet/(second^3)
     */
    private final double maxJerk;
    /**
     * Whether or not to invert the motion profile.
     */
    private boolean inverted;
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
     * The motion profile to return.
     */
    @Nullable
    private MotionProfileData[] motionProfileData;

    /**
     * Default constructor.
     *
     * @param pathRequester The object for interacting with the Jetson.
     * @param waypoints     The points for the path to hit. Can be null to use setters.
     * @param deltaTime     The time between setpoints in the profile, in seconds.
     * @param maxVel        The maximum velocity, in feet/second.
     * @param maxAccel      The maximum acceleration, in feet/(second^2)
     * @param maxJerk       The maximum jerk, in feet/(second^3)
     * @param resetPosition Whether or not to reset the encoder position before running the profile.
     */
    @JsonCreator
    public GetPathFromJetson(@NotNull @JsonProperty(required = true) PathRequester pathRequester,
                             @Nullable Waypoint[] waypoints,
                             @JsonProperty(required = true) double deltaTime,
                             @JsonProperty(required = true) double maxVel,
                             @JsonProperty(required = true) double maxAccel,
                             @JsonProperty(required = true) double maxJerk,
                             boolean resetPosition) {
        this.pathRequester = pathRequester;
        this.waypoints = waypoints;
        this.deltaTime = deltaTime;
        this.resetPosition = resetPosition;
        this.maxVel = maxVel;
        this.maxAccel = maxAccel;
        this.maxJerk = maxJerk;
    }

    /**
     * Log when this command is initialized and send the request to the Jetson.
     */
    @Override
    protected void initialize() {
        Logger.addEvent("GetPathFromJetson init", this.getClass());
        //Check if we're using the supplier or the parameter
        if (waypointSupplier != null) {
            waypoints = waypointSupplier.get();
        }

        //Check inversion
        inverted = waypoints[0].getX() < 0;
        if (inverted) {
            for (Waypoint waypoint : waypoints) {
                waypoint.setX(-waypoint.getX());
                waypoint.setThetaDegrees(-waypoint.getThetaDegrees());
            }
        }

        //Request the path
        pathRequester.requestPath(waypoints, deltaTime, maxVel, maxAccel, maxJerk);

        //Wipe any previous profiles
        motionProfileData = null;
    }

    /**
     * Receive the path, or null if the Jetson hasn't replied yet.
     */
    @Override
    protected void execute() {
        motionProfileData = pathRequester.getPath(inverted, resetPosition);
    }

    /**
     * Stop when profile received.
     *
     * @return true when the profile is received from the Jetson, false otherwise.
     */
    @Override
    protected boolean isFinished() {
        return motionProfileData != null;
    }

    /**
     * Log that the command has ended.
     */
    @Override
    protected void end() {
        Logger.addEvent("GetPathFromJetson end", this.getClass());
    }

    /**
     * Log that the command has been interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("GetPathFromJetson interrupted!", this.getClass());
    }

    /**
     * @return The motion profile gotten from the Jetson or null if not finished yet.
     */
    @Nullable
    public MotionProfileData[] getMotionProfileData() {
        if (inverted && motionProfileData.length == 2) {
            return new MotionProfileData[]{motionProfileData[1], motionProfileData[0]};
        }
        return motionProfileData;
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
}
