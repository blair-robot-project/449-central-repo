package org.usfirst.frc.team449.robot.javamaps.builders;

import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.doubleUnaryOperator.Polynomial;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedJoystick;
import org.usfirst.frc.team449.robot.oi.throttles.ThrottlePolynomial;

public class ThrottlePolynomialBuilder {

  private MappedJoystick stick;
  private int axis;
  private double deadband;
  private Double smoothingTimeSecs;
  private boolean inverted;
  private Polynomial polynomial;
  private Double scale;

  public ThrottlePolynomialBuilder stick(@NotNull MappedJoystick stick) {
    this.stick = stick;
    return this;
  }

  public ThrottlePolynomialBuilder axis(int axis) {
    this.axis = axis;
    return this;
  }

  public ThrottlePolynomialBuilder deadband(double deadband) {
    this.deadband = deadband;
    return this;
  }

  public ThrottlePolynomialBuilder smoothingTimeSecs(double smoothingTimeSecs) {
    this.smoothingTimeSecs = smoothingTimeSecs;
    return this;
  }

  public ThrottlePolynomialBuilder inverted(boolean inverted) {
    this.inverted = inverted;
    return this;
  }

  public ThrottlePolynomialBuilder polynomial(@NotNull Polynomial polynomial) {
    this.polynomial = polynomial;
    return this;
  }

  public ThrottlePolynomialBuilder scale(double scale) {
    this.scale = scale;
    return this;
  }

  public ThrottlePolynomial build() {
    assert stick != null : "Stick for ThrottlePolynomial must not be null";
    assert polynomial != null : "Polynomial for ThrottlePolynomial must not be null";
    return new ThrottlePolynomial(
        stick, axis, deadband, smoothingTimeSecs, inverted, polynomial, scale);
  }
}
