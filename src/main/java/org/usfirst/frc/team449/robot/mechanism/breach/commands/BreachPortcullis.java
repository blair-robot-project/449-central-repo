package org.usfirst.frc.team449.robot.mechanism.breach.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.breach.BreachSubsystem;

/**
 * {@link Command} to set the breach arm for breaching the portcullis
 */
public class BreachPortcullis extends ReferencingCommand {

	/**
	 * Instantiate a new <code>BreachPortcullis</code>, taking control of the
	 * breach subsystem.
	 *
	 * @param breach breach subsystem
	 */
	public BreachPortcullis(BreachSubsystem breach) {
		super(breach);
		requires(breach);
	}

	/**
	 * Initialize the {@link Command}, printing the status to the terminal
	 */
	@Override
	protected void initialize() {
		System.out.println("BreachPortcullis init");
	}

	/**
	 * Sets the solenoids to the portcullis (down, (F, F)) position
	 */
	@Override
	protected void execute() {
		((BreachSubsystem) subsystem).setSolenoid(true, true);
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
		System.out.println("BreachPortcullis end");
	}

	/**
	 * Interrupt the <code>Command</code>, printing status to the terminal
	 */
	@Override
	protected void interrupted() {
		System.out.println("BreachPortcullis interupted");
	}
}
