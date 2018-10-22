package org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.TwoSideMPSubsystem.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.poseCommand.PoseCommand;
import org.usfirst.frc.team449.robot.other.MotionProfileData;
import org.usfirst.frc.team449.robot.other.Waypoint;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.TwoSideMPSubsystem.SubsystemMPTwoSides;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.commands.GetPathFromJetson;

import java.util.function.Supplier;

/**
 * A command that drives the given subsystem to a position relative to the current position.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class GoToPositionRelative<T extends Subsystem & SubsystemMPTwoSides> extends CommandGroup implements PoseCommand {

    /**
     * The command for getting the path from the Jetson.
     */
    private final GetPathFromJetson getPath;

    /**
     * Default constructor.
     *
     * @param getPath   The command to get a path from the Jetson.
     * @param subsystem The subsystem to run the path gotten from the Jetson on.
     */
    @JsonCreator
    public GoToPositionRelative(@NotNull @JsonProperty(required = true) GetPathFromJetson getPath,
                                @NotNull @JsonProperty(required = true) T subsystem) {
        this.getPath = getPath;
        addSequential(this.getPath);
        addSequential(new RunProfileTwoSides<>(subsystem, this::getLeft,
                this::getRight, 10));
    }

    /**
     * Set the destination to given values.
     *
     * @param waypoints The points for the path to hit.
     */
    @Override
    public void setWaypoints(Waypoint[] waypoints) {
        getPath.setWaypoints(waypoints);
    }

    /**
     * Set the destination to a waypoint array from a function.
     *
     * @param waypointSupplier The supplier for the points for the path to hit.
     */
    @Override
    public void setWaypoints(Supplier<Waypoint[]> waypointSupplier) {
        getPath.setWaypoints(waypointSupplier);
    }

    /**
     * @return The motion profile for the left side to run, or null if not received from the Jetson yet.
     */
    @Nullable
    private MotionProfileData getLeft() {
        if (getPath.getMotionProfileData() == null) {
            return null;
        } else {
            return getPath.getMotionProfileData()[0];
        }
    }

    /**
     * @return The motion profile for the right side to run, or null if not received from the Jetson yet.
     */
    @Nullable
    private MotionProfileData getRight() {
        if (getPath.getMotionProfileData() == null) {
            return null;
        } else if (getPath.getMotionProfileData().length < 2) {
            //If it's only 1 profile, then it's the same one for both sides.
            return getPath.getMotionProfileData()[0];
        } else {
            return getPath.getMotionProfileData()[1];
        }
    }
}
