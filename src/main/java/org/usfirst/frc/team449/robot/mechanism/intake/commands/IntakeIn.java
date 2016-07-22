package org.usfirst.frc.team449.robot.mechanism.intake.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.intake.IntakeMap;
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
		SmartDashboard.putBoolean("IntakeIn", true);
		System.out.println("IntakeIn init");
	}

	@Override
	protected void execute() {
		SmartDashboard.putBoolean("IntakeIn", true);
		((IntakeSubsystem) subsystem).setMotorSpeed(((IntakeMap) (subsystem.map)).INPUT_SPEED);
	}

	@Override
	protected boolean isFinished() {
		if (((IntakeSubsystem) subsystem).isIgnoringIR())
			return false;
		else
			return ((IntakeSubsystem) subsystem).findBall();
	}

	@Override
	protected void end() {
		SmartDashboard.putBoolean("IntakeIn", false);
		((IntakeSubsystem) subsystem).setMotorSpeed(0);
		System.out.println("IntakeIn end");
	}

	@Override
	protected void interrupted() {
		SmartDashboard.putBoolean("IntakeIn", false);
		((IntakeSubsystem) subsystem).setMotorSpeed(0);
		System.out.println("ItakeIn interrupted");
	}
}
