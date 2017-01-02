package org.usfirst.frc.team449.robot.mechanism.intake;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.VictorSP;
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

	private maps.org.usfirst.frc.team449.robot.mechanism.intake.IntakeMap.Intake intakeMap;

	/**
	 * Instantiate a new <code>IntakeSubsystem</code>
	 *
	 * @param map message used for reference to constants
	 */
	public IntakeSubsystem(maps.org.usfirst.frc.team449.robot.mechanism.intake.IntakeMap.Intake map) {
		super(map.getMechanism());
		System.out.println("Intake init started");
		intakeMap = map;

		this.mainMotor = new VictorSP(map.getMotor().getPort());
		this.mainMotor.setInverted(map.getMotor().getInverted());

		solenoid = new DoubleSolenoid(map.getSolenoid().getForward(), map.getSolenoid().getReverse());
		this.leftIR = new AnalogInput(map.getLeftIR().getPort());
		this.leftIR.setAverageBits(map.getLeftIR().getAverageBits());
		this.leftIR.setOversampleBits(map.getLeftIR().getOversamplingBits());
		this.rightIR = new AnalogInput(map.getRightIR().getPort());
		this.rightIR.setAverageBits(map.getRightIR().getAverageBits());
		this.rightIR.setOversampleBits(map.getRightIR().getOversamplingBits());

		leftChannel = new AnalogInput(map.getLeftUltrasonic().getPort());
		rightChannel = new AnalogInput(map.getRightIR().getPort());

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
		boolean found = (intakeMap.getRightIR().getLowerBound() < right && right < intakeMap.getRightIR().getUpperBound())
				|| (intakeMap.getLeftIR().getLowerBound() < left && left < intakeMap.getLeftIR().getUpperBound());
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