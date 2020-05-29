package org.usfirst.frc.team449.robot.commands.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import org.jetbrains.annotations.NotNull;

/**
 * A ConditionalCommand that chooses which command to run based off of if the switch is on the left or the right.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ConditionalCommandSwitchBased extends ConditionalCommand {

    /**
     * Default constructor.
     *
     * @param leftSwitch  The Command to execute if the left side of the switch is our alliance color.
     * @param rightSwitch The Command to execute if the right side of the switch is our alliance color.
     */
    @JsonCreator
    public ConditionalCommandSwitchBased(@NotNull Command leftSwitch,
                                         @NotNull Command rightSwitch) {
        //BooleanSupplier returns true if m_onTrue should be run or false if m_onFalse should be run.
        super(leftSwitch, rightSwitch, () -> DriverStation.getInstance().getGameSpecificMessage().charAt(0) == 'L');
    }
}
