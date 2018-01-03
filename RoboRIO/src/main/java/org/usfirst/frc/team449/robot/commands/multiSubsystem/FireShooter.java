package org.usfirst.frc.team449.robot.commands.multiSubsystem;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.CommandGroup;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.subsystem.interfaces.flywheel.SubsystemFlywheel;
import org.usfirst.frc.team449.robot.subsystem.interfaces.flywheel.commands.TurnAllOn;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.SubsystemIntake;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.commands.SetIntakeMode;

/**
 * Command group for firing the flywheel. Runs flywheel, runs static intake, stops dynamic intake, raises intake, and
 * runs feeder.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class FireShooter extends CommandGroup {

    /**
     * Constructs a FireShooter command group
     *
     * @param subsystemFlywheel flywheel subsystem. Can be null.
     * @param subsystemIntake   intake subsystem. Can be null.
     */
    @JsonCreator
    public FireShooter(@Nullable SubsystemFlywheel subsystemFlywheel,
                       @Nullable SubsystemIntake subsystemIntake) {
        if (subsystemFlywheel != null) {
            addParallel(new TurnAllOn(subsystemFlywheel));
        }
        if (subsystemIntake != null) {
            addParallel(new SetIntakeMode(subsystemIntake, SubsystemIntake.IntakeMode.IN_SLOW));
        }
    }
}
