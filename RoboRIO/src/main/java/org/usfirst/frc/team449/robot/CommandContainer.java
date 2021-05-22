package org.usfirst.frc.team449.robot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import edu.wpi.first.wpilibj2.command.Command;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.oi.buttons.CommandButton;
import org.usfirst.frc.team449.robot.other.DefaultCommand;

import java.util.List;

/**
 * A container class that holds all the commands on the robot, for cleanliness in the map and so
 * that they all appear under the same tab on the dashboard.
 */
public class CommandContainer implements Loggable {
  @Log.Include private final List<DefaultCommand> defaultCommands;
  @Log.Include private final List<CommandButton> buttons;

  private final List<Command> robotStartupCommand;

  private final List<Command> autoStartupCommand;

  private final List<Command> teleopStartupCommand;

  private final List<Command> testStartupCommand;

  @JsonCreator
  public CommandContainer(
      // TODO Figure out why this doesn't work @JsonInclude(JsonInclude.Include.NON_NULL)
      @Nullable @JsonSetter(contentNulls = Nulls.SKIP) final List<DefaultCommand> defaultCommands,
      @Nullable @JsonSetter(contentNulls = Nulls.SKIP) final List<CommandButton> buttons,
      @Nullable @JsonSetter(contentNulls = Nulls.SKIP) final List<Command> robotStartupCommand,
      @Nullable @JsonSetter(contentNulls = Nulls.SKIP) final List<Command> autoStartupCommand,
      @Nullable @JsonSetter(contentNulls = Nulls.SKIP) final List<Command> teleopStartupCommand,
      @Nullable @JsonSetter(contentNulls = Nulls.SKIP) final List<Command> testStartupCommand) {
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
