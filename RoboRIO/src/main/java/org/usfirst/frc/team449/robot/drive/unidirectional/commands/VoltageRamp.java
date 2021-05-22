package org.usfirst.frc.team449.robot.drive.unidirectional.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectional;
import org.usfirst.frc.team449.robot.other.Clock;

/** A command to ramp up the motors to full power at a given voltage rate. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class VoltageRamp<T extends Subsystem & DriveUnidirectional> extends CommandBase {

  /** The subsystem to execute this command on. */
  @NotNull private final T subsystem;

  /** The number of percentage points to increase motor output by per millisecond. */
  private final double percentPerMillis;
  /** Whether to spin in place or drive straight. */
  private final boolean spin;
  /** The last time execute() was run. */
  private long lastTime;
  /** The output to give to the motors. */
  private double output;

  /**
   * Default constructor
   *
   * @param subsystem The subsystem to execute this command on
   * @param voltsPerSecond How many volts to increase the output by per second.
   * @param spin Whether to spin in place or drive straight. Defaults to false.
   */
  @JsonCreator
  public VoltageRamp(
      @NotNull @JsonProperty(required = true) T subsystem,
      @JsonProperty(required = true) double voltsPerSecond,
      boolean spin) {
    addRequirements(subsystem);
    this.subsystem = subsystem;
    this.percentPerMillis = voltsPerSecond / 12. / 1000.;
    this.spin = spin;
  }

  /** Reset the output */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "VoltageRamp init.", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("VoltageRamp init.", this.getClass());
    lastTime = Clock.currentTimeMillis();
    output = 0;
  }

  /** Update the output based on how long it's been since execute() was last run. */
  @Override
  public void execute() {
    output += percentPerMillis * (Clock.currentTimeMillis() - lastTime);
    subsystem.setOutput(output, (spin ? -1 : 1) * output);
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
          "VoltageRamp Interrupted!", this.getClass().getSimpleName(), EventImportance.kNormal);
    }
    subsystem.setOutput(0, 0);
    Shuffleboard.addEventMarker(
        "VoltageRamp end.", this.getClass().getSimpleName(), EventImportance.kNormal);
  }
}
