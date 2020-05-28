package org.usfirst.frc.team449.robot.commands.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SelectCommand;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SelectCommandFMSMessageBased extends SelectCommand {

  /**
   * Creates a new SelectCommand with given map of selectors and m_commands.
   *
   * <p>
   *
   * <p>Users of this constructor should also override selector().
   *
   * @param commands The map of selectors to the command that should be run if they're chosen via
   *     selector().
   */
  @JsonCreator
  public SelectCommandFMSMessageBased(
      @NotNull @JsonProperty(required = true) Map<Object, Command> commands) {
    super(commands, () -> DriverStation.getInstance().getGameSpecificMessage());
  }
}
