package org.usfirst.frc.team449.robot.commands.multiInterface;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.subsystem.interfaces.binaryMotor.SubsystemBinaryMotor;
import org.usfirst.frc.team449.robot.subsystem.interfaces.conditional.SubsystemConditional;

/** Run a BinaryMotor while a condition is true. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class RunMotorWhileConditonMet<
        T extends Subsystem & SubsystemBinaryMotor & SubsystemConditional>
    extends CommandBase {

  /** The subsystem to execute this command on */
  @NotNull private final T subsystem;

  /**
   * Default constructor
   *
   * @param subsystem The subsystem to execute this command on.
   */
  @JsonCreator
  public RunMotorWhileConditonMet(@NotNull @JsonProperty(required = true) T subsystem) {
    addRequirements(subsystem);
    this.subsystem = subsystem;
  }

  /** Log when this command is initialized */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "RunMotorWhileConditonMet init", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("RunMotorWhileConditonMet init", this.getClass());
  }

  /** Run the motor */
  @Override
  public void execute() {
    subsystem.turnMotorOn();
  }

  /**
   * Stop when the condition is met.
   *
   * @return true if the condition is met, false otherwise.
   */
  @Override
  public boolean isFinished() {
    return subsystem.isConditionTrueCached();
  }

  /** Stop the motor and log that the command has ended. */
  @Override
  public void end(boolean interrupted) {
    // Stop the motor when we meet the condition.
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "RunMotorWhileConditonMet interrupted, stopping climb.",
          this.getClass().getSimpleName(),
          EventImportance.kNormal);
    }
    subsystem.turnMotorOff();
    Shuffleboard.addEventMarker(
        "RunMotorWhileConditonMet end", this.getClass().getSimpleName(), EventImportance.kNormal);
  }
}
