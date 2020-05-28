package org.usfirst.frc.team449.robot.commands.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A command that runs any number of {@link Runnable} objects once or every tick. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class RunRunnables extends CommandBase {

  /** The runnables to run. */
  @NotNull private final Runnable[] runnables;

  private final boolean keepRunning;

  /**
   * Default constructor
   *
   * @param keepRunning Whether to keep running after the first tick.
   * @param runnables The runnables to run.
   */
  @JsonCreator
  public RunRunnables(
      @Nullable final Boolean keepRunning,
      @NotNull @JsonProperty(required = true) final Runnable... runnables) {
    this.runnables = runnables;
    this.keepRunning = keepRunning != null ? keepRunning : true;
  }

  /** Log on init */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "RunRunnables init", this.getClass().getSimpleName(), EventImportance.kNormal);
  }

  /** Run all the runnables in the order they were given. */
  @Override
  public void execute() {
    for (final Runnable runnable : this.runnables) {
      runnable.run();
    }
  }

  /**
   * If {@code keepRunning} was specified to be true, false; otherwise, whether the command has
   * finished running.
   *
   * @return whether the command has finished running
   */
  @Override
  public boolean isFinished() {
    return !this.keepRunning;
  }

  /** Log on exit. */
  @Override
  public void end(final boolean interrupted) {
    if (interrupted) {
      Shuffleboard.addEventMarker(
          "RunRunnables interrupted", this.getClass().getSimpleName(), EventImportance.kNormal);
    }
    Shuffleboard.addEventMarker(
        "RunRunnables end", this.getClass().getSimpleName(), EventImportance.kNormal);
  }
}
