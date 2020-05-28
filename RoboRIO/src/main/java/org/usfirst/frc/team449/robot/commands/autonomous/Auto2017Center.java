package org.usfirst.frc.team449.robot.commands.autonomous;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedDigitalInput;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.commands.RunLoadedProfile;

/**
 * The autonomous routine to deliver a gear to the center gear.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Auto2017Center extends SequentialCommandGroup {

    /**
     * Default constructor.
     *
     * @param runWallToPegProfile The command for running the profile for going from the wall to the peg, which has
     *                            already been loaded.
     * @param dropGear            The command for dropping the held gear.
     * @param dropGearSwitch      The switch deciding whether or not to drop the gear.
     * @param driveBack           The command for backing up away from the peg.
     */
    @JsonCreator
    public Auto2017Center(
            @NotNull @JsonProperty(required = true) RunLoadedProfile runWallToPegProfile,
            @NotNull @JsonProperty(required = true) Command dropGear,
            @NotNull @JsonProperty(required = true) MappedDigitalInput dropGearSwitch,
            @NotNull @JsonProperty(required = true) Command driveBack) {
        addCommands(runWallToPegProfile, driveBack);
        if (dropGearSwitch.get()) {
            addCommands(dropGear);
        }
    }
}
