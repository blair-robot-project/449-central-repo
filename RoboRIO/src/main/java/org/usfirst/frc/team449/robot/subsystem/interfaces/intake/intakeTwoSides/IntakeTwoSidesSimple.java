package org.usfirst.frc.team449.robot.subsystem.interfaces.intake.intakeTwoSides;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.simpleMotor.SimpleMotor;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.SubsystemIntake;

/** A simple two-sided intake subsystem. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class IntakeTwoSidesSimple extends SubsystemBase
    implements SubsystemIntakeTwoSides, Loggable {

  /** The motors this subsystem controls. */
  @NotNull private final SimpleMotor leftMotor, rightMotor;

  /**
   * The velocities for the motor to go at for each of the modes, on [-1, 1]. Can be null to
   * indicate that this intake doesn't have/use that mode.
   */
  @Nullable private final Double inSlowVel;

  @Nullable private final Double inFastVel;
  @Nullable private final Double outSlowVel;
  @Nullable private final Double outFastVel;

  /** The current mode. */
  @NotNull private SubsystemIntake.IntakeMode mode;

  /**
   * Default constructor
   *
   * @param leftMotor The left motor that this subsystem controls.
   * @param rightMotor The left motor that this subsystem controls.
   * @param inSlowVel The velocity for the motor to go at for the IN_SLOW {@link IntakeMode}, on
   *     [-1, 1]. Can be null to indicate that this intake doesn't have/use IN_SLOW.
   * @param inFastVel The velocity for the motor to go at for the IN_FAST {@link IntakeMode}, on
   *     [-1, 1]. Can be null to indicate that this intake doesn't have/use IN_FAST.
   * @param outSlowVel The velocity for the motor to go at for the OUT_SLOW {@link IntakeMode}, on
   *     [-1, 1]. Can be null to indicate that this intake doesn't have/use OUT_SLOW.
   * @param outFastVel The velocity for the motor to go at for the OUT_FAST {@link IntakeMode}, on
   *     [-1, 1]. Can be null to indicate that this intake doesn't have/use OUT_FAST.
   */
  @JsonCreator
  public IntakeTwoSidesSimple(
      @JsonProperty(required = true) @NotNull final SimpleMotor leftMotor,
      @JsonProperty(required = true) @NotNull final SimpleMotor rightMotor,
      @Nullable final Double inSlowVel,
      @Nullable final Double inFastVel,
      @Nullable final Double outSlowVel,
      @Nullable final Double outFastVel) {
    this.leftMotor = leftMotor;
    this.rightMotor = rightMotor;
    this.inSlowVel = inSlowVel;
    this.inFastVel = inFastVel;
    this.outSlowVel = outSlowVel;
    this.outFastVel = outFastVel;
    this.mode = IntakeMode.OFF;
  }

  /** @return the current mode of the intake. */
  @NotNull
  @Override
  @Log.ToString
  public SubsystemIntake.IntakeMode getMode() {
    return this.mode;
  }

  /** @param mode The mode to switch the left side of the intake to. */
  @Override
  public void setLeftMode(@NotNull final SubsystemIntake.IntakeMode mode) {
    this.setMode(mode, this.leftMotor);
  }

  /** @param mode The mode to switch the right side of the intake to. */
  @Override
  public void setRightMode(@NotNull final SubsystemIntake.IntakeMode mode) {
    this.setMode(mode, this.rightMotor);
  }

  /**
   * @param mode
   * @param motor
   */
  private void setMode(@NotNull final SubsystemIntake.IntakeMode mode, @NotNull final SimpleMotor motor) {
    switch (mode) {
      case OFF:
        motor.setVelocity(0);
        motor.disable();
        this.mode = IntakeMode.OFF;
        break;
      case IN_FAST:
        if (this.inFastVel != null) {
          motor.enable();
          motor.setVelocity(this.inFastVel);
          this.mode = IntakeMode.IN_FAST;
        }
        break;
      case IN_SLOW:
        if (this.inSlowVel != null) {
          motor.enable();
          motor.setVelocity(this.inSlowVel);
          this.mode = IntakeMode.IN_SLOW;
        }
        break;
      case OUT_FAST:
        if (this.outFastVel != null) {
          motor.enable();
          motor.setVelocity(this.outFastVel);
          this.mode = IntakeMode.OUT_FAST;
        }
        break;
      case OUT_SLOW:
        if (this.outSlowVel != null) {
          motor.enable();
          motor.setVelocity(this.outSlowVel);
          this.mode = IntakeMode.OUT_SLOW;
        }
        break;
    }
  }
}
