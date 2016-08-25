package org.usfirst.frc.team449.robot.drive.tank.components;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SpeedController;
import org.usfirst.frc.team449.robot.components.PIDMotorController;

public abstract class PIDMotorClusterController extends PIDMotorController {
    private MotorCluster motorCluster;

    public PIDMotorClusterController(double p, double i, double d, double f, double period, double maxAbsoluteSetpoint,
                              boolean inverted, boolean useAbsolute, PIDSourceType pidSourceType) {
        super(p, i, d, f, period, maxAbsoluteSetpoint, inverted, useAbsolute, pidSourceType);
    }

    @Override
    public PIDOutput constructPIDOutputDevice() {
        motorCluster = new MotorCluster(getNumMotors());
        populateMotorCluster();
        return motorCluster;
    }

    public abstract int getNumMotors();
    public abstract void populateMotorCluster();

    public void addMotorClusterSlave(SpeedController motor) {
        motorCluster.addSlave(motor);
    }

    public double getMotorCLusterPIDOutput() {
        return motorCluster.getPIDOutput();
    }
}
