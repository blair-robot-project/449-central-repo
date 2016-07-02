package org.usfirst.frc.team0449.robot.drive.tank;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team0449.robot.OISubsystem;
import org.usfirst.frc.team0449.robot.RobotMap;
import org.usfirst.frc.team0449.robot.components.PIDOutputGetter;
import org.usfirst.frc.team0449.robot.components.PIDVelocityMotor;
import org.usfirst.frc.team0449.robot.drive.DriveSubsystem;
import org.usfirst.frc.team0449.robot.drive.tank.commands.DefaultDrive;
import org.usfirst.frc.team0449.robot.drive.tank.components.MotorCluster;
import org.usfirst.frc.team0449.robot.drive.tank.components.PIDAngleController;

/**
 * a Drive subsystem that operates with a tank drive
 */
public class TankDriveSubsystem extends DriveSubsystem {
	private PIDVelocityMotor rightClusterVelocity;
	private PIDVelocityMotor leftClusterVelocity;
	private MotorCluster rightCluster;
	private MotorCluster leftCluster;
	private Encoder rightEnc;
	private Encoder leftEnc;

	private PIDOutputGetter leftVelCorrector;
	private PIDOutputGetter rightVelCorrector;

	private PIDAngleController angleController;
	private PIDAngleController driveStraightAngleController;
	private AHRS gyro;
	private boolean pidEnabled;

	private OISubsystem oi;

	public TankDriveSubsystem(RobotMap map, OISubsystem oi) {
		super(map);
		this.oi = oi;
		System.out.println("Drive init started");
		if (!(map instanceof TankDriveMap)) {
			System.err.println(
					"TankDrive has a map of class " + map.getClass().getSimpleName() + " and not TankDriveMap");
		}

		TankDriveMap tankMap = (TankDriveMap) map;
		// initialize motor clusters and add slaves
		VictorSP motor;
		// left pid
		leftCluster = new MotorCluster(tankMap.leftCluster.cluster.motors.length);
		for (int i = 0; i < tankMap.leftCluster.cluster.motors.length; i++) {
			motor = new VictorSP(tankMap.leftCluster.cluster.motors[i].PORT);
			motor.setInverted(tankMap.leftCluster.cluster.motors[i].INVERTED);
			leftCluster.addSlave(motor);
		}
		leftCluster.setInverted(tankMap.leftCluster.cluster.INVERTED);
		leftEnc = new Encoder(tankMap.leftCluster.encoder.a, tankMap.leftCluster.encoder.b);
		leftEnc.setDistancePerPulse(tankMap.leftCluster.encoder.dpp);
		this.leftClusterVelocity = new PIDVelocityMotor(tankMap.leftCluster.p, tankMap.leftCluster.i,
				tankMap.leftCluster.d, leftCluster, leftEnc, "left");
		this.leftClusterVelocity.setOutputRange(-tankMap.leftCluster.outputRange, tankMap.leftCluster.outputRange);
		this.leftClusterVelocity.setSpeed(tankMap.leftCluster.speed);
		this.leftClusterVelocity.setPercentTolerance(tankMap.leftCluster.percentTolerance);
		this.leftClusterVelocity.setZeroTolerance(tankMap.leftCluster.zeroTolerance);
		this.leftClusterVelocity.setInverted(tankMap.leftCluster.inverted);
		this.leftClusterVelocity.setRampRate(tankMap.leftCluster.rampRate);
		this.leftClusterVelocity.setRampRateEnabled(tankMap.leftCluster.rampRateEnabled);

		// right pid
		rightCluster = new MotorCluster(tankMap.rightCluster.cluster.motors.length);
		for (int i = 0; i < tankMap.rightCluster.cluster.motors.length; i++) {
			motor = new VictorSP(tankMap.rightCluster.cluster.motors[i].PORT);
			motor.setInverted(tankMap.rightCluster.cluster.motors[i].INVERTED);
			rightCluster.addSlave(motor);
		}
		rightCluster.setInverted(tankMap.rightCluster.cluster.INVERTED);
		rightEnc = new Encoder(tankMap.rightCluster.encoder.a, tankMap.rightCluster.encoder.b);
		rightEnc.setDistancePerPulse(tankMap.rightCluster.encoder.dpp);
		this.rightClusterVelocity = new PIDVelocityMotor(tankMap.rightCluster.p, tankMap.rightCluster.i,
				tankMap.rightCluster.d, rightCluster, rightEnc, "right");
		this.rightClusterVelocity.setOutputRange(-tankMap.rightCluster.outputRange, tankMap.rightCluster.outputRange);
		this.rightClusterVelocity.setSpeed(tankMap.rightCluster.speed);
		this.rightClusterVelocity.setPercentTolerance(tankMap.rightCluster.percentTolerance);
		this.rightClusterVelocity.setZeroTolerance(tankMap.rightCluster.zeroTolerance);
		this.rightClusterVelocity.setInverted(tankMap.rightCluster.inverted);
		this.rightClusterVelocity.setRampRate(tankMap.rightCluster.rampRate);
		this.rightClusterVelocity.setRampRateEnabled(tankMap.rightCluster.rampRateEnabled);

		gyro = new AHRS(SPI.Port.kMXP);
		angleController = new PIDAngleController(tankMap.anglePID.p, tankMap.anglePID.i, tankMap.anglePID.d,
				leftClusterVelocity, rightClusterVelocity, gyro);
		angleController.setAbsoluteTolerance(tankMap.anglePID.absoluteTolerance);
		angleController.setMinimumOutput(tankMap.anglePID.minimumOutput);
		angleController.setMinimumOutputEnabled(tankMap.anglePID.minimumOutputEnabled);
		leftVelCorrector = new PIDOutputGetter();
		rightVelCorrector = new PIDOutputGetter();
		driveStraightAngleController = new PIDAngleController(tankMap.driveStraightAnglePID.p,
				tankMap.driveStraightAnglePID.i, tankMap.driveStraightAnglePID.d, leftVelCorrector, rightVelCorrector,
				gyro);
		driveStraightAngleController.setAbsoluteTolerance(tankMap.driveStraightAnglePID.absoluteTolerance);
		driveStraightAngleController.setMinimumOutput(tankMap.driveStraightAnglePID.minimumOutput);
		driveStraightAngleController.setMinimumOutputEnabled(tankMap.driveStraightAnglePID.minimumOutputEnabled);
		SmartDashboard.putData("pid drive straight", driveStraightAngleController);
		this.setPidEnabled(true);
	}

