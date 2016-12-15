package org.usfirst.frc.team449.robot.vision.commands;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.vision.VisionSubsystem;

/**
 * {@link Command} to display the camera feed. If the cameras fail, the robot
 * will not crash.
 */
public class DefaultVision extends ReferencingCommand {

	private boolean cameraFailed;

	/**
	 * Instantiate an instance of <code>ToggleCamera</code>.
	 *
	 * @param vision vision subsystem
	 */
	public DefaultVision(VisionSubsystem vision) {
		super(vision);
		requires(vision);
		cameraFailed = false;
	}

	@Override
	protected void initialize() {
		// CameraServer.getInstance().setQuality(10);
		System.out.println("ToggleCamera init");
	}

	@Override
	protected void execute() {
		try {
			CameraServer.getInstance().setImage(((VisionSubsystem) subsystem).getFrame());
		} catch (Exception e) {
			if (!cameraFailed) {
				System.out.println(
						"(DefaultVision execute) Cameras done goofed, but everything else is (maybe) functional.");
				cameraFailed = true;
			}
		}
	}

	@Override
	protected boolean isFinished() {
		return false;
	}

	@Override
	protected void end() {
		System.out.println("ToggleCamera end");
	}

	@Override
	protected void interrupted() {
		System.out.println("ToggleCamera interupted");
	}
}
