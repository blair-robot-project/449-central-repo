package org.usfirst.frc.team449.robot.subsystem.intake;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.oblarg.oblog.Loggable;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.motors.simple.SimpleMotor;
import org.usfirst.frc.team449.robot.other.InjectiveDependencyHelper;
import org.usfirst.frc.team449.robot.subsystem.analogMotor.SubsystemAnalogMotor;

import java.util.Map;

import static org.usfirst.frc.team449.robot.other.Util.getLogPrefix;

/** A simple intake subsystem that relies on a single motor to rotate some part of it. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class IntakeSimple extends SubsystemBase
    implements SubsystemIntake, SubsystemAnalogMotor, Loggable {

  /** The motor this subsystem controls. */
  @NotNull private final SimpleMotor motor;

  /**
   * The velocities for the motor to go at for each of the modes, on [-1, 1]. Can be null to
   * indicate that this intake doesn't have/use that mode.
   */
  @NotNull private final Map<IntakeMode, Double> velocities;

  /** The current mode. */
  @NotNull private SubsystemIntake.IntakeMode mode;

  /**
   * Default constructor
   *
   * @param motor The motor this subsystem controls.
   * @param velocities The velocity for the motor to go at for each {@link IntakeMode}, on the
   * interval [-1, 1]. Modes can be missing to indicate that this intake doesn't have/use them.
   */
  @JsonCreator
  public IntakeSimple(
      @NotNull @JsonProperty(required = true) final SimpleMotor motor,
      @NotNull @JsonProperty(required = true) final Map<IntakeMode, Double> velocities) {

    InjectiveDependencyHelper.assertInjective(this, motor);

    this.motor = motor;
    this.velocities = velocities;

    this.mode = IntakeMode.OFF;

    if (velocities.containsKey(IntakeMode.OFF))
      System.err.println(
          getLogPrefix(this)
              + "Warning: velocity for mode "
              + IntakeMode.OFF
              + " will be ignored.");

    if (velocities.isEmpty()) {
      System.err.println(
          getLogPrefix(this) + "Warning: no defined velocities; motor will never spin.");
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
    //  This guard means intake instances should not share motors.
    if (this.getMode() == mode) return;

    if (mode == IntakeMode.OFF) {
      this.mode = IntakeMode.OFF;
      motor.setVelocity(0);
      motor.disable();
    } else if (this.velocities.containsKey(mode)) {
      this.mode = mode;
      this.motor.enable();
      this.motor.setVelocity(this.velocities.get(mode));
    } else {
//      System.err.println(getLogPrefix(this) + "Warning: use of undefined mode " + mode);
//      Shuffleboard.addEventMarker(
//          "Undefined " + getLogPrefix(this) + "mode used",
//          "mode: " + mode,
//          EventImportance.kCritical);

      DriverStation.reportError("Mode not defined for instance: " + mode, false);
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
