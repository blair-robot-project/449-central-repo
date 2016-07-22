package org.usfirst.frc.team449.robot.mechanism.breach.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.breach.BreachSubsystem;

/**
 * {@link Command}that sets the breach arm to the state for breaching the
 * chivald de fries
 */
public class BreachChivald extends ReferencingCommand {
	/**
	 * Instantiate a new <code>BreachChivald</code>, taking control of the
	 * breach subsystem.
	 *
	 * @param breach breach subystem
	 */
	public BreachChivald(BreachSubsystem breach) {
		super(breach);
		requires(breach);
	}

	@Override
	protected void initialize() {
		System.out.println("BreachChivald init");
	}

	@Override
	protected void execute() {
		((BreachSubsystem) subsystem).setSolenoid(true, false);
	}

	@Override
	protected boolean isFinished() {
		return true;
	}

	@Override
	protected void end() {
		System.out.println("BreachChivald end");
	}

	@Override
	protected void interrupted() {
		System.out.println("BreachChivald interupted");
	}
}
