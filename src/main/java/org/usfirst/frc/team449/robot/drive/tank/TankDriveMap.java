package org.usfirst.frc.team449.robot.drive.tank;

import org.json.JSONObject;
import org.usfirst.frc.team449.robot.RobotMap;
import org.usfirst.frc.team449.robot.drive.DriveMap;

/**
 * a map of constants needed for any form of TankDrive or its subclasses, and
 * not defined higher in the hierarchy
 */
public class TankDriveMap extends DriveMap {
	/**
	 * the map for the left cluster of the tank drive
	 */
	public ClusterPID leftCluster;
	/**
	 * the map for the right cluster of the tank drive
	 */
	public ClusterPID rightCluster;
	/**
	 * the map for angle controller of the tank drive
	 */
	public AnglePID anglePID;
	public AnglePID driveStraightAnglePID;
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

	/**
	 * a map for a MotorCluster of variable size. the size of the Cluster is
	 * defined by the JSONObject
	 */
	public static class MotorCluster extends RobotMap.MapObject {
		/**
		 * an array of maps for the motors in this Cluster
		 */
		public RobotMap.Motor[] motors;
		/**
		 * whether the whole cluster should be inverted
		 */
		public boolean INVERTED;

		public MotorCluster(JSONObject json, String path, Class enclosing) {
			super(json, path, enclosing);
		}
	}

	/**
	 * a map for a PID controller that has a MotorCluster, and a single encoder
	 */
	public static class ClusterPID extends RobotMap.VelocityPID {
		/**
		 * the MotorCluster controlled by this PID controller
		 */
		public MotorCluster cluster;
		/**
		 * the Encoder used for control in this PID controller
		 */
		public RobotMap.Encoder encoder;

		public ClusterPID(JSONObject json, String path, Class enclosing) {
			super(json, path, enclosing);
		}
	}

	public static class AnglePID extends RobotMap.PID {
		public double absoluteTolerance;
		public double minimumOutput;
		public boolean minimumOutputEnabled;

		public AnglePID(JSONObject json, String path, Class enclosing) {
			super(json, path, enclosing);
		}
	}
}
