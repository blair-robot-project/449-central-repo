package org.usfirst.frc.team449.robot.drive.tank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.VictorSP;
import maps.org.usfirst.frc.team449.robot.components.MotorMap;
import org.usfirst.frc.team449.robot.components.PIDMotorClusterController;
import org.usfirst.frc.team449.robot.components.PIDOutputGetter;
import org.usfirst.frc.team449.robot.drive.DriveSubsystem;
import org.usfirst.frc.team449.robot.drive.tank.commands.DefaultDrive;
import org.usfirst.frc.team449.robot.drive.tank.components.PIDAngleController;
import org.usfirst.frc.team449.robot.oi.OISubsystem;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * a Drive subsystem that operates with a tank drive
 */
public class TankDriveSubsystem extends DriveSubsystem {
	private PIDMotorClusterController rightClusterController;
	private PIDMotorClusterController leftClusterController;

	private PIDOutputGetter leftVelCorrector;
	private PIDOutputGetter rightVelCorrector;

	private PIDAngleController angleController;
	private PIDAngleController driveStraightAngleController;

	private AHRS gyro;

	private OISubsystem oi;

	private long startTime;

	public TankDriveSubsystem(maps.org.usfirst.frc.team449.robot.drive.tank.TankDriveMap.TankDrive map, OISubsystem
			oi) {
		super(map.getDrive());
		this.oi = oi;
		System.out.println("TankDrive init started");

		rightClusterController = new PIDMotorClusterController(map.getRightCluster().getVelocityPID().getPID().getP(),
				map.getRightCluster().getVelocityPID().getPID().getI(), map.getRightCluster().getVelocityPID().getPID
				().getD(),
				0, 0.05, 130.0, true, false, PIDSourceType.kRate) {
			@Override
			public int getNumMotors() {
				return map.getRightCluster().getCluster().getMotorCount();
			}

			@Override
			public boolean getOutputDeviceInverted() {
				return true;
			}

			@Override
			public void populateMotorCluster() {
				VictorSP motor; // declare before loop to save garbage collection time
				for (MotorMap.Motor m : map.getRightCluster().getCluster().getMotorList()) {
					motor = new VictorSP(m.getPort());
					motor.setInverted(false);   // motors should not be inverted, motor cluster should be
					addMotorClusterSlave(motor);
				}
			}

			@Override
			public PIDSource constructPIDSourceDevice() {
				Encoder enc = new Encoder(map.getRightCluster().getEncoder().getPortA(),
						map.getRightCluster().getEncoder().getPortB());
				enc.setDistancePerPulse(map.getRightCluster().getEncoder().getDistancePerPulse());
				return enc;
			}
		};

		leftClusterController = new PIDMotorClusterController(map.getLeftCluster().getVelocityPID().getPID().getP(),
				map.getLeftCluster().getVelocityPID().getPID().getI(), map.getLeftCluster().getVelocityPID().getPID()
				.getD(),
				0, 0.05, 130.0, false, false, PIDSourceType.kRate) {
			@Override
			public int getNumMotors() {
				return map.getLeftCluster().getCluster().getMotorCount();
			}

			@Override
			public boolean getOutputDeviceInverted() {
				return true;
			}

			@Override
			public void populateMotorCluster() {
				VictorSP motor; // declare before loop to save garbage collection time
				for (MotorMap.Motor m : map.getLeftCluster().getCluster().getMotorList()) {
					motor = new VictorSP(m.getPort());
					motor.setInverted(false);   // motors should not be inverted, motor cluster should be
					addMotorClusterSlave(motor);
				}
			}

			@Override
			public PIDSource constructPIDSourceDevice() {
				Encoder enc = new Encoder(map.getLeftCluster().getEncoder().getPortA(),
						map.getLeftCluster().getEncoder().getPortB());
				enc.setDistancePerPulse(map.getLeftCluster().getEncoder().getDistancePerPulse());
				return enc;
			}
		};

		gyro = new AHRS(SPI.Port.kMXP);

		angleController = new PIDAngleController(map.getAnglePID().getPID().getP(), map.getAnglePID().getPID().getI(),
				map.getAnglePID().getPID().getD(), leftClusterController, rightClusterController, gyro);
		angleController.setAbsoluteTolerance(map.getAnglePID().getAbsoluteTolerance());
		angleController.setMinimumOutput(map.getAnglePID().getMinimumOutput());
		angleController.setMinimumOutputEnabled(map.getAnglePID().getMinimumOutputEnabled());
		leftVelCorrector = new PIDOutputGetter();
		rightVelCorrector = new PIDOutputGetter();
		driveStraightAngleController = new PIDAngleController(map.getDriveStraightAnglePID().getPID().getP(),
				map.getDriveStraightAnglePID().getPID().getI(), map.getDriveStraightAnglePID().getPID().getD(),
				leftVelCorrector, rightVelCorrector, gyro);
		driveStraightAngleController.setAbsoluteTolerance(map.getDriveStraightAnglePID().getAbsoluteTolerance());
		driveStraightAngleController.setMinimumOutput(map.getDriveStraightAnglePID().getMinimumOutput());
		driveStraightAngleController.setMinimumOutputEnabled(map.getDriveStraightAnglePID().getMinimumOutputEnabled());

		startTime = new Date().getTime();

		setThrottle(0, 0);
		System.out.println("TankDrive init finished");
	}

	public void zeroGyro() {
		gyro.zeroYaw();
	}

	public void disableAngleController() {
		angleController.disable();
		this.leftClusterController.set(0);
		this.rightClusterController.set(0);
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
		leftClusterController.setRelativeSetpoint(left);
		rightClusterController.setRelativeSetpoint(right);

		try (FileWriter fw = new FileWriter("/home/lvuser/driveLog.csv", true)) {
			StringBuilder sb = new StringBuilder();
			sb.append(new Date().getTime() - startTime);    // 1
			sb.append(",");
			sb.append(left);  // 2
			sb.append(",");
			sb.append(right); // 3
			sb.append(",");
			sb.append(leftClusterController.getPIDOutput()); // 4
			sb.append(",");
			sb.append(rightClusterController.getPIDOutput()); // 5
			sb.append(",");
			sb.append(leftClusterController.getSourceMeasuredValue()); // 6
			sb.append(",");
			sb.append(rightClusterController.getSourceMeasuredValue()); // 7
			sb.append("\n");
			fw.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public void encoderReset() {
		((Encoder) leftClusterController.pidSourceDevice).reset();
		((Encoder) rightClusterController.pidSourceDevice).reset();
	}

	public double getDistance() {
		return Math.abs(((Encoder) leftClusterController.pidSourceDevice).getDistance());
	}

	public void subsystemReset() {
		leftClusterController.reset();
		rightClusterController.reset();
		setThrottle(0, 0);
	}
}
