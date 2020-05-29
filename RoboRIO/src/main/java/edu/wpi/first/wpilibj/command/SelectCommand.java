package edu.wpi.first.wpilibj.command;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

/**
 * TODO test the changes
 * A command that runs a command from a dictionary.
 */
public abstract class SelectCommand<T> extends CommandBase {

    /**
     * The Commands to choose from.
     */
    private final Map<T, Command> m_commands;

    /**
     * Stores command chosen by condition.
     */
    private Command m_chosenCommand = null;

    /**
     * Creates a new SelectCommand with given map of selectors and m_commands.
     * <br>
     * Users of this constructor should also override selector().
     *
     * @param commands The map of selectors to the command that should be run if they're chosen via selector().
     */
    public SelectCommand(Map<T, Command> commands) {
        m_commands = commands;

        requireAll();
    }

    /**
     * Creates a new SelectCommand with given map of selectors and m_commands.
     * <p>
     * <p>Users of this constructor should also override selector().
     *
     * @param name     the name for this command group
     * @param commands The map of selectors to the command that should be run if they're chosen via selector().
     */
    public SelectCommand(String name, Map<T, Command> commands) {
        setName(name);
        m_commands = commands;

        requireAll();
    }

    private void requireAll() {
        for (Command c : m_commands.values())
            for (Subsystem requirement : c.getRequirements())
                addRequirements(requirement);
    }

    /**
     * The Selector to determine which command should be run
     *
     * @return the key mapped to the command which should be run
     */
    protected abstract T selector();

    /**
     * Calls {@link SelectCommand#selector()} and runs the proper command.
     */
    @Override
    public void initialize() {
        m_chosenCommand = m_commands.get(selector());

        if (m_chosenCommand != null) {
            /*
             * This is a hack to make cancelling the chosen command inside a
             * CommandGroup work properly
             */
            //m_chosenCommand.clearRequirements();

            m_chosenCommand.initialize();
        }
        super.initialize();
    }

    @Override
    public void cancel() {
        if (m_chosenCommand != null && !m_chosenCommand.isFinished()) {
            m_chosenCommand.cancel();
        }

        super.cancel();
    }

    @Override
    public boolean isFinished() {
        if (m_chosenCommand != null) {
            return m_chosenCommand.isFinished();
        } else {
            return true;
        }
    }

    //TODO Remove this! @Override
    public void end(boolean interrupted) {
        if (interrupted && m_chosenCommand != null && !m_chosenCommand.isFinished()) {
            m_chosenCommand.cancel();
        }
    }
}
