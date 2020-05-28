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

/** Run a BinaryMotor until a {@link SubsystemConditional}'s condition is met. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class RunMotorUntilConditionMet<T extends Subsystem & SubsystemBinaryMotor>
    extends CommandBase {

  /** The subsystem to execute this command on */
  @NotNull private final T subsystem;

  /** The conditional subsystem this command uses. */
  @NotNull private final SubsystemConditional subsystemConditional;

  /**
   * Default constructor
   *
   * @param subsystem The subsystem to execute this command on.
   * @param subsystemConditional The conditional subsystem this command uses.
   */
  @JsonCreator
  public RunMotorUntilConditionMet(
      @NotNull @JsonProperty(required = true) final T subsystem,
      @NotNull @JsonProperty(required = true) final SubsystemConditional subsystemConditional) {
    this.addRequirements(subsystem);
    this.subsystem = subsystem;
    this.subsystemConditional = subsystemConditional;
  }

  /** Log when this command is initialized */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "RunMotorUntilConditionMet init", this.getClass().getSimpleName(), EventImportance.kNormal);
    this.subsystem.turnMotorOn();
    // Logger.addEvent("RunMotorUntilConditionMet init", this.getClass());
  }

  /**
   * Stop when the condition is met.
   *
   * @return true if the condition is met, false otherwise.
   */
  @Override
  public boolean isFinished() {
    return this.subsystemConditional.isConditionTrueCached();
  }

  /** Stop the motor and log that the command has ended. */
  @Override
  public void end(final boolean interrupted) {
    // Stop the motor when we meet the condition.
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "RunMotorUntilConditionMet interrupted, stopping climb.",
          this.getClass().getSimpleName(),
          EventImportance.kNormal);
    }
    this.subsystem.turnMotorOff();
    Shuffleboard.addEventMarker(
        "RunMotorUntilConditionMet end", this.getClass().getSimpleName(), EventImportance.kNormal);
  }
}
