package org.usfirst.frc.team449.robot.mechanism.intake;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.VictorSP;
import org.usfirst.frc.team449.robot.RobotMap;
import org.usfirst.frc.team449.robot.components.SmoothedValue;
import org.usfirst.frc.team449.robot.mechanism.MechanismSubsystem;
import org.usfirst.frc.team449.robot.mechanism.intake.commands.IntakeIn;
import org.usfirst.frc.team449.robot.mechanism.intake.commands.UpdateUS;

/**
 * This is the subsystem for the intake rollers and infrared sensor. It extends
 * <code>MechanismSubsystem</code>.
 *
 * @see MechanismSubsystem
 */
public class IntakeSubsystem extends MechanismSubsystem {
	/**
	 * The <code>SpeedController</code> driving the shaft that sucks in and
	 * spits out the balls.
	 */
	private SpeedController mainMotor;

	/**
	 * The <code>DoubleSolenoid</code> controlling the piston raising and
	 * lowering the intake subsystem.
	 */
	private DoubleSolenoid solenoid;

	/**
	 * The left ultrasonic rangefinding sensor's <code>AnalogInput<code> channel
	 */
	private AnalogInput leftChannel;

	/**
	 * The right ultrasonic rangefinding sensor's <code>AnalogInput
	 * <code> channel
	 */
	private AnalogInput rightChannel;

	/**
	 * The left ultrasonic rangefinder's <code>Value</code>
	 */
	private SmoothedValue leftVal;

	/**
	 * The right ultrasonic rangefinder's <code>Value</code>
	 */
	private SmoothedValue rightVal;

	private AnalogInput leftIR;

	private AnalogInput rightIR;

	private boolean ignoreIR;

	/**
	 * Instantiate a new <code>IntakeSubsystem</code>
	 *
	 * @param map {@link RobotMap} used for reference to constants
	 */
	public IntakeSubsystem(RobotMap map) {
		super(map);
		System.out.println("Intake init started");

		if (!(map instanceof IntakeMap)) {
			System.err.println("Intake has a map of class " + map.getClass().getSimpleName() + " and not IntakeMap");
		}

		IntakeMap intakeMap = (IntakeMap) map;

		this.mainMotor = new VictorSP(intakeMap.motor.PORT);
		this.mainMotor.setInverted(intakeMap.motor.INVERTED);

		solenoid = new DoubleSolenoid(intakeMap.solenoid.forward, intakeMap.solenoid.reverse);
		this.leftIR = new AnalogInput(intakeMap.leftIR.PORT);
		this.leftIR.setAverageBits(intakeMap.leftIR.AVERAGE_BITS);
		this.leftIR.setOversampleBits(intakeMap.leftIR.OVERSAMPLING_BITS);
		this.rightIR = new AnalogInput(intakeMap.rightIR.PORT);
		this.rightIR.setAverageBits(intakeMap.rightIR.AVERAGE_BITS);
		this.rightIR.setOversampleBits(intakeMap.rightIR.OVERSAMPLING_BITS);

		leftChannel = new AnalogInput(intakeMap.leftUltrasonic.PORT);
		rightChannel = new AnalogInput(intakeMap.rightUltrasonic.PORT);

		leftVal = new SmoothedValue(1);
		rightVal = new SmoothedValue(1);

		ignoreIR = false;

		// new IntakeDown(); // start in down position

		System.out.println("Intake init finished");
	}

	/**
	 * This sets the velocity of the motor of the intake shaft.
	 *
	 * @param speed the normalized speed of the motor (between -1 and 1)
	 */
	public void setMotorSpeed(double speed) {
		mainMotor.set(speed);
	}

	/**
	 * Sets the double solenoid to forward its forward state
	 */
	public void setSolenoidForward() {
		solenoid.set(DoubleSolenoid.Value.kForward);
	}

	/**
	 * Sets the double solenoid to forward its reverse state
	 */
	public void setSolenoidReverse() {
		solenoid.set(DoubleSolenoid.Value.kReverse);
	}

	// TODO add documentation, detail how this works (ask Eyob)
	public double getValLeft() {
		// CONVERT Analog Input value to distance in inches????
		return 0.0982 * leftChannel.getValue() + 2.2752;
	}

	// TODO add documentation, detail how this works (ask Eyob)
	public double getValRight() {
		return 0.0497 * rightChannel.getValue() - 0.2725;
	}

	/**
	 * Update the {@link SmoothedValue}s
	 */
	public void updateVals() {
		leftVal.set(leftChannel.getValue());
		rightVal.set(rightChannel.getValue());
	}

	// TODO add documentation, detail how this works (ask Eyob)
	public double getAngle() {
		double y = Math.abs(getValLeft() - getValRight());
		double x = 24; // 2 feet apart // TODO externalize this
		return Math.toDegrees(Math.atan2(y, x));
	}

	/**
	 * Checks whether the IR sensors sense that the ball is in the intake
	 * mechanism.
	 *
	 * @return if at least one IR is sensing the ball
	 */
	public boolean findBall() {
		double right = rightIR.getAverageVoltage();
		double left = leftIR.getAverageVoltage();
		IntakeMap intakeMap = (IntakeMap) map; // TODO move this to
		// constructor/initializer
		boolean found = (intakeMap.rightIR.LOWER_BOUND < right && right < intakeMap.rightIR.UPPER_BOUND)
				|| (intakeMap.leftIR.LOWER_BOUND < left && left < intakeMap.leftIR.UPPER_BOUND);
		return found;
	}

	/**
	 * Toggles whether the robot is going to ignore the IR values that it is
	 * receiving. If ignoreIR is <code>false</code>, the robot will only stop
	 * {@link IntakeIn IntakeIn} when the user presses the button that
	 * initialized the command again. If it is <code>true</code>, the robot will
	 * only stop <code>IntakeIn</code> when the IR detects the ball.
	 */
	public void toggleIgnoreIR() {
		ignoreIR = !ignoreIR;
	}

	/**
	 * Checks whether the robot is "ignoring IR". If ignoreIR is
	 * <code>false</code>, the robot will only stop {@link IntakeIn IntakeIn}
	 * when the user presses the button that initialized the command again. If
	 * it is <code>true</code>, the robot will stop when the IR detects the
	 * ball.
	 *
	 * @return whether the IR sensor is being ignored.
	 */
	public boolean isIgnoringIR() {
		return ignoreIR;
	}

	@Override
	public void initDefaultCommand() {
		setDefaultCommand(new UpdateUS(this));
	}
}