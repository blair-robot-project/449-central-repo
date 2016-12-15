package org.usfirst.frc.team449.robot.mechanism.intake.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.intake.IntakeSubsystem;

/**
 * {@link Command} that lowers the intake mechanism on its two piston supports.
 */
public class IntakeDown extends ReferencingCommand {

	/**
	 * Instantiate a new <code>IntakeDown</code>, taking control of the intake
	 * subsystem.
	 *
	 * @param intake intake subsystem
	 */
	public IntakeDown(IntakeSubsystem intake) {
		super(intake);
		requires(intake);
	}

	@Override
	protected void initialize() {
		System.out.println("IntakeDown init");
	}

	@Override
	protected void execute() {
		((IntakeSubsystem) subsystem).setSolenoidReverse();
	}

	@Override
	protected boolean isFinished() {
		return true;
	}

	@Override
	protected void end() {
		System.out.println("IntakeDown end");
	}

	@Override
	protected void interrupted() {
		System.out.println("IntakeDown interupted");
	}
}