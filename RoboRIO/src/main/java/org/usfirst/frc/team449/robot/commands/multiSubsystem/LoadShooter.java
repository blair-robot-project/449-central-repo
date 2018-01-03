package org.usfirst.frc.team449.robot.commands.multiSubsystem;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.CommandGroup;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.subsystem.interfaces.flywheel.SubsystemFlywheel;
import org.usfirst.frc.team449.robot.subsystem.interfaces.flywheel.commands.TurnAllOff;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.SubsystemIntake;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.commands.SetIntakeMode;
import org.usfirst.frc.team449.robot.subsystem.interfaces.solenoid.SubsystemSolenoid;
import org.usfirst.frc.team449.robot.subsystem.interfaces.solenoid.commands.SolenoidReverse;

/**
 * Command group for intaking balls from the ground. Stops flywheel, runs static intake, runs dynamic intake, lowers
 * intake, and stops feeder.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class LoadShooter<T extends SubsystemIntake & SubsystemSolenoid> extends CommandGroup {

    /**
     * Constructs a LoadShooter command group
     *
     * @param subsystemFlywheel flywheel subsystem. Can be null.
     * @param subsystemIntake   intake subsystem. Can be null.
     */
    @JsonCreator
    public LoadShooter(@Nullable SubsystemFlywheel subsystemFlywheel,
                       @Nullable T subsystemIntake) {
        if (subsystemFlywheel != null) {
            addParallel(new TurnAllOff(subsystemFlywheel));
        }
        if (subsystemIntake != null) {
            addParallel(new SolenoidReverse(subsystemIntake));
            addParallel(new SetIntakeMode(subsystemIntake, SubsystemIntake.IntakeMode.IN_FAST));
        }
    }
}
