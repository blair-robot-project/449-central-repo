package org.usfirst.frc.team449.robot.other;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.Subsystem;
import io.github.oblarg.oblog.Loggable;
import org.jetbrains.annotations.NotNull;

/** A class that sets the default command for a subsystem when constructed. */
public class DefaultCommand implements Loggable {

  /** The command of this defaultCommand; field to allow logging */
  private Command command;

  /**
   * Sets the given command as the default command for the given subsystem.
   *
   * @param subsystem The subsystem to set the default command for.
   * @param command The command to set as the default.
   */
  @JsonCreator
  public DefaultCommand(
      @NotNull @JsonProperty(required = true) Subsystem subsystem,
      @NotNull @JsonProperty(required = true) Command command) {
    // Check if it's an instant command and warn the user if it is
    if (InstantCommand.class.isAssignableFrom(command.getClass())) {
      System.out.println(
          "You're trying to set an InstantCommand as a default command! This is a really bad "
              + "idea!");
      System.out.println("Subsystem: " + subsystem.getClass().toString());
      System.out.println("Command: " + command.getClass().toString());
    }
    subsystem.setDefaultCommand(command);
    this.command = command;
  }

  @Override
  public boolean skipLayout() {
    return true;
  }
}
