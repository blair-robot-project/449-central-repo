package org.usfirst.frc.team449.robot.commands.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import org.jetbrains.annotations.NotNull;

/**
 * A ConditionalCommand that chooses which command to run based off of alliance.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ConditionalCommandAllianceBased extends ConditionalCommand {

    /**
     * Default constructor.
     *
     * @param redCommand  The Command to execute if the robot is on the red alliance
     * @param blueCommand The Command to execute if the robot is on the blue alliance
     */
    @JsonCreator
    public ConditionalCommandAllianceBased(@NotNull @JsonProperty(required = true) Command redCommand,
                                           @NotNull @JsonProperty(required = true) Command blueCommand) {
        super(redCommand, blueCommand);
    }

    /**
     * The Condition to test to determine which Command to run.
     *
     * @return true if m_onTrue should be run or false if m_onFalse should be run.
     */
    @Override
    protected boolean condition() {
        return DriverStation.getInstance().getAlliance().equals(DriverStation.Alliance.Red);
    }
}
