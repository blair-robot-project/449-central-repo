package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.wpi.first.wpilibj.controller.PIDController;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.Nullable;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "@class")
public class MappedPIDController extends PIDController implements Loggable {

  private final String name;

  private double measurement;
  private double output;

  @JsonCreator
  public MappedPIDController(
      final double Kp, final double Ki, final double Kd, @Nullable String name) {
    super(Kp, Ki, Kd);

    if (name == null) {
      name = "PIDController";
    }
    this.name = name;
  }

  @Override
  @Log
  public double getVelocityError() {
    return super.getVelocityError();
  }

  @Override
  @Log
  public double getSetpoint() {
    return super.getSetpoint();
  }

  @Override
  public double calculate(final double measurement) {
    this.measurement = measurement;
    return output = super.calculate(measurement);
  }

  @Log
  public double getMeasurement() {
    return measurement;
  }

  @Log
  public double getOutput() {
    return output;
  }

  @Override
  public String configureLogName() {
    return name;
  }
}
