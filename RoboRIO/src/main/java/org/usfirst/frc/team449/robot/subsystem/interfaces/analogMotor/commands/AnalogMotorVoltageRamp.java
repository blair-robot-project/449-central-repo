package org.usfirst.frc.team449.robot.subsystem.interfaces.analogMotor.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Subsystem;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.other.Clock;
import org.usfirst.frc.team449.robot.subsystem.interfaces.analogMotor.SubsystemAnalogMotor;

/** Slowly increase the output to the motors in order to characterize the system. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class AnalogMotorVoltageRamp<T extends Subsystem & SubsystemAnalogMotor>
    extends CommandBase {

  /** The subsystem to execute this command on. */
  @NotNull @Log.Exclude private final T subsystem;

  /** The number of percentage points to increase motor output by per millisecond. */
  private final double percentPerMillis;

  /** The last time execute() was run. */
  private long lastTime;

  /** The output to give to the motors. */
  private double output;

  /**
   * Default constructor
   *
   * @param subsystem The subsystem to execute this command on
   * @param voltsPerSecond How many volts to increase the output by per second.
   * @param startingVoltage The voltage to start the ramp at. Defaults to 0.
   */
  @JsonCreator
  public AnalogMotorVoltageRamp(
      @NotNull @JsonProperty(required = true) T subsystem,
      @JsonProperty(required = true) double voltsPerSecond,
      double startingVoltage) {
    addRequirements(subsystem);
    this.subsystem = subsystem;
    this.percentPerMillis = voltsPerSecond / 12. / 1000.;
    this.output = startingVoltage;
  }

  /** Reset the output */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "AnalogMotorVoltageRamp init.", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("AnalogMotorVoltageRamp init.", this.getClass());
    lastTime = Clock.currentTimeMillis();
  }

  /** Update the output based on how long it's been since execute() was last run. */
  @Override
  public void execute() {
    output += percentPerMillis * (Clock.currentTimeMillis() - lastTime);
    subsystem.set(output);
    lastTime = Clock.currentTimeMillis();
  }

  /**
   * Exit if the output is greater than the motors can produce.
   *
   * @return true if the output is greater than or equal to 1, false otherwise.
   */
  @Override
  public boolean isFinished() {
    return output >= 1.;
  }

  /** Log and stop on end. */
  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "AnalogMotorVoltageRamp Interrupted!",
          this.getClass().getSimpleName(),
          EventImportance.kNormal);
    }
    subsystem.disable();
    Shuffleboard.addEventMarker(
        "AnalogMotorVoltageRamp end.", this.getClass().getSimpleName(), EventImportance.kNormal);
  }
}
