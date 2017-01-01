package org.usfirst.frc.team449.robot.drive.tank.components;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SpeedController;
import org.usfirst.frc.team449.robot.components.PIDComponent;

/**
 * a PID controller to control a tank drive's wheels' velocities through PID via
 * the PIDSubsystem in order to turn to a specific angle.
 */
public class PIDAngleController extends PIDComponent {

	private SpeedController leftMotor;
	private SpeedController rightMotor;
	private AHRS gyro;
	private double minimumOutput;
	private boolean minimumOutputEnabled;

	public PIDAngleController(double p, double i, double d, SpeedController leftMotor, SpeedController rightMotor,
	                          AHRS gyro) {
		super(p, i, d);
		this.getPIDController().setContinuous(true);
		this.setInputRange(-180, 180);
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.gyro = gyro;
		this.minimumOutput = 0;
		this.minimumOutputEnabled = false;
	}

	public void setMinimumOutput(double minimumOutput) {
		this.minimumOutput = minimumOutput;
	}

	public void setMinimumOutputEnabled(boolean minimumOutputEnabled) {
		this.minimumOutputEnabled = minimumOutputEnabled;
	}

	/**
	 * used by the PIDSubsystem to calculate the output wanted for the setpoint
	 * in this class, this returns the attached gyro's angle via pidGet()
	 *
	 * @return the angle of the gyro as per the gyro's pidGet() method (between
	 * -180 and 180 degrees)
	 * @see AHRS#pidGet()
	 */
	@Override
	protected double returnPIDInput() {
		return gyro.pidGet();
		// return Robot.oi.getDebugAngle();
	}

	/**
	 * Uses the output decided by the PIDSubsystem This output is the normalized
	 * voltage to the motors, effectively directly proportional to the
	 * derivative of the wheels' position. This drives the right motor to the
	 * opposite of the left motor.
	 *
	 * @param output the output decided by the <code>PIDSubsystem</code>
	 */

	@Override
	protected void usePIDOutput(double output) {
		if (minimumOutputEnabled) {
			if (output > 0 && output < minimumOutput) {
				output = minimumOutput;
			} else if (output < 0 && output > -minimumOutput) {
				output = -minimumOutput;
			}
		}
		this.leftMotor.pidWrite(-output);
		this.rightMotor.pidWrite(output);
	}

	@Override
	public void disable() {
		this.leftMotor.stopMotor();
		this.rightMotor.stopMotor();
		super.disable();
	}
}
