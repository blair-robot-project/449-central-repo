package org.usfirst.frc.team449.robot;

import edu.wpi.first.wpilibj.command.Subsystem;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * The base of all subsystems linked to maps. Holds a <code>RobotMap</code> that will be set
 * by subclasses to their own map
 */
public abstract class MappedSubsystem extends Subsystem {
	public final RobotMap map;

	/**
	 * Creates a mapped subsystem and sets its map
	 *
	 * @param map the map of constants relevant to this subsystem
	 */
	public MappedSubsystem(RobotMap map) {
		this.map = map;
	}

	/**
	 * This creates a JSONObject from a .json file referenced by the given
	 * path.
	 *
	 * @param path the path to the <code>.json</code> from which to create the
	 *             JSONObject
	 * @return the JSONObject creted from the given file, or null if there was
	 * an IOException
	 */
	public static JSONObject readConfig(String path) {
		File cfg = new File(path);
		if (!cfg.exists()) {
			throw new RuntimeException("Configuration file does not exist!");
		}
		JSONObject json = null;
		try {
			json = new JSONObject(new String(Files.readAllBytes(cfg.toPath()), StandardCharsets.UTF_8));
		} catch (IOException e) {
			e.printStackTrace(); // if this happens, we're fucked
		}
		return json;
	}
}
