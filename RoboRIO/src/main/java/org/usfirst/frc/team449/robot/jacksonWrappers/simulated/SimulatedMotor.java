package org.usfirst.frc.team449.robot.jacksonWrappers.simulated;

import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.DoubleSupplier;

/** Units are in rotations. */
public class SimulatedMotor implements Loggable {
  /** (V) Nominal bus voltage; used to calculate maximum speed. */
  public static final double NOMINAL_VOLTAGE = 12;
  /** (Kg * m^2) Moment of moving parts. */
  private static final double MOMENT = 20;
  /** (Ohms) Used to calculate output current. */
  private static final double RESISTANCE = 1;
  /** (N*m / V) Torque per volt due to force of motor. */
  private static final double TORQUE_COEFF = 450000;
  /** (N*m / (R/s)) Torque per RPS due to motor internal friction. */
  private static final double FRICTION_COEFF = -10;
  /**
   * The maximum speed that the simulation will by nature allow the motor to sustain. Not really
   * used for anything.
   */
  @Log(name = "maxSpeed")
  private static final double TRUE_MAX_SPEED = TORQUE_COEFF * NOMINAL_VOLTAGE / -FRICTION_COEFF;

  private static final double EPSILON = 0.0001;

  private final double moment;
  private final double torqueCoeff;
  private final double frictionCoeff;

  @NotNull
  @Log(methodName = "getAsDouble")
  private final DoubleSupplier voltageSource;

  /** (R/s) Signed rotations per second */
  private double velocity;
  /** Absolute rotation value */
  private double position;

  public SimulatedMotor(
      @Nullable final DoubleSupplier voltageSource,
      @Nullable final Double moment,
      @Nullable final Double torqueCoeff,
      @Nullable final Double frictionCoeff) {
    this.moment = Objects.requireNonNullElse(moment, MOMENT);
    this.voltageSource = Objects.requireNonNullElse(voltageSource, () -> NOMINAL_VOLTAGE);
    this.torqueCoeff = Objects.requireNonNullElse(torqueCoeff, TORQUE_COEFF);
    this.frictionCoeff = Objects.requireNonNullElse(frictionCoeff, FRICTION_COEFF);
  }

  public SimulatedMotor(@NotNull final DoubleSupplier voltageSource) {
    this(voltageSource, null, null, null);
  }

  public SimulatedMotor() {
    this(null, null, null, null);
  }

  public void updatePhysics(final double deltaSecs) {
    final double motorTorque = this.torqueCoeff * this.voltageSource.getAsDouble();
    final double frictionTorque = this.frictionCoeff * this.velocity;
    final double netTorque = motorTorque + frictionTorque;
    final double angularAcceleration = netTorque / this.moment;

    this.velocity += angularAcceleration * deltaSecs;
    this.position += this.velocity * deltaSecs;

    if (Math.abs(this.velocity) < EPSILON) this.velocity = 0;
    if (Math.abs(this.position) < EPSILON) this.position = 0;
  }

  @Log
  public double getVelocity() {
    return this.velocity;
  }

  @Log
  public double getPosition() {
    return this.position;
  }

  public void resetPosition() {
    this.position = 0;
  }

  @Log
  public double getCurrent() {
    return this.voltageSource.getAsDouble() / RESISTANCE;
  }
}
