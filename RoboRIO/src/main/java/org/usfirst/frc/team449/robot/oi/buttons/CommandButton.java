package org.usfirst.frc.team449.robot.oi.buttons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedButton;

/**
 * A button mapped to a command.
 */
public class CommandButton {

    /**
     * Default constructor.
     *
     * @param button  The button that triggers the command.
     * @param command The command to run or cancel.
     * @param action  The action to do to the command.
     */
    @JsonCreator
    public CommandButton(@NotNull @JsonProperty(required = true) MappedButton button,
                         @NotNull @JsonProperty(required = true) Command command,
                         @NotNull @JsonProperty(required = true) Action action) {
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

    /**
     * The possible actions for the button to do to the command.
     */
    enum Action {
        WHEN_PRESSED, WHILE_HELD, WHEN_RELEASED, TOGGLE_WHEN_PRESSED, CANCEL_WHEN_PRESSED
    }
}
