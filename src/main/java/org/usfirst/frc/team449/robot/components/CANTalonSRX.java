package org.usfirst.frc.team449.robot.components;

import edu.wpi.first.wpilibj.CANTalon;

/**
 * Component wrapper on CTRE CAN Talon SRX {@link CANTalon}
 */
public class CANTalonSRX extends Component {

	/**
	 * The CTRE CAN Talon SRX that this class is a wrapper on
	 */
	public CANTalon canTalon;

	/**
	 * kP of the internal PID loop
	 */
	protected double kP;
	/**
	 * kI of the internal PID loop
	 */
	protected double kI;
	/**
	 * kD of the internal PID loop
	 */
	protected double kD;
	/**
	 * kF of the interal PID loop
	 */
	protected double kF;

	/**
	 * Construct the CANTalonSRX from its map object
	 *
	 * @param m CANTalonSRX map object
	 */
	public CANTalonSRX(maps.org.usfirst.frc.team449.robot.components.CANTalonSRXMap.CANTalonSRX m) {
		// Configure stuff
		canTalon = new CANTalon(m.getPort());
		canTalon.setFeedbackDevice(CANTalon.FeedbackDevice.valueOf(m.getFeedbackDevice().getNumber()));
		canTalon.reverseSensor(m.getReverseSensor());
		canTalon.reverseOutput(m.getReverseOutput());
		canTalon.setInverted(m.getIsInverted());
		canTalon.configNominalOutputVoltage
				(+m.getNominalOutVoltage(), -m.getNominalOutVoltage());
		canTalon.configPeakOutputVoltage(+m.getPeakOutVoltage(),
				-m.getPeakOutVoltage());
		canTalon.setProfile(m.getProfile());

		/*
		 * Read the PIDF constants from the map, then call setPIDF to scale the stuff in the map as desired to get to
		 * native units, appropriates or whatever the hell the controller expect, then set the PIDF slots of the
		 * hardware and choose the slot
		 */
		kP = m.getKP();
		kI = m.getKI();
		kD = m.getKD();
		kF = m.getKF();
		setPIDF(m.getKP(), m.getKI(), m.getKD(), m.getKF());
		canTalon.setPID(kP, kI, kD, kF, 0, 0, 0);
		canTalon.setProfile(0);

		// Configure more stuff
		canTalon.ConfigFwdLimitSwitchNormallyOpen(m.getFwdLimNormOpen());
		canTalon.ConfigRevLimitSwitchNormallyOpen(m.getRevLimNormOpen());
		canTalon.enableLimitSwitch(m.getFwdLimEnabled(), m.getRevLimEnabled());
		canTalon.enableForwardSoftLimit(m.getFwdSoftLimEnabled());
		canTalon.setForwardSoftLimit(m.getFwdSoftLimVal());
		canTalon.enableReverseSoftLimit(m.getRevSoftLimEnabled());
		canTalon.setReverseSoftLimit(m.getRevSoftLimVal());
		canTalon.enableBrakeMode(m.getBrakeMode());
	}

	/**
	 * Method called in the constructor that sets the true PID values before they are handed to the Talon's internal
	 * PID controller.
	 * <p>
	 * When called in the constructor, this method takes the map's PIDF values as its arguments, scales them, and
	 * sticks
	 * them in the kP, kI, kD, kF fields. Later in the constructor, the Talon is given those fields as the final PIDF.
	 * <p>
	 * By default, the map PIDF values are untouched before they go into the Talon. However, you may want to specify
	 * a scaling factor between the map PIDF and the PIDF that is fed to the Talon. When you are PIDF tuning, you want
	 * to think of changing PIDF as fractions of a full response. However, robots have intrinsic scaling factors
	 * between
	 * the input units and the measured units and PIDF values, so this method allows you to specify external scaling
	 * factors so that you can have your fractional PIDF in one place on the map and your scaling in another place in
	 * the map.
	 * <p>
	 * To scale PIDF between the map and the Talon, override this method and change what the PIDF fields are assigned
	 * to.
	 * <p>
	 * Note that true PIDF have already been assigned for the first time in the constructor. If you do not want to
	 * scale
	 * anything, you do not need to fill out this method, and if you only want to scale one value, you can write in
	 * just
	 * that one value when you override this.
	 *
	 * @param mkP map kP
	 * @param mkI map kI
	 * @param mkD map kD
	 * @param mkF map KF
	 */
	protected void setPIDF(double mkP, double mkI, double mkD, double mkF) {
	}

	/**
	 * Give a PercentVbus setpoint (set to PercentVbus mode and set)
	 *
	 * @param percentVbus percent of total voltage (between -1.0 and +1.0)
	 */
	public void setPercentVbus(double percentVbus) {
		if (Math.abs(percentVbus) > 1.0) {
			System.out.println("WARNING: YOU ARE CLIPPING MAX PERCENT VBUS AT " + percentVbus);
			percentVbus = Math.signum(percentVbus);
		}
		canTalon.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		canTalon.set(percentVbus);
	}

	/**
	 * Give a position closed loop setpoint
	 * TODO: figure out units and warning clip to input range
	 *
	 * @param positionSp position setpoint
	 */
	public void setPosition(double positionSp) {
		canTalon.changeControlMode(CANTalon.TalonControlMode.Position);
		canTalon.set(positionSp);
	}

	/**
	 * Give a velocity closed loop setpoint in RPS
	 * <p>
	 * Note: This method is called setSpeed since the TalonControlMode enum is called speed. However, the input
	 * argument is signed and is actually a velocity.
	 *
	 * @param velocitySp velocity setpoint in revolutions per second
	 */
	public void setSpeed(double velocitySp) {
		canTalon.changeControlMode(CANTalon.TalonControlMode.Speed);
		canTalon.set(velocitySp * 60); // 60 converts from RPS to RPM, TODO figure out where the 60 should actually go
	}

	@Override
	public boolean getInverted() {
		return false;
	}

	@Override
	public void setInverted(boolean b) {
	}
}
