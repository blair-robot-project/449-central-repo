package org.usfirst.frc.team449.robot.commands.autonomous;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedDigitalInput;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.commands.RunLoadedProfile;

/**
 * TODO check this. The class originally had sequential and parallel commands, so
 *   I just made it a parallel command group and had a local variable that was a
 *   SequentialCommandGroup. Not sure if it'll work
 * The autonomous routine to deliver a gear to the center gear.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Auto2017Boiler extends ParallelCommandGroup {

    /**
     * Default constructor.
     *
     * @param runWallToPegProfile    The command for running the profile for going from the wall to the peg, which has
     *                               already been loaded.
     * @param dropGear               The command for dropping the held gear.
     * @param dropGearSwitch         The switch deciding whether or not to drop the gear.
     * @param allianceSwitch         The switch indicating which alliance we're on.
     * @param runRedPegToKeyProfile  The command for moving from the peg to the key, on the red side of the field.
     * @param runBluePegToKeyProfile The command for moving from the peg to the key, on the blue side of the field.
     * @param spinUpShooter          The command for revving up the flywheel. Can be null.
     * @param fireShooter            The command for firing the flywheel. Can be null.
     * @param waitBetweenProfiles    How long to wait between each motion profile, in seconds. Defaults to .05 if less
     *                               than .05.
     */
    @JsonCreator
    public Auto2017Boiler(@NotNull @JsonProperty(required = true) RunLoadedProfile runWallToPegProfile,
                          @NotNull @JsonProperty(required = true) Command dropGear,
                          @NotNull @JsonProperty(required = true) MappedDigitalInput dropGearSwitch,
                          @NotNull @JsonProperty(required = true) MappedDigitalInput allianceSwitch,
                          @NotNull @JsonProperty(required = true) Command runRedPegToKeyProfile,
                          @NotNull @JsonProperty(required = true) Command runBluePegToKeyProfile,
                          @Nullable Command spinUpShooter,
                          @Nullable Command fireShooter,
                          double waitBetweenProfiles) {
        waitBetweenProfiles = Math.max(.05, waitBetweenProfiles);
        if (spinUpShooter != null) {
            addCommands(spinUpShooter);
        }
        SequentialCommandGroup sequential = new SequentialCommandGroup();
        sequential.addCommands(runWallToPegProfile);
        if (dropGearSwitch.get()) {
            sequential.addCommands(dropGear);
        }

        sequential.addCommands(new WaitCommand(waitBetweenProfiles));

        //Red is true, blue is false
        if (allianceSwitch.get()) {
            sequential.addCommands(runRedPegToKeyProfile);
        } else {
            sequential.addCommands(runBluePegToKeyProfile);
        }

        if (fireShooter != null) {
            sequential.addCommands(fireShooter);
        }
        addCommands(sequential);
    }
}
