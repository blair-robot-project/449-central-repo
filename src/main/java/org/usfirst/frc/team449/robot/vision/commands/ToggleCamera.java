package org.usfirst.frc.team449.robot.vision.commands;

import com.ni.vision.NIVision;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.oi.OISubsystem;
import org.usfirst.frc.team449.robot.vision.VisionMap;
import org.usfirst.frc.team449.robot.vision.VisionSubsystem;


/**
 * <code>Command</code> to toggle the camera feed
 */
public class ToggleCamera extends ReferencingCommand {

	private OISubsystem oi;

	/**
	 * Instantiate a new <code>ToggleCamera</code>.
	 *
	 * @param vision vision subsystem
	 * @param oi     oi subsystem
	 */
	public ToggleCamera(VisionSubsystem vision, OISubsystem oi) {
		super(vision);
		this.oi = oi;
		requires(vision);
	}

	@Override
	protected void initialize() {
		System.out.println("ToggleCamera init");

		// Stop current camera
		NIVision.IMAQdxStopAcquisition(((VisionSubsystem) subsystem).sessions[((VisionSubsystem) subsystem)
				.sessionPtr]);

		// Get new session pointer
		((VisionSubsystem) subsystem).sessionPtr = ((VisionSubsystem) subsystem).sessionPtr < VisionMap.CAMERA_NAMES
				.length - 1
				? ((VisionSubsystem) subsystem).sessionPtr + 1 : 0;

		// Start capture from new session
		NIVision.IMAQdxConfigureGrab(((VisionSubsystem) subsystem).sessions[((VisionSubsystem) subsystem).sessionPtr]);
		NIVision.IMAQdxStartAcquisition(((VisionSubsystem) subsystem).sessions[((VisionSubsystem) subsystem)
				.sessionPtr]);
		oi.toggleCamera();
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
		System.out.println("ToggleCamera end");
	}

	@Override
	protected void interrupted() {
		System.out.println("ToggleCamera interupted");
	}
}
