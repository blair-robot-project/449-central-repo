package org.usfirst.frc.team449.robot.vision;

import org.usfirst.frc.team449.robot.RobotMap;

public class VisionMap extends RobotMap {


	/**
	 * Names of the cameras (from the roborio web interface)
	 */
	public static String[] CAMERA_NAMES = {"cam0", "cam3", "cam4"};

	public VisionMap(maps.org.usfirst.frc.team449.robot.vision.VisionMap.Vision message) {
		super(message);
		//CAMERA_NAMES = (String[]) message.getCameraNamesList().toArray();
	}
}
