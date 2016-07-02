package org.usfirst.frc.team0449.robot.mechanism.intake.commands;


import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team0449.robot.ReferencingCommand;
import org.usfirst.frc.team0449.robot.mechanism.intake.IntakeSubsystem;

public class UpdateUS extends ReferencingCommand {
	/**
	 * Instantiate a new <code>UpdateUS</code>, taking control of the intake
	 * subsystem.
	 *
	 * @param intake intake subysystem
	 */
	public UpdateUS(IntakeSubsystem intake) {
		super(intake);
		requires(intake);
	}

	@Override
	protected void initialize() {
		System.out.println("UpdateUs init");
	}

	@Override
	protected void execute() {
		((IntakeSubsystem) subsystem).updateVals();
		SmartDashboard.putNumber("left", ((IntakeSubsystem) subsystem).getValLeft());
		SmartDashboard.putNumber("right", ((IntakeSubsystem) subsystem).getValRight());
		SmartDashboard.putNumber("angle", ((IntakeSubsystem) subsystem).getAngle());
		((IntakeSubsystem) subsystem).findBall(); // for debugging ir
	}

	@Override
	protected boolean isFinished() {
		return false;
	}

	@Override
	protected void end() {
		System.out.println("UpdateUs end; this Command should run for the"
				+ " duration of a robot run, so you (probably) done goofed");
	}

	@Override
	protected void interrupted() {
		System.out.println("UpdateUs interrupted");
	}

}
