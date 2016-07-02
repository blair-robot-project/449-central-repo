package org.usfirst.frc.team0449.robot.mechanism.breach;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.usfirst.frc.team0449.robot.RobotMap;
import org.usfirst.frc.team0449.robot.mechanism.MechanismSubsystem;

/**
 * This is the subsystem for the defense breaching arm. It extends
 * <code>MechanismSubsystem</code>
 *
 * @see MechanismSubsystem
 */
public class BreachSubsystem extends MechanismSubsystem {
	/**
	 * The double solenoid for the back piston
	 */
	private DoubleSolenoid backSolenoid;

	/**
	 * The double solenoid for the front piston
	 */
	private DoubleSolenoid frontSolenoid;

	/**
	 * Instantiate a new <code>BreachSubsystem</code>
	 *
	 * @param map configuration map
	 */
	public BreachSubsystem(RobotMap map) {
		super(map);
		System.out.println("Drive init started");
		if (!(map instanceof BreachMap)) {
			System.err.println("Breach has a map of class " + map.getClass().getSimpleName() + " and not BreachMap");
		}

		BreachMap breachMap = (BreachMap) map;
		backSolenoid = new DoubleSolenoid(breachMap.back.forward, breachMap.back.reverse);
		frontSolenoid = new DoubleSolenoid(breachMap.front.forward, breachMap.front.reverse);
	}

	/**
	 * Sets both solenoids to a specified state for each (true for forward,
	 * false for reverse).
	 *
	 * @param backSolForward  state to set the back double solenoid to (<code>true</code>
	 *                        for forward and <code>false</code> for reverse)
	 * @param frontSolForward state to set the front double solenoid to (<code>true</code>
	 *                        for forward and <code>false</code> for reverse)
	 */
	public void setSolenoid(boolean backSolForward, boolean frontSolForward) {
		if (backSolForward) {
			backSolenoid.set(DoubleSolenoid.Value.kForward);
		} else {
			backSolenoid.set(DoubleSolenoid.Value.kReverse);
		}
		if (frontSolForward) {
			frontSolenoid.set(DoubleSolenoid.Value.kForward);
		} else {
			frontSolenoid.set(DoubleSolenoid.Value.kReverse);
		}
	}

	@Override
	protected void initDefaultCommand() {
	}
}
