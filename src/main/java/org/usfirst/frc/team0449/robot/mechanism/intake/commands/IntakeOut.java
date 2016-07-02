package org.usfirst.frc.team0449.robot.mechanism.intake.commands;

import org.usfirst.frc.team0449.robot.ReferencingCommand;
import org.usfirst.frc.team0449.robot.mechanism.intake.IntakeMap;
import org.usfirst.frc.team0449.robot.mechanism.intake.IntakeSubsystem;

/**
 * A command to make push the ball out from the intake
 */
public class IntakeOut extends ReferencingCommand {
	/**
	 * Instantiate a new <code>IntakeOut</code>, taking control of the intake
	 * subsystem.
	 *
	 * @param intake intake subsystem
	 */
	public IntakeOut(IntakeSubsystem intake) {
		super(intake);
		requires(intake);
	}

	@Override
	protected void initialize() {
		System.out.println("IntakeOut init");
	}

	@Override
	protected void execute() {
		((IntakeSubsystem) subsystem).setMotorSpeed(((IntakeMap) (subsystem.map)).OUTPUT_SPEED);
	}

	@Override
	protected boolean isFinished() {
		return false;
	}

	@Override
	protected void end() {
		System.out.println("IntakeOut end");
	}

	@Override
	protected void interrupted() {
		((IntakeSubsystem) subsystem).setMotorSpeed(0);
		System.out.println("IntakeOut interrupted");
	}
}
