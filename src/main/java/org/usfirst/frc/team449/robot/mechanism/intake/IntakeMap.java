package org.usfirst.frc.team449.robot.mechanism.intake;

import edu.wpi.first.wpilibj.AnalogInput;
import org.json.JSONObject;
import org.usfirst.frc.team449.robot.mechanism.MechanismMap;

/**
 * a map of constants needed for any form of Drive or its subclasses, and not
 * defined higher in the hierarchy
 */
public class IntakeMap extends MechanismMap {
	public Motor motor;
	public DoubleSolenoid solenoid;
	public IRSensor leftIR;
	public IRSensor rightIR;
	public double OUTPUT_SPEED;
	public double INPUT_SPEED;
	public Ultrasonic leftUltrasonic;

	public Ultrasonic rightUltrasonic;

	/**
	 * creates a new Intake Map based on the configuration in the given json any
	 * maps in here are to be shared across all intake subsystems
	 *
	 * @param json a JSONObject containing the configuration for the maps in this
	 *             object
	 */
	public IntakeMap(JSONObject json) {
		super(json);
	}

	public static class IRSensor extends MapObject {
		public int PORT;

		/**
		 * Minimum value at which IntakeIn should stop
		 */
		public double LOWER_BOUND;
		/**
		 * Maximum value at which IntakeIn should stop
		 */
		public double UPPER_BOUND;
		/**
		 * number of bits for oversampling as defined by
		 * {@link AnalogInput#setOversampleBits(int)}
		 * Should probably be the same as {@link #AVERAGE_BITS}
		 */
		public int OVERSAMPLING_BITS;
		/**
		 * number of bits for averaging as defined by
		 * {@link AnalogInput#setAverageBits(int) }
		 * Should probably be the same as {@link #OVERSAMPLING_BITS}
		 */
		public int AVERAGE_BITS;

		public IRSensor(JSONObject json, String objPath, Class enclosing) {
			super(json, objPath, enclosing);
		}

	}

	public static class Ultrasonic extends MapObject {
		public int PORT;

		public Ultrasonic(JSONObject json, String objPath, Class enclosing) {
			super(json, objPath, enclosing);
		}
	}
}
