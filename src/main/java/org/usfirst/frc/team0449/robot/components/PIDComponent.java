package org.usfirst.frc.team0449.robot.components;

import edu.wpi.first.wpilibj.PIDController.Tolerance;
import edu.wpi.first.wpilibj.command.PIDSubsystem;

/**
 * A class designed for PID subsystems not linked to commands and that are parts
 * of more complex subsystems Should be the super class for all PID based
 * classes in components packages
 */
public abstract class PIDComponent extends PIDSubsystem {
	private Tolerance tol;
	private double lower;
	private double upper;

	/**
	 * Used internally for when Tolerance hasn't been set.
	 */
	public class NullTolerance implements Tolerance {
		@Override
		public boolean onTarget() {
			throw new RuntimeException("No tolerance value set when calling onTarget().");
		}
	}

	public class PercentageTolerance implements Tolerance {
		double percentage;

		PercentageTolerance(double value) {
			percentage = value;
		}

		@Override
		public boolean onTarget() {
			return Math.abs(getSetpoint() - returnPIDInput()) < Math.abs(percentage / 100 * (upper - lower));
		}
	}

	public class AbsoluteTolerance implements Tolerance {
		double value;

		AbsoluteTolerance(double value) {
			this.value = value;
		}

		@Override
		public boolean onTarget() {
			return Math.abs(getSetpoint() - returnPIDInput()) < Math.abs(value);
		}
	}

	public PIDComponent(String name, double p, double i, double d) {
		super(name, p, i, d);
		this.tol = new NullTolerance();
	}

	public PIDComponent(String name, double p, double i, double d, double f) {
		super(name, p, i, d, f);
		this.tol = new NullTolerance();
	}

	public PIDComponent(String name, double p, double i, double d, double f, double period) {
		super(name, p, i, d, f, period);
		this.tol = new NullTolerance();
	}

	public PIDComponent(double p, double i, double d) {
		super(p, i, d);
		this.tol = new NullTolerance();
	}

	public PIDComponent(double p, double i, double d, double period, double f) {
		super(p, i, d, period, f);
		this.tol = new NullTolerance();
	}

	public PIDComponent(double p, double i, double d, double period) {
		super(p, i, d, period);
		this.tol = new NullTolerance();
	}

	@Override
	public void setInputRange(double lower, double upper) {
		this.lower = lower;
		this.upper = upper;
		super.setInputRange(lower, upper);
	}

	@Override
	public void setAbsoluteTolerance(double tolerance) {
		this.tol = new AbsoluteTolerance(tolerance);
		super.setAbsoluteTolerance(tolerance);
	}

	@Override
	public void setPercentTolerance(double p) {
		this.tol = new PercentageTolerance(p);
		super.setPercentTolerance(p);
	}

	@Override
	public boolean onTarget() {
		return tol.onTarget() || super.onTarget();
	}

	@Override
	final protected void initDefaultCommand() {
	}
}
