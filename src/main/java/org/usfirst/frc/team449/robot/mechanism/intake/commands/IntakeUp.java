package org.usfirst.frc.team449.robot.mechanism.intake.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.intake.IntakeSubsystem;

/**
 * Command to set the intake mechanism to its up state
 */
public class IntakeUp extends ReferencingCommand {

	/**
	 * Instantiate a new <code>IntakeUp</code>, taking control of the intake
	 * subsystem.
	 *
	 * @param intake intake subsystem
	 */
	public IntakeUp(IntakeSubsystem intake) {
		super(intake);
		requires(intake);
	}

	@Override
	protected void initialize() {
		System.out.println("IntakeUp init");
	}

	@Override
	protected void execute() {
		((IntakeSubsystem) subsystem).setSolenoidForward();
	}

	@Override
	protected boolean isFinished() {
		return true;
	}

	@Override
	protected void end() {
		System.out.println("IntakeUp end");
	}

	@Override
	protected void interrupted() {
		System.out.println("IntakeUp interupted");
	}
}
