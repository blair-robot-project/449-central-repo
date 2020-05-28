package org.usfirst.frc.team449.robot.commands.multiInterface;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.subsystem.interfaces.position.SubsystemPosition;
import org.usfirst.frc.team449.robot.subsystem.interfaces.solenoid.SubsystemSolenoid;

/** Go to a given position, then actuate a piston, e.g. for an elevator with a pneumatic brake. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MoveThenActuate<T extends Subsystem & SubsystemPosition & SubsystemSolenoid>
    extends CommandBase {

  /** The subsystem to execute this command on. */
  @NotNull private final T subsystem;
  /** The value to set the piston to once the subsystem reaches its destination. */
  private final DoubleSolenoid.Value setPistonTo;
  /** The position to go to, in feet. */
  private double setpoint;

  /**
   * Default constructor
   *
   * @param subsystem The subsystem to execute this command on.
   * @param setpoint The position to go to, in feet.
   * @param setPistonTo The value to set the piston to once the subsystem reaches its destination.
   */
  @JsonCreator
  public MoveThenActuate(
      @NotNull @JsonProperty(required = true) T subsystem,
      @JsonProperty(required = true) double setpoint,
      @JsonProperty(required = true) DoubleSolenoid.Value setPistonTo) {
    addRequirements(subsystem);
    this.subsystem = subsystem;
    this.setpoint = setpoint;
    this.setPistonTo = setPistonTo;
  }

  /** Log and set setpoint when this command is initialized */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "GoToPose init.", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("GoToPose init.", this.getClass());
    subsystem.setPositionSetpoint(setpoint);
  }

  /** Does nothing, don't want to spam position setpoints. */
  @Override
  public void execute() {
    // Do nothing
  }

  /**
   * Exit when the setpoint has been reached
   *
   * @return true if the setpoint is reached, false otherwise.
   */
  @Override
  public boolean isFinished() {
    return subsystem.onTarget();
  }

  /** Log and actuate when this command ends */
  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "GoToPose interrupted!", this.getClass().getSimpleName(), EventImportance.kNormal);
    }
    subsystem.setSolenoid(setPistonTo);
    subsystem.disableMotor();
    Shuffleboard.addEventMarker(
        "GoToPose end.", this.getClass().getSimpleName(), EventImportance.kNormal);
  }
}
