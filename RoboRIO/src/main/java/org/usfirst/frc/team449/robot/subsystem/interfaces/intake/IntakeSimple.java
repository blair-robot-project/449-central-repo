package org.usfirst.frc.team449.robot.subsystem.interfaces.intake;

import static org.usfirst.frc.team449.robot.other.Util.getLogPrefix;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.oblarg.oblog.Loggable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.simpleMotor.SimpleMotor;

/** A simple intake subsystem. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class IntakeSimple extends SubsystemBase implements SubsystemIntake, Loggable {

  /** The motor this subsystem controls. */
  @NotNull private final SimpleMotor motor;

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
   * @param motor The motor this subsystem controls.
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
  public IntakeSimple(
      @JsonProperty(required = true) @NotNull final SimpleMotor motor,
      @Nullable final Double inSlowVel,
      @Nullable final Double inFastVel,
      @Nullable final Double outSlowVel,
      @Nullable final Double outFastVel) {
    this.motor = motor;
    this.inSlowVel = inSlowVel;
    this.inFastVel = inFastVel;
    this.outSlowVel = outSlowVel;
    this.outFastVel = outFastVel;
    this.mode = IntakeMode.OFF;

    if (inSlowVel == null && inFastVel == null && outSlowVel == null && outFastVel == null) {
      System.err.println(
          getLogPrefix(this) + "Warning: without any defined velocities; motor will never spin.");
    }
  }

  /** @return the current mode of the intake. */
  @NotNull
  @Override
  public SubsystemIntake.IntakeMode getMode() {
    return this.mode;
  }

  /** @param mode The mode to switch the intake to. */
  @Override
  public void setMode(@NotNull final SubsystemIntake.IntakeMode mode) {
    switch (mode) {
      case OFF:
        this.motor.setVelocity(0);
        this.motor.disable();
        this.mode = IntakeMode.OFF;
        break;
      case IN_FAST:
        if (this.inFastVel != null) {
          this.motor.enable();
          this.motor.setVelocity(this.inFastVel);
          this.mode = IntakeMode.IN_FAST;
        }
        break;
      case IN_SLOW:
        if (this.inSlowVel != null) {
          this.motor.enable();
          this.motor.setVelocity(this.inSlowVel);
          this.mode = IntakeMode.IN_SLOW;
        }
        break;
      case OUT_FAST:
        if (this.outFastVel != null) {
          this.motor.enable();
          this.motor.setVelocity(this.outFastVel);
          this.mode = IntakeMode.OUT_FAST;
        }
        break;
      case OUT_SLOW:
        if (this.outSlowVel != null) {
          this.motor.enable();
          this.motor.setVelocity(this.outSlowVel);
          this.mode = IntakeMode.OUT_SLOW;
        }
        break;
    }
  }
}
