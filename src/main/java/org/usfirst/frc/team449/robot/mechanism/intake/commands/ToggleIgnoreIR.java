package org.usfirst.frc.team449.robot.mechanism.intake.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.intake.IntakeSubsystem;

/**
 * "Toggles ignoreIR" If ignoreIR is false, the robot will only stop
 * {@link IntakeIn IntakeIn} when the user presses the button that initialized
 * the command again. If it's true, the command will stop when the IR detects
 * the ball.
 */
public class ToggleIgnoreIR extends ReferencingCommand {

	/**
	 * Instantiate a new <code>ToggleIgnoreIr</code>, taking control of the
	 * intake subsystem.
	 *
	 * @param intake intake subsystem
	 */
	public ToggleIgnoreIR(IntakeSubsystem intake) {
		super(intake);
		requires(intake);
	}

	@Override
	protected void initialize() {
		((IntakeSubsystem) subsystem).toggleIgnoreIR();
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
