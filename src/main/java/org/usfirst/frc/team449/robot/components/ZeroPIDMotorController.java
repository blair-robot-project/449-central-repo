package org.usfirst.frc.team449.robot.components;

import org.usfirst.frc.team449.robot.ReferencingCommand;

/**
 * Created by BlairRobot on 2016-08-23.
 */
public class ZeroPIDMotorController extends ReferencingCommand {
    public ZeroPIDMotorController(PIDMotorController pidMotorController) {
        super(pidMotorController);
    }

    @Override
    protected void execute() {
        System.out.println("Zeroing PID motor controller");
        ((PIDMotorController) subsystem).reset();
    }
}
