package org.usfirst.frc.team0449.robot.drive.tank.components;

import edu.wpi.first.wpilibj.SpeedController;
import org.usfirst.frc.team0449.robot.components.Component;

/**
 * A cluster of motors, or SpeedControllers, used for tank drive generally.
 */
public class MotorCluster extends Component implements SpeedController {
	private final SpeedController[] controllerList;
	private boolean inverted;

	private double lastSet = 0;

	/**
	 * construct a MotorCluster to hold a total of <code>total</code>
	 * SpeedControllers
	 *
	 * @param total the number of SpeedControllers to hold in this glorified array
	 */
	public MotorCluster(int total) {
		this.controllerList = new SpeedController[total];
		this.lastSet = 0;
	}

	/**
	 * construct a MotorCluster to control the given SpeedControllers
	 *
	 * @param controllers the SpeedControllers to control
	 */
	public MotorCluster(SpeedController[] controllers) {
		this.controllerList = controllers;
		this.lastSet = 0;
	}

	/**
	 * add a motor that will be considered a part of the cluster
	 *
	 * @param controller the motorController
	 */
	public void addSlave(SpeedController controller) {
		for (int i = 0; i < controllerList.length; i++) {
			if (controllerList[i] == null) {
				controllerList[i] = controller;
				return;
			}
		}
		System.err.println("Motor cluster over capacity, not adding a new motor! (" + controllerList.length + ")");
	}

	@Override
	public void pidWrite(double output) {
		for (int i = 0; i < this.controllerList.length; i++) {
			controllerList[i].pidWrite(output);
		}

		this.lastSet = output;
	}

	@Override
	public double get() {
		return this.lastSet;
	}

	@Override
	public void set(double speed, byte syncGroup) {
		System.out.println("This shit is deprecated and shouldn't be called.\n TL;DR: YOU DONE FUCKED UP");
	}

	@Override
	public void set(double speed) {

		for (int i = 0; i < this.controllerList.length; i++) {
			controllerList[i].set(speed);
		}

		this.lastSet = speed;
	}

	@Override
	public void setInverted(boolean b) {
		boolean changed = b != this.inverted;
		if (!changed) {
			return;
		}
		this.inverted = b;
		for (int i = 0; i < this.controllerList.length; i++) {
			controllerList[i].setInverted(!controllerList[i].getInverted());
		}
	}

	@Override
	public boolean getInverted() {
		return this.inverted;
	}

	@Override
	public void disable() {
		for (int i = 0; i < this.controllerList.length; i++) {
			controllerList[i].disable();
		}
	}

	@Override
	public void stopMotor() {
		for (int i = 0; i < this.controllerList.length; i++) {
			controllerList[i].stopMotor();
		}
	}
}
