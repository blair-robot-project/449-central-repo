package org.usfirst.frc.team449.robot.commands.general;

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

  @JsonCreator
  private PlaceholderCommand() {}

  /**
   * Returns a default instance that does nothing when executed.
   *
   * @return a default instance
   */
  @JsonCreator
  public static PlaceholderCommand getInstance() {
    return instance;
  }

  /**
   * Whether the command has finished. Once a command finishes, the scheduler will call its end()
   * method and un-schedule it.
   *
   * @return whether the command has finished.
   */
  @Override
  public boolean isFinished() {
    return true;
  }

  /**
   * Specifies the set of subsystems used by this command. Two commands cannot use the same
   * subsystem at the same time. If the command is scheduled as interruptible and another command is
   * scheduled that shares a requirement, the command will be interrupted. Else, the command will
   * not be scheduled. If no subsystems are required, return an empty set.
   *
   * <p>Note: it is recommended that user implementations contain the requirements as a field, and
   * return that field here, rather than allocating a new set every time this is called.
   *
   * @return the set of subsystems that are required
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
   * @return {@literal false}
   */
  @Override
  public boolean equals(final Object obj) {
    return false;
  }
}
