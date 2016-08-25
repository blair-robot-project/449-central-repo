package org.usfirst.frc.team449.robot.drive.tank;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team449.robot.RobotMap;
import org.usfirst.frc.team449.robot.components.PIDOutputGetter;
import org.usfirst.frc.team449.robot.drive.DriveSubsystem;
import org.usfirst.frc.team449.robot.drive.tank.commands.DefaultDrive;
import org.usfirst.frc.team449.robot.drive.tank.components.PIDAngleController;
import org.usfirst.frc.team449.robot.drive.tank.components.PIDMotorClusterController;
import org.usfirst.frc.team449.robot.oi.OISubsystem;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * a Drive subsystem that operates with a tank drive
 */
public class TankDriveSubsystem extends DriveSubsystem {
    private PIDOutputGetter leftVelCorrector;
    private PIDOutputGetter rightVelCorrector;

    private PIDAngleController angleController;
    private PIDAngleController driveStraightAngleController;
    private AHRS gyro;

    private PIDMotorClusterController rc;
    private PIDMotorClusterController lc;

    private OISubsystem oi;

    private long startTime;

    public TankDriveSubsystem(RobotMap map, OISubsystem oi) {
        super(map);
        this.oi = oi;
        System.out.println("TankDrive init started");
        if (!(map instanceof TankDriveMap)) {
            System.err.println(
                    "TankDrive has a map of class " + map.getClass().getSimpleName() + " and not TankDriveMap");
        }
        TankDriveMap tankMap = (TankDriveMap) map;

        rc = new PIDMotorClusterController(tankMap.rightCluster.p, tankMap.rightCluster.i, tankMap.rightCluster.d,
                0, 0.05, 130.0, false, false, PIDSourceType.kRate) {
            @Override
            public int getNumMotors() {
                return tankMap.rightCluster.cluster.motors.length;
            }

            @Override
            public void populateMotorCluster() {
                VictorSP motor; // declare before loop to save garbage collection time
                for (int i = 0; i < tankMap.rightCluster.cluster.motors.length; i++) {
                    motor = new VictorSP(tankMap.rightCluster.cluster.motors[i].PORT);
                    motor.setInverted(tankMap.rightCluster.cluster.motors[i].INVERTED);
                    addMotorClusterSlave(motor);
                }
            }

            @Override
            public PIDSource constructPIDSourceDevice() {
                Encoder enc = new Encoder(tankMap.rightCluster.encoder.a, tankMap.rightCluster.encoder.b);
                enc.setDistancePerPulse(tankMap.rightCluster.encoder.dpp);
                return enc;
            }
        };

        lc = new PIDMotorClusterController(tankMap.leftCluster.p, tankMap.leftCluster.i, tankMap.leftCluster.d,
                0, 0.05, 130.0, false, false, PIDSourceType.kRate) {
            @Override
            public int getNumMotors() {
                return tankMap.leftCluster.cluster.motors.length;
            }

            @Override
            public void populateMotorCluster() {
                VictorSP motor; // declare before loop to save garbage collection time
                for (int i = 0; i < tankMap.leftCluster.cluster.motors.length; i++) {
                    motor = new VictorSP(tankMap.leftCluster.cluster.motors[i].PORT);
                    motor.setInverted(tankMap.leftCluster.cluster.motors[i].INVERTED);
                    addMotorClusterSlave(motor);
                }
            }

            @Override
            public PIDSource constructPIDSourceDevice() {
                Encoder enc = new Encoder(tankMap.leftCluster.encoder.a, tankMap.leftCluster.encoder.b);
                enc.setDistancePerPulse(tankMap.leftCluster.encoder.dpp);
                return enc;
            }
        };

        gyro = new AHRS(SPI.Port.kMXP);

        angleController = new PIDAngleController(tankMap.anglePID.p, tankMap.anglePID.i, tankMap.anglePID.d,
                lc, rc, gyro);
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

        startTime = new Date().getTime();
        System.out.println("TankDrive init finished");
    }

    public void zeroGyro() {
        gyro.zeroYaw();
    }

    public void disableAngleController() {
        angleController.disable();
        this.lc.set(0);
        this.rc.set(0);
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
        SmartDashboard.putNumber("left throttle", left);
        SmartDashboard.putNumber("right throttle", right);
        SmartDashboard.putNumber("right setpoint", rc.getAbsoluteSetpoint());
        SmartDashboard.putNumber("left setpoint", lc.getAbsoluteSetpoint());
        SmartDashboard.putNumber("right enc", lc.getMotorCLusterPIDOutput());
        SmartDashboard.putNumber("left enc", lc.getMotorCLusterPIDOutput());
        SmartDashboard.putNumber("right correction", rightVelCorrector.get());
        SmartDashboard.putNumber("left correction", leftVelCorrector.get());
        SmartDashboard.putNumber("getangle", gyro.pidGet());

        left += leftVelCorrector.get() * ((TankDriveMap) map).leftCluster.speed;
        right += rightVelCorrector.get() * ((TankDriveMap) map).rightCluster.speed;

        lc.setRelativeSetpoint(left);
        rc.setRelativeSetpoint(right);

        try (FileWriter fw = new FileWriter("/home/lvuser/driveLog.csv", true)) {
            StringBuilder sb = new StringBuilder();
            sb.append(new Date().getTime() - startTime);    // 1
            sb.append(",");
            sb.append(left * ((TankDriveMap) map).leftCluster.inputRange);  // 2
            sb.append(",");
            sb.append(right * ((TankDriveMap) map).rightCluster.inputRange); // 3
            sb.append(",");
            sb.append(lc.getMotorCLusterPIDOutput() * ((TankDriveMap) map).leftCluster.inputRange); // 4
            sb.append(",");
            sb.append(rc.getMotorCLusterPIDOutput() * ((TankDriveMap) map).rightCluster.inputRange); // 5
            sb.append(",");
            sb.append(lc.getSourceMeasuredValue()); // 6
            sb.append(",");
            sb.append(rc.getSourceMeasuredValue()); // 7
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
        ((Encoder) lc.pidSourceDevice).reset();
        ((Encoder) rc.pidSourceDevice).reset();
    }

    public double getDistance() {
        return Math.abs(((Encoder) lc.pidSourceDevice).getDistance());
    }

    public void subsystemReset() {
        lc.reset();
        rc.reset();
    }
}
