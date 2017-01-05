package org.usfirst.frc.team449.robot.mechanism.intake.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.intake.IntakeSubsystem;

/**
 * {@link Command} that activates the intake mechanism to shoot a ball out.
 */
public class IntakeIn extends ReferencingCommand {
	/**
	 * Instantiate a new <code>IntakeIn</code>, taking control of the intake
	 * subsystem.
	 *
	 * @param intake intake subsystem
	 */
	public IntakeIn(IntakeSubsystem intake) {
		super(intake);
		requires(intake);
	}

	@Override
	protected void initialize() {
		System.out.println("IntakeIn init");
	}

	@Override
	protected void execute() {
		((IntakeSubsystem) subsystem).setMotorSpeed(((maps.org.usfirst.frc.team449.robot.mechanism.intake.IntakeMap
				.Intake)
				(subsystem.map)).getInputSpeed());
	}

	@Override
	protected boolean isFinished() {
		return !((IntakeSubsystem) subsystem).isIgnoringIR() && ((IntakeSubsystem) subsystem).findBall();
	}

	@Override
	protected void end() {
		((IntakeSubsystem) subsystem).setMotorSpeed(0);
		System.out.println("IntakeIn end");
	}

	@Override
	protected void interrupted() {
		((IntakeSubsystem) subsystem).setMotorSpeed(0);
		System.out.println("ItakeIn interrupted");
	}
}
