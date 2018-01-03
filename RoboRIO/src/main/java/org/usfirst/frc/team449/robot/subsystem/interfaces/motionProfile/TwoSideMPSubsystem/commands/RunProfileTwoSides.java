package org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.TwoSideMPSubsystem.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.CommandGroup;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.other.MotionProfileData;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.TwoSideMPSubsystem.SubsystemMPTwoSides;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.commands.RunLoadedProfile;

/**
 * Loads and runs the given profiles into the given subsystem.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class RunProfileTwoSides<T extends YamlSubsystem & SubsystemMPTwoSides> extends CommandGroup {

    /**
     * Default constructor.
     *
     * @param subsystem The subsystem to execute this command on.
     * @param left      The motion profile for the left side to load and execute.
     * @param right     The motion profile for the right side to load and execute.
     * @param timeout   The maximum amount of time this command is allowed to take, in seconds.
     */
    @JsonCreator
    public RunProfileTwoSides(@NotNull @JsonProperty(required = true) T subsystem,
                              @NotNull @JsonProperty(required = true) MotionProfileData left,
                              @NotNull @JsonProperty(required = true) MotionProfileData right,
                              @JsonProperty(required = true) double timeout) {
        addParallel(new LoadProfileTwoSides(subsystem, left, right));
        addParallel(new RunLoadedProfile<>(subsystem, timeout, true));
    }
}