	public void zeroGyro() {
		gyro.zeroYaw();
	}

	public void disableAngleController() {
		angleController.disable();
		this.leftClusterVelocity.setSetpoint(0);
		this.rightClusterVelocity.setSetpoint(0);
	}

	public void enableAngleController() {
		angleController.enable();
	}

	/**
	 * @return pitch indicated by the gyro
	 */
	public double getPitch() {
		return gyro.getPitch();
	}

	public void enableDriveStraightCorrector() {
		driveStraightAngleController.enable();
		driveStraightAngleController.setSetpoint(gyro.pidGet());
	}

	public void disableDriveStraightCorrector() {
		driveStraightAngleController.disable();
	}

	/**
	 * sets the throttle for the left and right clusters as specified by the
	 * parameters
	 *
	 * @param left  the normalized speed between -1 and 1 for the left cluster
	 * @param right the normalized speed between -1 and 1 for the right cluster
	 */
	public void setThrottle(double left, double right) {
		SmartDashboard.putNumber("right js", right);
		SmartDashboard.putNumber("left js", left);
		SmartDashboard.putNumber("right enc", rightEnc.getRate());
		SmartDashboard.putNumber("left enc", leftEnc.getRate());
		SmartDashboard.putNumber("right corr", rightVelCorrector.get());
		SmartDashboard.putNumber("left corr", leftVelCorrector.get());
		left += leftVelCorrector.get() * ((TankDriveMap) map).leftCluster.speed;
		right += rightVelCorrector.get() * ((TankDriveMap) map).rightCluster.speed;
		if (pidEnabled) {
			this.leftClusterVelocity.setSetpoint(left);
			this.rightClusterVelocity.setSetpoint(right);
		} else {
			this.leftCluster.set(left);
			this.rightCluster.set(right);
		}
		SmartDashboard.putNumber("getangle", gyro.pidGet());
		SmartDashboard.putNumber("modded angle", gyro.pidGet());
	}

	/**
	 * sets the angle controller to go to theta
	 *
	 * @param theta the angle to turn in place to
	 */
	public void setTurnToAngle(double theta) {
		this.angleController.setSetpoint(theta);
	}

	/**
	 * get if the <code>AngleController</code> has reached the angle it is set
	 * to
	 *
	 * @return if the <code>AngleController</code> has reached the angle it is
	 * set to
	 */
	public boolean getTurnAngleDone() {
		return this.angleController.onTarget();
	}

	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(new DefaultDrive(this, oi));
	}

	public void reset() {
		this.leftEnc.reset();
		this.rightEnc.reset();
	}

	public double getDistance() {
		return Math.abs(leftEnc.getDistance());
	}

	/**
	 * switch whether or not the controls consider PID (in case of encoder
	 * failure)
	 */
	public void togglePID() {
		setPidEnabled(!this.pidEnabled);
	}

	private void setPidEnabled(boolean pidEnabled) {
		this.pidEnabled = pidEnabled;
		if (pidEnabled) {
			this.rightClusterVelocity.enable();
			this.leftClusterVelocity.enable();
		} else {
			this.rightClusterVelocity.disable();
			this.leftClusterVelocity.disable();
		}
		SmartDashboard.putBoolean("Drive PID", pidEnabled);
	}
}
