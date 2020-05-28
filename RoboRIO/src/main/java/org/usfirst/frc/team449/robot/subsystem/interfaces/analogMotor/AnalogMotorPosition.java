package org.usfirst.frc.team449.robot.subsystem.interfaces.analogMotor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.oblarg.oblog.Loggable;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedTalon;

/** An analogMotor that uses position instead of velocity. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class AnalogMotorPosition extends SubsystemBase implements SubsystemAnalogMotor, Loggable {

  /** The motor this subsystem controls. */
  @NotNull private final MappedTalon motor;

  /**
   * The constants that are added to and multiplied by a [-1, 1] setpoint to turn it into the
   * desired range of distances in feet.
   */
  private final double addToSP, multiplyBySP;

  /**
   * Default constructor.
   *
   * @param motor The motor this subsystem controls.
   * @param minPos The lowest position, in feet, this subsystem should go to. Defaults to 0.
   * @param maxPos The greatest position, in feet, this subsystem should go to.
   */
  @JsonCreator
  public AnalogMotorPosition(
      @NotNull @JsonProperty(required = true) MappedTalon motor,
      double minPos,
      @JsonProperty(required = true) double maxPos) {
    this.motor = motor;
    this.addToSP = (maxPos + minPos) / 2.;
    this.multiplyBySP = Math.abs((maxPos - minPos) / 2.);
  }

  /**
   * Set output to a given input.
   *
   * @param input The input to give to the motor.
   */
  @Override
  public void set(double input) {
    motor.setPositionSetpoint(addToSP + input * multiplyBySP);
  }

  /** Disable the motor. */
  @Override
  public void disable() {
    motor.disable();
  }
}
