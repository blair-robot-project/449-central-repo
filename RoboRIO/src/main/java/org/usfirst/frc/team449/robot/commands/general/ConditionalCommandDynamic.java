package org.usfirst.frc.team449.robot.commands.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A ConditionalCommand that takes a lambda for determining which command to run and that checks its
 * condition every time it executes.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ConditionalCommandDynamic extends ConditionalCommand {
  /**
   * Default constructor
   *
   * @param onTrue The Command to execute if BooleanSupplier returns true.
   * @param onFalse The Command to execute if BooleanSupplier returns false.
   * @param booleanSupplier A method for determining which command to run.
   */
  @JsonCreator
  public ConditionalCommandDynamic(
      @Nullable final Command onTrue,
      @Nullable final Command onFalse,
      @NotNull @JsonProperty(required = true) final BooleanSupplier booleanSupplier,
      @Nullable final Subsystem[] requiredSubsystems) {
    super(
        Objects.requireNonNullElse(onTrue, PlaceholderCommand.getInstance()),
        Objects.requireNonNullElse(onFalse, PlaceholderCommand.getInstance()),
        booleanSupplier);
    if (requiredSubsystems != null) this.addRequirements(requiredSubsystems);
  }

  /**
   * Calls {@link ConditionalCommand#initialize()} (which queries the condition) and then {@link
   * ConditionalCommand#execute()}.
   */
  @Override
  public void execute() {
    // TODO This is janky.
    if (this.isFinished()) {
      super.end(false);
      super.initialize();
    }
    super.execute();
  }
}
