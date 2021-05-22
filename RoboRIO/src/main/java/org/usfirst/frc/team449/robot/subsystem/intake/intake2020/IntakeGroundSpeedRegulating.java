package org.usfirst.frc.team449.robot.subsystem.intake.intake2020;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectional;
import org.usfirst.frc.team449.robot.generalInterfaces.motors.smart.SmartMotor;
import org.usfirst.frc.team449.robot.other.Util;
import org.usfirst.frc.team449.robot.subsystem.analogMotor.SubsystemAnalogMotor;
import org.usfirst.frc.team449.robot.subsystem.intake.SubsystemIntake;

import java.util.Map;

/**
 * a An intake that consists of rollers that are regulated to a consistent velocity relative to the
 * ground.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class IntakeGroundSpeedRegulating extends SubsystemBase
    implements SubsystemIntake, SubsystemAnalogMotor, Loggable {
  @NotNull private final DriveUnidirectional drive;
  @NotNull private final SmartMotor motor;
  private final double minVelFraction;
  @NotNull private final Map<IntakeMode, Double> targetVelocities;
  @Log.ToString @NotNull private SubsystemIntake.IntakeMode mode = IntakeMode.OFF;

  /**
   * Default constructor
   *
   * @param drive The drive subsystem from which to obtain ground speed information.
   * @param motor The motor this subsystem controls.
   * @param velocities The velocity for the motor to go at for each {@link IntakeMode}, on the
   *     interval [-1, 1]. Modes can be missing to indicate that this intake doesn't have/use them.
   * @param minVelFraction The minimum fraction of the target speed for the current mode to run at
   *     when slowing down due to robot movement.
   *     <p>Sign is not ignored. Zero prevents the intake from reversing, and negative values allow
   *     it to reverse if the robot attains a higher speed than the target speed.
   */
  @JsonCreator
  public IntakeGroundSpeedRegulating(
      @NotNull @JsonProperty(required = true) final DriveUnidirectional drive,
      @NotNull @JsonProperty(required = true) final SmartMotor motor,
      @NotNull @JsonProperty(required = true) final Map<IntakeMode, Double> velocities,
      final double minVelFraction) {
    this.drive = drive;
    this.motor = motor;
    this.minVelFraction = minVelFraction;

    this.targetVelocities = velocities;
  }

  @Override
  public @NotNull SubsystemIntake.IntakeMode getMode() {
    return this.mode;
  }

  @Override
  public void setMode(@NotNull final IntakeMode mode) {
    if (this.targetVelocities.get(mode) != null) this.mode = mode;
  }

  @Override
  public void periodic() {
    final Double targetVelocity = this.targetVelocities.get(this.mode);

    if (targetVelocity != null) {
      if (this.drive.getLeftVelCached() == null || this.drive.getRightVelCached() == null) {
        throw new UnsupportedOperationException(
            Util.getLogPrefix(this) + "Can't use drive without encoders.");
      }

      // TODO Currently one drive master's output is always reversed, so we subtract the velocities.
      final double driveVelocity =
          (this.drive.getLeftVelCached() - this.drive.getRightVelCached()) * 0.5;
      final double adjustedIntakeVelocity = targetVelocity - driveVelocity;

      final double minVelocity = targetVelocity * this.minVelFraction;
      final double finalVelocity = Math.max(minVelocity, adjustedIntakeVelocity);

      // TODO Fix the units
      this.motor.setVelocityUPS(finalVelocity);
    }
  }

  /**
   * Set output to a given input.
   *
   * @param input The input to give to the motor.
   */
  @Override
  public void set(final double input) {
    this.motor.setVelocity(input);
  }

  /** Disable the motor. */
  @Override
  public void disable() {
    this.motor.disable();
  }
}
