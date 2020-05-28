package org.usfirst.frc.team449.robot.oi.buttons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.Button;
import io.github.oblarg.oblog.Loggable;
import org.jetbrains.annotations.NotNull;

/** A button mapped to a command. */
public class CommandButton implements Loggable {

  /** The command mapped to the button. Field to allow logging. */
  private final Command command;

  /**
   * Default constructor.
   *
   * @param button The button that triggers the command.
   * @param command The command to run or cancel.
   * @param action The action to do to the command.
   */
  @JsonCreator
  public CommandButton(
      @NotNull @JsonProperty(required = true) final Button button,
      @NotNull @JsonProperty(required = true) final Command command,
      @NotNull @JsonProperty(required = true) final Action action) {

    this.command = command;
    switch (action) {
      case WHILE_HELD:
        button.whileHeld(command);
        break;
      case WHEN_PRESSED:
        button.whenPressed(command);
        break;
      case WHEN_RELEASED:
        button.whenReleased(command);
        break;
      case CANCEL_WHEN_PRESSED:
        button.cancelWhenPressed(command);
        break;
      case TOGGLE_WHEN_PRESSED:
        button.toggleWhenPressed(command);
        break;
    }
  }

  @Override
  public boolean skipLayout() {
    return true;
  }

  /** The possible actions for the button to do to the command. */
  enum Action {
    WHEN_PRESSED,
    WHILE_HELD,
    WHEN_RELEASED,
    TOGGLE_WHEN_PRESSED,
    CANCEL_WHEN_PRESSED
  }
}
