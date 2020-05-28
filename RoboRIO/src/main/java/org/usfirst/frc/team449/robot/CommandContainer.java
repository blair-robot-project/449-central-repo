package org.usfirst.frc.team449.robot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import edu.wpi.first.wpilibj2.command.Command;
import io.github.oblarg.oblog.Loggable;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.oi.buttons.CommandButton;
import org.usfirst.frc.team449.robot.other.DefaultCommand;

/**
 * A container class that holds all the commands on the robot, for cleanliness in the map and so
 * that they all appear under the same tab on the dashboard.
 */
public class CommandContainer implements Loggable {

  private final List<DefaultCommand> defaultCommands;

  private final List<CommandButton> buttons;

  private final List<Command> robotStartupCommand;

  private final List<Command> autoStartupCommand;

  private final List<Command> teleopStartupCommand;

  private final List<Command> testStartupCommand;

  @JsonCreator
  public CommandContainer(
      @Nullable @JsonInclude(content = JsonInclude.Include.NON_NULL)
          final List<DefaultCommand> defaultCommands,
      @Nullable @JsonInclude(content = JsonInclude.Include.NON_NULL)
          final List<CommandButton> buttons,
      @Nullable @JsonInclude(content = JsonInclude.Include.NON_NULL)
          final List<Command> robotStartupCommand,
      @Nullable @JsonInclude(content = JsonInclude.Include.NON_NULL)
          final List<Command> autoStartupCommand,
      @Nullable @JsonInclude(content = JsonInclude.Include.NON_NULL)
          final List<Command> teleopStartupCommand,
      @Nullable @JsonInclude(content = JsonInclude.Include.NON_NULL)
          final List<Command> testStartupCommand) {
    this.defaultCommands = defaultCommands;
    this.buttons = buttons;
    this.robotStartupCommand = robotStartupCommand;
    this.autoStartupCommand = autoStartupCommand;
    this.teleopStartupCommand = teleopStartupCommand;
    this.testStartupCommand = testStartupCommand;
  }

  public List<Command> getRobotStartupCommand() {
    return this.robotStartupCommand;
  }

  public List<Command> getAutoStartupCommand() {
    return this.autoStartupCommand;
  }

  public List<Command> getTeleopStartupCommand() {
    return this.teleopStartupCommand;
  }

  public List<Command> getTestStartupCommand() {
    return this.testStartupCommand;
  }

  @Override
  public String configureLogName() {
    return "Commands";
  }
}
