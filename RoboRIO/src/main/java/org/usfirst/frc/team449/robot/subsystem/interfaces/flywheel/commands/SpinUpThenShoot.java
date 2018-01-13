package org.usfirst.frc.team449.robot.subsystem.interfaces.flywheel.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.CommandGroup;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.commands.general.WaitForMillis;
import org.usfirst.frc.team449.robot.subsystem.interfaces.flywheel.SubsystemFlywheel;

/**
 * Spin up the flywheel until it's at the target speed, then start feeding in balls.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SpinUpThenShoot extends CommandGroup {

    /**
     * Default constructor.
     *
     * @param subsystem The subsystem to execute this command on.
     */
    @JsonCreator
    public SpinUpThenShoot(@NotNull @JsonProperty(required = true) SubsystemFlywheel subsystem) {
        addSequential(new SpinUpFlywheel(subsystem));
        //Use a wait command here because SpinUpFlywheel is instantaneous.
        addSequential(new WaitForMillis(subsystem.getSpinUpTimeMillis()));
        addSequential(new TurnAllOn(subsystem));
    }
}
