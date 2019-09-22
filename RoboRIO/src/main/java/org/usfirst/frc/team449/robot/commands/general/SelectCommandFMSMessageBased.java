package org.usfirst.frc.team449.robot.commands.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.SelectCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SelectCommandFMSMessageBased extends SelectCommand<String> {

    /**
     * Creates a new SelectCommand with given map of selectors and m_commands.
     * <p>
     * <p>Users of this constructor should also override selector().
     *
     * @param commands The map of selectors to the command that should be run if they're chosen via selector().
     */
    @JsonCreator
    public SelectCommandFMSMessageBased(@NotNull @JsonProperty(required = true) Map<String, Command> commands) {
        super(commands);
    }

    /**
     * The Selector to determine which command should be run
     *
     * @return the key mapped to the command which should be run
     */
    @Override
    protected String selector() {
        return DriverStation.getInstance().getGameSpecificMessage();
    }
}
