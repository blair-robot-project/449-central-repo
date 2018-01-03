package org.usfirst.frc.team449.robot.commands.autonomous;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.commands.general.WaitForMillis;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedDigitalInput;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.commands.RunLoadedProfile;

/**
 * The autonomous routine to deliver a gear to the center gear.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Auto2017Feeder extends CommandGroup {

    /**
     * Default constructor.
     *
     * @param runWallToPegProfile       The command for running the profile for going from the wall to the peg, which
     *                                  has already been loaded.
     * @param dropGear                  The command for dropping the held gear.
     * @param dropGearSwitch            The switch deciding whether or not to drop the gear.
     * @param allianceSwitch            The switch indicating which alliance we're on.
     * @param runRedBackupProfile       The command for away from the peg, on the red side of the field.
     * @param runBlueBackupProfile      The command for moving away from the peg, on the blue side of the field.
     * @param driveForwardsRed          The command for moving forwards towards the feeder station on the red side.
     * @param driveForwardsBlue         The command for moving forwards towards the feeder station on the blue side.
     * @param waitBetweenProfilesMillis How long to wait between each motion profile. Defaults to 50 if less than 50.
     */
    @JsonCreator
    public Auto2017Feeder(@NotNull @JsonProperty(required = true) RunLoadedProfile runWallToPegProfile,
                          @NotNull @JsonProperty(required = true) Command dropGear,
                          @NotNull @JsonProperty(required = true) MappedDigitalInput dropGearSwitch,
                          @NotNull @JsonProperty(required = true) MappedDigitalInput allianceSwitch,
                          @NotNull @JsonProperty(required = true) Command runRedBackupProfile,
                          @NotNull @JsonProperty(required = true) Command runBlueBackupProfile,
                          @NotNull @JsonProperty(required = true) Command driveForwardsRed,
                          @NotNull @JsonProperty(required = true) Command driveForwardsBlue,
                          long waitBetweenProfilesMillis) {
        waitBetweenProfilesMillis = Math.max(50, waitBetweenProfilesMillis);
        addSequential(runWallToPegProfile);

        //Only do this stuff if we drop the gear
        if (dropGearSwitch.get()) {
            addSequential(dropGear);

            addSequential(new WaitForMillis(waitBetweenProfilesMillis));

            if (allianceSwitch.get()) { //Red is true
                addSequential(runRedBackupProfile);
            } else {
                addSequential(runBlueBackupProfile);
            }

            addSequential(new WaitForMillis(waitBetweenProfilesMillis));

            if (allianceSwitch.get()) {
                addSequential(driveForwardsRed);
            } else {
                addSequential(driveForwardsBlue);
            }
        }
    }
}
