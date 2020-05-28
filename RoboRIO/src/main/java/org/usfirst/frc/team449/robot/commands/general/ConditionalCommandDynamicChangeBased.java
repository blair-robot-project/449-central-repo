package org.usfirst.frc.team449.robot.commands.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.function.BooleanSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.other.Clock;

/** Runs one of two commands the first tick on which the given condition becomes true. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ConditionalCommandDynamicChangeBased extends ConditionalCommandDynamic {
  /**
   * Default constructor
   *
   * @param afterBecomingTrue the Command to execute if BooleanSupplier returns begins returning
   *     true
   * @param afterBecomingFalse the Command to execute if BooleanSupplier returns begins returning
   *     false
   * @param booleanSupplier a method for determining which command to run
   */
  @JsonCreator
  public ConditionalCommandDynamicChangeBased(
      @Nullable final InstantCommand afterBecomingTrue,
      @Nullable final InstantCommand afterBecomingFalse,
      @NotNull @JsonProperty(required = true) final BooleanSupplierUpdatable booleanSupplier,
      @Nullable final Subsystem[] requiredSubsystems,
      @Nullable final Double pollingInterval) {

    super(
        new InstantCommand(
            () -> {
              final Command selected =
                  booleanSupplier.getAsBoolean() ? afterBecomingTrue : afterBecomingFalse;

              if (selected != null) {
                selected.initialize();
                selected.execute();
                selected.end(false);
              }
            }),

        // Don't do anything when the condition isn't changing.
        null,
        new BooleanSupplier() {
          private long lastPollTime;
          private boolean lastState;

          @Override
          public boolean getAsBoolean() {
            final var now = Clock.currentTimeMillis();
            if (pollingInterval != null && now - this.lastPollTime < pollingInterval) return false;

            this.lastPollTime = now;
            booleanSupplier.update();

            final boolean current = booleanSupplier.getAsBoolean();
            final boolean stateChanged = current != this.lastState;
            this.lastState = current;
            return stateChanged;
          }
        },
        requiredSubsystems);
  }
}
