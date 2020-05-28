package edu.wpi.first.wpilibj.command;

import java.util.Enumeration;
import java.util.Map;

/**
 * A command that runs a command from a dictionary.
 */
public abstract class SelectCommand<T> extends CommandBase {

    /**
     * The Commands to choose from.
     */
    private Map<T, Command> m_commands;

    /**
     * Stores command chosen by condition.
     */
    private Command m_chosenCommand = null;

    /**
     * Creates a new SelectCommand with given map of selectors and m_commands.
     * <p>
     * <p>Users of this constructor should also override selector().
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
        super(name);
        m_commands = commands;

        requireAll();
    }

    private void requireAll() {
        for (T key : m_commands.keySet()) {
            for (Enumeration e = m_commands.get(key).getRequirements(); e.hasMoreElements(); ) {
                requires((Subsystem) e.nextElement());
            }
        }
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
    protected void _initialize() {
        m_chosenCommand = m_commands.get(selector());

        if (m_chosenCommand != null) {
            /*
             * This is a hack to make cancelling the chosen command inside a
             * CommandGroup work properly
             */
            m_chosenCommand.clearRequirements();

            m_chosenCommand.start();
        }
        super._initialize();
    }

    @Override
    protected void _cancel() {
        if (m_chosenCommand != null && m_chosenCommand.isRunning()) {
            m_chosenCommand.cancel();
        }

        super._cancel();
    }

    @Override
    public boolean isFinished() {
        if (m_chosenCommand != null) {
            return m_chosenCommand.isCompleted();
        } else {
            return true;
        }
    }

    @Override
    protected void _interrupted() {
        if (m_chosenCommand != null && m_chosenCommand.isRunning()) {
            m_chosenCommand.cancel();
        }

        super._interrupted();
    }
}
