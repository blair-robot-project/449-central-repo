package org.usfirst.frc.team449.robot.other.strangeCommands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.components.booleanSuppliers.BooleanSupplierUpdatable;
import org.usfirst.frc.team449.robot.other.PlaceholderCommand;

import java.util.Objects;
import java.util.function.BooleanSupplier;

/**
 * {@link ConditionalPerpetualCommand} but only runs a command when the specified condition changes.
 *
 * <p>The condition is not monitored while a command is being run as a result of a change.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ConditionalPerpetualCommandChangeBased extends ConditionalPerpetualCommand {
  /**
   * Default constructor
   *
   * @param afterBecomingTrue the Command to execute if BooleanSupplier returns begins returning
   *     {@code true}
   * @param afterBecomingFalse the Command to execute if BooleanSupplier returns begins returning
   *     {@code true}
   * @param booleanSupplier a method for determining which command to run
   */
  @JsonCreator
  public ConditionalPerpetualCommandChangeBased(
      @NotNull @JsonProperty(required = true) final BooleanSupplierUpdatable booleanSupplier,
      @Nullable final Command afterBecomingTrue,
      @Nullable final Command afterBecomingFalse) {
    //      @Nullable final Double pollingInterval) { TODO: Not sure if polling interval logic
    // works.
    super(
        // The command to run when the condition changes.
        new ConditionalCommand(
            Objects.requireNonNullElse(afterBecomingTrue, PlaceholderCommand.getInstance()),
            Objects.requireNonNullElse(afterBecomingFalse, PlaceholderCommand.getInstance()),
            booleanSupplier),

        // Don't do anything when the condition isn't changing.
        null,

        // A supplier that tests for whether the condition has changed.
        new BooleanSupplier() {
          //          private long lastPollTime;
          private boolean lastState;

          @Override
          public boolean getAsBoolean() {
            //            final var now = Clock.currentTimeMillis();
            //            if (pollingInterval != null && now - this.lastPollTime < pollingInterval)
            // return false;

            //            this.lastPollTime = now;
            booleanSupplier.update();

            final boolean current = booleanSupplier.getAsBoolean();
            final boolean stateChanged = current != this.lastState;
            this.lastState = current;
            return stateChanged;
          }
        });
  }
}
