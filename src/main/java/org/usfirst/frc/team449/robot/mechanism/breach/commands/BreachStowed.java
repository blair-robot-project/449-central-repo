package org.usfirst.frc.team449.robot.mechanism.breach.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.breach.BreachSubsystem;

/**
 * <code>Command</code> that sets the breach arm to the stowed state.
 */
public class BreachStowed extends ReferencingCommand {
	/**
	 * Instantiate a new <code>BreachStowed</code>, taking control of the breach
	 * subsystem.
	 *
	 * @param breach breach subsystem
	 */
	public BreachStowed(BreachSubsystem breach) {
		super(breach);
		requires(breach);
	}

	/**
	 * Initialize the {@link Command}, printing the status to the terminal
	 */
	@Override
	protected void initialize() {
		System.out.println("BreachStowed init");
	}

	/**
	 * Sets the solenoids to the stowed (up, (R, R)) position
	 */
	@Override
	protected void execute() {
		((BreachSubsystem) subsystem).setSolenoid(false, false);
	}

	/**
	 * Returns <code>true</code> to the Scheduler, as the <code>Command</code>
	 * is complete as soon as <code>execute</code> is run, firing the solenoids.
	 */
	@Override
	protected boolean isFinished() {
		return true;
	}

	/**
	 * End the <code>Command</code>, printing status to the terminal
	 */
	@Override
	protected void end() {
		System.out.println("BreachStowed end");
	}

	/**
	 * Interrupt the <code>Command</code>, printing status to the terminal
	 */
	@Override
	protected void interrupted() {
		System.out.println("BreachStowed interupted");
	}
}
