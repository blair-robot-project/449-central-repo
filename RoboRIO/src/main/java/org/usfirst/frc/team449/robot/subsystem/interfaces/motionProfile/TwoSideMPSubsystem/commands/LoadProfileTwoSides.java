package org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.TwoSideMPSubsystem.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.other.MotionProfileData;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.TwoSideMPSubsystem.SubsystemMPTwoSides;

/**
 * Loads the given profiles into the subsystem, but doesn't run it.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class LoadProfileTwoSides extends Command {

    /**
     * The subsystem to execute this command on.
     */
    @NotNull
    private final SubsystemMPTwoSides subsystem;

    /**
     * The motion profiles for the left and right sides to execute, respectively.
     */
    @NotNull
    private final MotionProfileData left, right;

    /**
     * Default constructor
     *
     * @param subsystem The subsystem to execute this command on.
     * @param left      The profile for the left side to run.
     * @param right     The profile for the right side to run.
     */
    @JsonCreator
    public LoadProfileTwoSides(@NotNull @JsonProperty(required = true) SubsystemMPTwoSides subsystem,
                               @NotNull @JsonProperty(required = true) MotionProfileData left,
                               @NotNull @JsonProperty(required = true) MotionProfileData right) {
        this.subsystem = subsystem;
        this.left = left;
        this.right = right;
    }

    /**
     * Log when this command is initialized
     */
    @Override
    protected void initialize() {
        Logger.addEvent("LoadProfileTwoSides init.", this.getClass());
    }

    /**
     * Load the profiles.
     */
    @Override
    protected void execute() {
        subsystem.loadMotionProfile(left, right);
    }

    /**
     * Finish immediately because this is a state-change command.
     *
     * @return true
     */
    @Override
    protected boolean isFinished() {
        return true;
    }

    /**
     * Log when this command ends
     */
    @Override
    protected void end() {
        Logger.addEvent("LoadProfileTwoSides end.", this.getClass());
    }

    /**
     * Log when this command is interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("LoadProfileTwoSides Interrupted!", this.getClass());
    }
}