package org.usfirst.frc.team449.robot.drive.tank;

import org.json.JSONObject;
import org.usfirst.frc.team449.robot.components.maps.AnglePIDMap;
import org.usfirst.frc.team449.robot.components.maps.ClusterPIDMap;
import org.usfirst.frc.team449.robot.drive.DriveMap;

/**
 * a map of constants needed for any form of TankDrive or its subclasses, and
 * not defined higher in the hierarchy
 */
public class TankDriveMap extends DriveMap {
	/**
	 * the map for the left cluster of the tank drive
	 */
	public ClusterPIDMap leftCluster;
	/**
	 * the map for the right cluster of the tank drive
	 */
	public ClusterPIDMap rightCluster;
	/**
	 * the map for angle controller of the tank drive
	 */
	public AnglePIDMap anglePID;
	public AnglePIDMap driveStraightAnglePID;
	/**
	 * the radius of the drive
	 */
	public double RADIUS;

	/**
	 * creates a new TankDrive Map based on the configuration in the given json
	 * any maps in here are to be shared across all tank drive subsystems
	 *
	 * @param json a JSONObject containing the configuration for the maps in this
	 *             object
	 */
	public TankDriveMap(JSONObject json) {
		super(json);
	}
}
