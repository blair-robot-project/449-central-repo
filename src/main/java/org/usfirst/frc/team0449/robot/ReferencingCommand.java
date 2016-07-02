package org.usfirst.frc.team0449.robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 * <p>
 * A command that has a reference to a specific <code>Robot.java</code> class. Build season code commands can call
 * <code>ReferencingCommand</code>s by passing specific <code>Robot.java</code> classes to these commands, allowing them
 * access to the sepcific subsystems in <code>Robot.java</code>.
 * </p>
 */
public class ReferencingCommand extends Command {
	public MappedSubsystem subsystem;

	/**
	 * <p>
	 * Instantiates a new <code>ReferencingCommand</code> with a given <code>Robot.java</code> class. This is used by
	 * build season code commands calling these library commands with a separate <code>Robot.java</code> not in the
	 * 449 central repo.
	 * </p>
	 *
	 * @param subsystem the subsystem that the <code>ReferencingCommand</code> belongs to
	 */
	public ReferencingCommand(MappedSubsystem subsystem) {
		this.subsystem = subsystem;
	}

	@Override
	public boolean willRunWhenDisabled() {
		return super.willRunWhenDisabled();
	}

	/**
	 * <p>
	 * Instantiates a new <code>ReferencingCommand</code> with a given <code>Robot.java</code> class and a timeout.
	 * The <code>Robot.java</code> class is used by build season code commands calling these library commands with a
	 * separate <code>Robot.java</code> not in the 449 central repo.
	 * </p>
	 *
	 * @param subsystem the subysystem that the <code>{@link ReferencingCommand}</code>  belongs to
	 * @param timeout time in seconds before the command will time out
	 */
	public ReferencingCommand(MappedSubsystem subsystem, double timeout) {
		super(timeout);
		this.subsystem = subsystem;
	}

	@Override
	protected void initialize() {
	}

	@Override
	protected void execute() {
	}

	@Override
	protected boolean isFinished() {
		return true;
	}

	@Override
	protected void end() {
	}

	@Override
	protected void interrupted() {
	}
}
