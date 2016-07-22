package org.usfirst.frc.team449.robot.vision;

import org.json.JSONObject;
import org.usfirst.frc.team449.robot.RobotMap;

public class VisionMap extends RobotMap {

	public VisionMap(JSONObject json) {super(json);}

	/**
	 * Names of the cameras (from the roborio web interface)
	 */
	public static String[] CAMERA_NAMES = {"cam0", "cam3", "cam4"};
}
