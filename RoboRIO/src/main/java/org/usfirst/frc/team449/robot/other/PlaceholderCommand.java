package org.usfirst.frc.team449.robot.other;

import com.fasterxml.jackson.annotation.JsonCreator;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;

import java.util.Set;

/**
 * A command that does nothing when run. Used if a command is required somewhere but no side effects
 * are desired.
 *
 * <p>For a command that prints something to the console every time that it is run, use {@link
 * edu.wpi.first.wpilibj2.command.PrintCommand}
 */
public class PlaceholderCommand implements Command {
  /** The singleton instance. */
  private static final PlaceholderCommand instance = new PlaceholderCommand();

  private PlaceholderCommand() {}

  /**
   * Returns the singleton instance, which does nothing when executed.
   *
   * @return the singleton instance
   */
  @JsonCreator
  public static PlaceholderCommand getInstance() {
    return instance;
  }

  /**
   * Returns {@code true}
   *
   * @return {@code true}
   */
  @Override
  public boolean isFinished() {
    return true;
  }

  /**
   * Returns an empty set.
   *
   * @return an empty set
   */
  @Override
  public Set<Subsystem> getRequirements() {
    return Set.of();
  }

  /**
   * Gets around WPILib's no-more-than-one-group-per-command rule by never being equal to any other
   * object.
   *
   * @param obj the reference object with which to compare
   * @return {@code false}
   */
  @Override
  public boolean equals(final Object obj) {
    return false;
  }
}
