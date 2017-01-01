package org.usfirst.frc.team449.robot.mechanism.breach;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.usfirst.frc.team449.robot.mechanism.MechanismSubsystem;

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
	public BreachSubsystem(maps.org.usfirst.frc.team449.robot.mechanism.breach.BreachMap.Breach map) {
		super(map.getMechanism());
		System.out.println("Breach init started");

		backSolenoid = new DoubleSolenoid(map.getBack().getForward(), map.getBack().getReverse());
		frontSolenoid = new DoubleSolenoid(map.getFront().getForward(), map.getFront().getReverse());
		System.out.println("Breach init finished");
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