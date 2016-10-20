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
import org.usfirst.frc.team449.robot.components.PIDMotorClusterController;
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

    public TankDriveSubsystem(RobotMap map, OISubsystem oi) {
        super(map);
        this.oi = oi;
        System.out.println("TankDrive init started");
        if (!(map instanceof TankDriveMap)) {
            System.err.println(
                    "TankDrive has a map of class " + map.getClass().getSimpleName() + " and not TankDriveMap");
        }
        TankDriveMap tankMap = (TankDriveMap) map;

        rightClusterController = new PIDMotorClusterController(tankMap.rightCluster.p, tankMap.rightCluster.i,
                tankMap.rightCluster.d, tankMap.rightCluster.f, tankMap.rightCluster.controllerPeriod,
                tankMap.rightCluster.inputRange, tankMap.rightCluster.inverted, false, PIDSourceType.kRate) {
            @Override
            public int getNumMotors() {
                return tankMap.rightCluster.cluster.motors.length;
            }

            @Override
            public boolean getOutputDeviceInverted() {
                return tankMap.rightCluster.cluster.INVERTED;
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

        leftClusterController = new PIDMotorClusterController(tankMap.leftCluster.p, tankMap.leftCluster.i,
                tankMap.leftCluster.d, tankMap.leftCluster.f, tankMap.leftCluster.controllerPeriod,
                tankMap.leftCluster.inputRange, tankMap.leftCluster.inverted, false, PIDSourceType.kRate) {
            @Override
            public int getNumMotors() {
                return tankMap.leftCluster.cluster.motors.length;
            }

            @Override
            public boolean getOutputDeviceInverted() {
                return tankMap.leftCluster.cluster.INVERTED;
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
                leftClusterController, rightClusterController, gyro);
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
        SmartDashboard.putNumber("left throttle", left);
        SmartDashboard.putNumber("right throttle", right);
        SmartDashboard.putNumber("left setpoint", leftClusterController.getAbsoluteSetpoint());
        SmartDashboard.putNumber("right setpoint", rightClusterController.getAbsoluteSetpoint());
        SmartDashboard.putNumber("left error",
 leftClusterController.getPIDError());
        SmartDashboard.putNumber("right error", rightClusterController.getPIDError());
        SmartDashboard.putNumber("left enc", leftClusterController.getPIDOutput());
        SmartDashboard.putNumber("right enc", leftClusterController.getPIDOutput());
//        SmartDashboard.putNumber("left correction", leftVelCorrector.get());
//        SmartDashboard.putNumber("right correction", rightVelCorrector.get());
        SmartDashboard.putNumber("getangle", gyro.pidGet());

//        left += leftVelCorrector.get() * ((TankDriveMap) map).leftCluster.speed;
//        right += rightVelCorrector.get() * ((TankDriveMap) map).rightCluster.speed;

        leftClusterController.setRelativeSetpoint(left);
        rightClusterController.setRelativeSetpoint(right);
//        leftClusterController.noPIDWrite(left);
//        rightClusterController.noPIDWrite(right);

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
