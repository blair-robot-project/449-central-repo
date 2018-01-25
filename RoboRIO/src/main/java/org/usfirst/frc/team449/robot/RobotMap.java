package org.usfirst.frc.team449.robot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedRunnable;
import org.usfirst.frc.team449.robot.oi.buttons.CommandButton;
import org.usfirst.frc.team449.robot.other.DefaultCommand;
import org.usfirst.frc.team449.robot.other.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * The Jackson-compatible object representing the entire robot.
 */
public class RobotMap {

    /**
     * The buttons for controlling this robot. This field only exists to prevent the list from deallocating itself.
     */
    @NotNull
    private final List<CommandButton> buttons;

    /**
     * The logger for recording events and telemetry data.
     */
    @NotNull
    private final Logger logger;

    /**
     * A runnable that updates cached variables.
     */
    @NotNull
    private final Runnable updater;

    /**
     * A map of subsystems to commands that sets the default command for each subsystem to its corresponding command.
     * This field only exists to prevent the list from deallocating itself.
     */
    @Nullable
    private final List<DefaultCommand> defaultCommands;

    /**
     * The command to be run when first enabled in autonomous mode.
     */
    @Nullable
    private final Command autoStartupCommand;

    /**
     * The command to be run when first enabled in teleoperated mode.
     */
    @Nullable
    private final Command teleopStartupCommand;

    /**
     * The command to be run when first enabled.
     */
    @Nullable
    private final Command startupCommand;

    /**
     * Default constructor.
     *
     * @param buttons              The buttons for controlling this robot. Can be null for an empty list.
     * @param logger               The logger for recording events and telemetry data.
     * @param updater              A runnable that updates cached variables.
     * @param defaultCommands      The default commands for various subsystems.
     * @param autoStartupCommand   The command to be run when first enabled in autonomous mode.
     * @param teleopStartupCommand The command to be run when first enabled in teleoperated mode.
     * @param startupCommand       The command to be run when first enabled.
     */
    @JsonCreator
    public RobotMap(@Nullable List<CommandButton> buttons,
                    @NotNull @JsonProperty(required = true) Logger logger,
                    @NotNull @JsonProperty(required = true) MappedRunnable updater,
                    @Nullable List<DefaultCommand> defaultCommands,
                    @Nullable Command autoStartupCommand,
                    @Nullable Command teleopStartupCommand,
                    @Nullable Command startupCommand) {
        this.buttons = buttons != null ? buttons : new ArrayList<>();
        this.logger = logger;
        this.updater = updater;
        this.defaultCommands = defaultCommands;
        this.autoStartupCommand = autoStartupCommand;
        this.teleopStartupCommand = teleopStartupCommand;
        this.startupCommand = startupCommand;
    }

    /**
     * @return The logger for recording events and telemetry data.
     */
    @NotNull
    public Logger getLogger() {
        return logger;
    }

    /**
     * @return The command to be run when first enabled in autonomous mode.
     */
    @Nullable
    public Command getAutoStartupCommand() {
        return autoStartupCommand;
    }

    /**
     * @return The command to be run when first enabled in teleoperated mode.
     */
    @Nullable
    public Command getTeleopStartupCommand() {
        return teleopStartupCommand;
    }

    /**
     * @return The command to be run when first enabled.
     */
    @Nullable
    public Command getStartupCommand() {
        return startupCommand;
    }

    /**
     * @return A runnable that updates cached variables.
     */
    @NotNull
    public Runnable getUpdater() {
        return updater;
    }
}
