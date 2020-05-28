package org.usfirst.frc.team449.robot.commands.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.PerpetualCommand;
import edu.wpi.first.wpilibj2.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Runs another command in perpetuity, ignoring that command's end conditions and reinitializing it
 * when it finishes.
 *
 * @see PerpetualCommand
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class PerpetualCommandReinitializing extends PerpetualCommand {
  /**
   * Creates a new PerpetualCommand. Will run another command in perpetuity, ignoring that command's
   * end conditions, unless this command itself is interrupted.
   *
   * @param command the command to run perpetually
   * @param requiredSubsystems the list of subsystems that this command requires
   */
  @JsonCreator
  public PerpetualCommandReinitializing(
      @NotNull @JsonProperty(required = true) final Command command,
      @Nullable final Subsystem[] requiredSubsystems) {
    // TODO: We should requireNonNull all @Nullable parameters because map errors can cause them to
    // be null and result in weird Jackson exceptions.
    super(command);
    if (requiredSubsystems != null) this.addRequirements(requiredSubsystems);
  }

  @Override
  public void execute() {
    // TODO: How much less jank is this compared to ConditionalCommandDynamic?
    if (this.m_command.isFinished()) {
      this.m_command.end(false);
      this.m_command.initialize();
    }
    super.execute();
  }
}
