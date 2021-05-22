package org.usfirst.frc.team449.robot.other.strangeCommands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.other.PlaceholderCommand;

import java.util.Objects;
import java.util.function.BooleanSupplier;

/**
 * Whenever it is executed, either continues running a command that it is already running or begins
 * running one of the two given commands based on the current state of the given condition.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ConditionalPerpetualCommand extends ConditionalCommand {
  /**
   * Default constructor
   *
   * @param onTrue The Command to execute if BooleanSupplier returns true.
   * @param onFalse The Command to execute if BooleanSupplier returns false.
   * @param booleanSupplier A method for determining which command to run.
   */
  @JsonCreator
  public ConditionalPerpetualCommand(
      @Nullable final Command onTrue,
      @Nullable final Command onFalse,
      @NotNull @JsonProperty(required = true) final BooleanSupplier booleanSupplier) {
    super(
        Objects.requireNonNullElse(onTrue, PlaceholderCommand.getInstance()),
        Objects.requireNonNullElse(onFalse, PlaceholderCommand.getInstance()),
        booleanSupplier);
  }

  /**
   * Calls {@link ConditionalCommand#initialize()} (which queries the condition) and then {@link
   * ConditionalCommand#execute()}.
   */
  @Override
  public void execute() {
    // TODO This might be janky.
    if (super.isFinished()) {
      super.end(false);
      super.initialize();
    }
    super.execute();
  }

  /**
   * Returns {@code false}
   *
   * @return {@code false}
   */
  @Override
  public boolean isFinished() {
    return false;
  }
}
